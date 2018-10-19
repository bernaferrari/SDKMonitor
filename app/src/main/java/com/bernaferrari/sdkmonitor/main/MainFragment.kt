package com.bernaferrari.sdkmonitor.main

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.fragmentViewModel
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.core.MvRxEpoxyController
import com.bernaferrari.sdkmonitor.core.simpleController
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.extensions.onTextChanged
import com.bernaferrari.sdkmonitor.rowItemBinding
import com.bernaferrari.sdkmonitor.ui.InsetDecoration
import com.bernaferrari.sdkmonitor.util.hideKeyboard
import com.bernaferrari.sdkmonitor.util.hideKeyboardWhenNecessary
import com.bernaferrari.sdkmonitor.views.LoadingRowModel_
import com.bernaferrari.sdkmonitor.views.loadingRow
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

class AppVersion(
    val app: App,
    val sdkVersion: Int,
    val lastUpdateTime: String,
    val cornerRadius: Float
)

data class MainState(
    /** We use this request to store the list of all jokes */
    val listOfItems: List<AppVersion> = emptyList()
) : MvRxState

class MainFragment : BaseMvRxFragment(), CoroutineScope {

    private lateinit var model: MainViewModel

    private val viewModel: MainRxViewModel by fragmentViewModel()

    val epoxyController by lazy { epoxyController() }

    fun epoxyController(): MvRxEpoxyController = simpleController(viewModel) { state ->

        if (state.listOfItems.isEmpty()) {
            loadingRow { id("loading") }
        }

//        state.listOfItems.forEach {
//            button {
//                id(it.app.packageName)
//
//                this.app(it)
//
//                val topShape = createShape(it.app.backgroundColor, false, it.cornerRadius)
//                val bottomShape = createShape(it.app.backgroundColor.darken, true, it.cornerRadius)
//
//                this.bottomShape(bottomShape)
//                this.topShape(topShape)
//            }
//        }

        state.listOfItems.forEach {
            rowItemBinding {
                id(it.app.packageName)

                this.appversion(it)

                val topShape = createShape(it.app.backgroundColor, false, it.cornerRadius)
                val bottomShape = createShape(it.app.backgroundColor.darken, true, it.cornerRadius)

                this.bottomShape(bottomShape)
                this.topShape(topShape)
            }
        }
    }

    private fun createShape(color: Int, isBottom: Boolean, cornerRadius: Float): Drawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadii = if (isBottom) {
            floatArrayOf(0f, 0f, 0f, 0f, cornerRadius, cornerRadius, cornerRadius, cornerRadius)
        } else {
            floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f, 0f, 0f)
        }
        shape.setColor(color)
        return shape
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private val disposable = CompositeDisposable()

    private val itemDecorator by lazy {
        InsetDecoration(
            resources.getDimensionPixelSize(R.dimen.right_padding_for_fast_scroller),
            false,
            true
        )
    }

//    private val controller = MainController()

    private val inputMethodManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.getSystemService<InputMethodManager>()
        } else {
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        } ?: throw Exception("null activity. Can't bind inputMethodManager")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.itemsList.isEmpty()) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        } else {
//            progressBar.isVisible = false
            queryInput.hint = "Search ${viewModel.allItems.size} apps.."
        }

        val linearLayoutManager = LinearLayoutManager(context).apply {
            initialPrefetchItemCount = 8
        }

        recycler.layoutManager = linearLayoutManager
        recycler.setController(epoxyController)
        recycler.addItemDecoration(itemDecorator)
        setupFastScroller(linearLayoutManager)

        filter.setOnClickListener {
            //            MaterialDialog(requireContext())
        }

        setupDataAndSearch()

        hideKeyboardWhenNecessary(
            requireActivity(),
            inputMethodManager,
            recycler,
            queryInput
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    override fun invalidate() {
        recycler.requestModelBuild()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }

    private fun setupDataAndSearch() {

        var work: Job? = null

        viewModel.relay.accept(queryInput.text.toString())

        queryClear.setOnClickListener { queryInput.setText("") }

        queryInput.onTextChanged {
            queryClear.isVisible = it.isNotEmpty()
            work?.cancel()
            work = launch {
                // If the user types anything before data has loaded, this will
                // delay and try again until it is available or the user types
                // another thing.
                // Without this, the input would be ignored while data is loading.
                while (viewModel.allItems.isEmpty()) delay(200)
                viewModel.relay.accept(it.toString())
            }
        }
    }

    override fun onDestroy() {
        println("onDestroy!!")
        disposable.clear()
        coroutineContext.cancel()
        super.onDestroy()
    }

    private fun setupFastScroller(linearLayoutManager: LinearLayoutManager) {
        fastscroller.setupWithRecyclerView(
            recyclerView = recycler,
            useDefaultScroller = false,
            getItemIndicator = {
                println("modelListSize: ${viewModel.itemsList.count()}")
                // or fetch the section at [position] from your database

                if (epoxyController.adapter.getModelAtPosition(it) is LoadingRowModel_) {
                    return@setupWithRecyclerView null
                }

                val letter = viewModel.itemsList[it].app.title.substring(0, 1)
                val index = if (letter[0].isDigit()) "#" else letter.toUpperCase()

                FastScrollItemIndicator.Text(index) // Return a text indicator
            }
        )

        val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
        }

        fastscroller.itemIndicatorSelectedCallbacks += object :
            FastScrollerView.ItemIndicatorSelectedCallback {
            override fun onItemIndicatorSelected(
                indicator: FastScrollItemIndicator,
                indicatorCenterY: Int,
                itemPosition: Int
            ) {
                recycler.stopScroll()
                inputMethodManager.hideKeyboard(queryInput)
                smoothScroller.targetPosition = itemPosition
                linearLayoutManager.startSmoothScroll(smoothScroller)
            }
        }

        fastscroller_thumb.setupWithFastScroller(fastscroller)
    }
}
