package com.bernaferrari.sdkmonitor.main

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.onTextChanged
import com.bernaferrari.sdkmonitor.extensions.toDpF
import com.bernaferrari.sdkmonitor.extensions.viewModelProvider
import com.bernaferrari.sdkmonitor.ui.InsetDecoration
import com.bernaferrari.sdkmonitor.util.ViewModelFactory
import com.bernaferrari.sdkmonitor.util.hideKeyboard
import com.bernaferrari.sdkmonitor.util.hideKeyboardWhenNecessary
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

class MainFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private lateinit var model: MainViewModel
    private val disposable = CompositeDisposable()

    private val itemDecorator by lazy {
        InsetDecoration(
            resources.getDimensionPixelSize(R.dimen.right_padding_for_fast_scroller),
            false,
            true
        )
    }

    private val controller = MainController()

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

        model = viewModelProvider(ViewModelFactory.getInstance())

        if (model.itemsList.isEmpty()) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        } else {
            progressBar.isVisible = false
            queryInput.hint = "Search ${model.allItems.size} apps.."
        }

        val linearLayoutManager = LinearLayoutManager(context).apply {
            initialPrefetchItemCount = 8
        }

        recycler.setHasFixedSize(true)
        recycler.layoutManager = linearLayoutManager
        recycler.adapter = controller.adapter
        recycler.addItemDecoration(itemDecorator)

        setupFastScroller(linearLayoutManager)

        filter.setOnClickListener {
            //            MaterialDialog(requireContext())
        }

        controller.onClick = { model, v ->

        }

        setupDataAndSearch()

        hideKeyboardWhenNecessary(
            requireActivity(),
            inputMethodManager,
            recycler,
            queryInput
        )
    }

    private fun setupDataAndSearch() {

        var work: Job? = null
        val cornerRadius = 8.toDpF(resources)

        disposable += model.getFlowableList(cornerRadius)
            .subscribe {
                progressBar.isVisible = model.allItems.isEmpty()

                if (model.allItems.isNotEmpty()) {
                    queryInput.hint = "Search ${model.allItems.size} apps.."
                }

                // if work is active, it means user is searching for something, so the
                // other disposable will be called.
                if (work?.isActive != true) {
                    controller.setData(model.allItems)
                }
            }

        queryClear.setOnClickListener { queryInput.setText("") }

        queryInput.onTextChanged {
            queryClear.isVisible = it.isNotEmpty()
            work?.cancel()
            work = launch {
                // If the user types anything before data has loaded, this will
                // delay and try again until it is available or the user types
                // another thing.
                // Without this, the input would be ignored while data is loading.
                while (model.allItems.isEmpty()) delay(200)
                model.relay.accept(it.toString())
            }
        }

        disposable += model.getFilteredList().subscribe { controller.setData(it) }
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
                println("modelListSize: ${model.itemsList.count()}")
                // or fetch the section at [position] from your database
                val letter = model.itemsList[it].app.title.substring(0, 1)
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
