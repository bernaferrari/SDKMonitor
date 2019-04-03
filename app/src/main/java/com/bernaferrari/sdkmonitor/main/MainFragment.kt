package com.bernaferrari.sdkmonitor.main

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.view.isVisible
import com.airbnb.mvrx.*
import com.bernaferrari.base.mvrx.simpleController
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.details.DetailsDialog
import com.bernaferrari.sdkmonitor.emptyContent
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.loadingRow
import com.bernaferrari.sdkmonitor.util.InsetDecoration
import com.bernaferrari.sdkmonitor.views.MainRowModel_
import com.bernaferrari.sdkmonitor.views.mainRow
import com.bernaferrari.ui.dagger.DaggerBaseSearchFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import java.util.*
import javax.inject.Inject

class AppVersion(
    val app: App,
    val sdkVersion: Int,
    val lastUpdateTime: String
)

data class AppDetails(val title: String, val subtitle: String)

data class MainState(val listOfItems: Async<List<AppVersion>> = Loading()) : MvRxState

class MainFragment : DaggerBaseSearchFragment() {

    private val viewModel: MainViewModel by fragmentViewModel()
    @Inject
    lateinit var mainViewModelFactory: MainViewModel.Factory

    override val showKeyboardWhenLoaded = false

    override fun onTextChanged(searchText: String) {
        viewModel.inputRelay.accept(searchText)
    }

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

        viewModel.inputRelay.accept(getInputText())

        val fastScroller = container.inflateFastScroll()

        fastScroller.setupFastScroller(recyclerView, activity) {
            if (getModelAtPos(it) is MainRowModel_) viewModel.itemsList.getOrNull(it) else null
        }

        setInputHint("Loading...")

        disposableManager += viewModel.maxListSize.observeOn(AndroidSchedulers.mainThread())
            .subscribe { setInputHint("Search $it apps..") }

        // observe when order changes
        disposableManager += Injector.get().orderBySdk().observe()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { orderBySdk ->
                fastScroller.isVisible = !orderBySdk

                if (orderBySdk) {
                    recyclerView.removeItemDecoration(standardItemDecorator)
                } else {
                    recyclerView.addItemDecoration(standardItemDecorator)
                }
            }
    }

    override val closeIconRes: Int? = null
}
