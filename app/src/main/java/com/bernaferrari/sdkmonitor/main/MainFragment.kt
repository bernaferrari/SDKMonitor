package com.bernaferrari.sdkmonitor.main

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.*
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.core.MvRxEpoxyController
import com.bernaferrari.sdkmonitor.core.simpleController
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.emptyContent
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.extensions.inflate
import com.bernaferrari.sdkmonitor.extensions.onTextChanged
import com.bernaferrari.sdkmonitor.extensions.toDpF
import com.bernaferrari.sdkmonitor.settings.SettingsFragment
import com.bernaferrari.sdkmonitor.util.InsetDecoration
import com.bernaferrari.sdkmonitor.util.hideKeyboard
import com.bernaferrari.sdkmonitor.util.hideKeyboardWhenNecessary
import com.bernaferrari.sdkmonitor.views.MainRowModel_
import com.bernaferrari.sdkmonitor.views.loadingRow
import com.bernaferrari.sdkmonitor.views.mainRow
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class AppVersion(
    val app: App,
    val sdkVersion: Int,
    val lastUpdateTime: String
)

data class AppDetails(val title: String, val subtitle: String)

data class MainState(val listOfItems: Async<List<AppVersion>> = Loading()) : MvRxState

class MainFragment : BaseMainFragment() {

    private val viewModel: MainRxViewModel by fragmentViewModel()

    private val standardItemDecorator by lazy {
        val isRightToLeft =
            TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL

        // the padding should be on right side, but because of RTL layouts, it can change.
        InsetDecoration(
            resources.getDimensionPixelSize(R.dimen.right_padding_for_fast_scroller),
            isRightToLeft,
            !isRightToLeft
        )
    }

    private val inputMethodManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.getSystemService<InputMethodManager>()
        } else {
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        } ?: throw Exception("null activity. Can't bind inputMethodManager")
    }

    private val showDialog: ((App) -> (Unit)) = {
        val customView = parentLayout.inflate(R.layout.details_fragment)

        val bottomDialog = BottomSheetDialog(requireContext()).also { btn ->
            btn.setContentView(customView)
            btn.show()
        }

        customView.findViewById<ImageView>(R.id.closecontent).setOnClickListener { _ ->
            bottomDialog.dismiss()
        }

        customView.findViewById<TextView>(R.id.titlecontent).text = it.title

        customView.findViewById<EpoxyRecyclerView>(R.id.recycler).also { epoxyRecycler ->

            val detailsController = DetailsController()
            epoxyRecycler?.setController(detailsController)

            runBlocking {
                val packageName = it.packageName
                val data = viewModel.fetchAppDetails(packageName)
                val versions = viewModel.fetchAllVersions(packageName)
                detailsController.setData(data, versions)
            }
        }
    }

    override fun epoxyController(): MvRxEpoxyController = simpleController(viewModel) { state ->

        when (state.listOfItems) {
            is Loading ->
                loadingRow { id("loading") }
            is Fail ->
                emptyContent {
                    this.id("error")
                    this.label(state.listOfItems.error.localizedMessage)
                }
            is Success -> {
                if (state.listOfItems()?.isEmpty() == true) {
                    emptyContent {
                        this.id("empty result")
                        this.label(getString(R.string.empty_search))
                    }
                }
            }
        }

        val cornerRadius = 8.toDpF(resources)

        state.listOfItems()?.forEach {
            mainRow {
                id(it.app.packageName)

                this.app(it)

                val topShape = createShape(it.app.backgroundColor, false, cornerRadius)
                val bottomShape = createShape(it.app.backgroundColor.darken, true, cornerRadius)

                this.bottomShape(bottomShape)
                this.topShape(topShape)

                this.clickListener { _ -> showDialog.invoke(it.app) }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.itemsList.isEmpty()) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        settings.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(SettingsFragment(), "settings").commit()
        }

        recycler.addItemDecoration(standardItemDecorator)
        setupFastScroller(recycler.layoutManager as? LinearLayoutManager)

        setupDataAndSearch()

        disposableManager += viewModel.showProgressRelay.observeOn(AndroidSchedulers.mainThread())
            .subscribe { if (!it) swipeToRefresh.isRefreshing = false }

        disposableManager += viewModel.maxListSize.observeOn(AndroidSchedulers.mainThread())
            .subscribe { queryInput.hint = "Search $it apps.." }

        swipeToRefresh.setOnRefreshListener { viewModel.updateAll() }

        hideKeyboardWhenNecessary(
            requireActivity(),
            inputMethodManager,
            recycler,
            queryInput
        )
    }

    private fun setupDataAndSearch() {

        var work: Job? = null

        viewModel.inputRelay.accept(queryInput.text.toString())

        queryClear.setOnClickListener { queryInput.setText("") }

        queryInput.onTextChanged {
            queryClear.isVisible = it.isNotEmpty()
            work?.cancel()
            work = launch {
                // If the user types anything before data has loaded, this will
                // delay and try again until it is available or the user types
                // another thing.
                // Without this, the input would be ignored while data is loading.
                while (!viewModel.hasLoaded) delay(200)
                viewModel.inputRelay.accept(it.toString())
            }
        }
    }

    private fun setupFastScroller(llm: LinearLayoutManager?) {
        val linearLayoutManager = llm ?: return

        fastscroller.setupWithRecyclerView(
            recyclerView = recycler,
            useDefaultScroller = false,
            getItemIndicator = {

                if (epoxyController.adapter.getModelAtPosition(it) !is MainRowModel_) {
                    return@setupWithRecyclerView null
                }

                // it might be null when model is updated really fast
                val itemFromList = viewModel.itemsList.getOrNull(it)
                        ?: return@setupWithRecyclerView null

                val letter = itemFromList.app.title.substring(0, 1)
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
