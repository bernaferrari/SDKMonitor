package com.bernaferrari.sdkmonitor.main

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.airbnb.mvrx.*
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.core.simpleController
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.details.DetailsDialog
import com.bernaferrari.sdkmonitor.emptyContent
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.onTextChanged
import com.bernaferrari.sdkmonitor.util.InsetDecoration
import com.bernaferrari.sdkmonitor.util.hideKeyboard
import com.bernaferrari.sdkmonitor.util.hideKeyboardWhenNecessary
import com.bernaferrari.sdkmonitor.views.MainRowModel_
import com.bernaferrari.sdkmonitor.views.loadingRow
import com.bernaferrari.sdkmonitor.views.mainRow
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class AppVersion(
    val app: App,
    val sdkVersion: Int,
    val lastUpdateTime: String
)

data class AppDetails(val title: String, val subtitle: String)

data class MainState(val listOfItems: Async<List<AppVersion>> = Loading()) : MvRxState

class MainFragment : BaseMainFragment() {

    private val viewModel: MainRxViewModel by activityViewModel()

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

    override fun epoxyController() = simpleController(viewModel) { state ->

        when (state.listOfItems) {
            is Loading ->
                loadingRow { id("loading") }
            else -> {
                if (state.listOfItems()?.isEmpty() == true) {
                    val label = if (state.listOfItems is Fail) {
                        state.listOfItems.error.localizedMessage
                    } else {
                        getString(R.string.empty_search)
                    }

                    emptyContent {
                        this.id("empty")
                        this.label(label)
                    }
                }
            }
        }

        val colorPrefs = Injector.get().sharedPrefs().getBoolean("colorBySdk", true)

        state.listOfItems()?.forEach {
            mainRow {
                id(it.app.packageName)
                this.app(it)

                val color = if (colorPrefs) {
                    it.sdkVersion.apiToColor()
                } else {
                    it.app.backgroundColor
                }

                this.cardColor(color)

                this.clickListener { _ ->
                    DetailsDialog.show(requireActivity(), it.app)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
