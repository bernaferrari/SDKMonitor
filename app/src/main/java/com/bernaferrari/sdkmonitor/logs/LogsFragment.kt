package com.bernaferrari.sdkmonitor.logs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.airbnb.mvrx.BuildConfig
import com.airbnb.mvrx.activityViewModel
import com.bernaferrari.sdkmonitor.LogsItemBindingModel_
import com.bernaferrari.sdkmonitor.core.RecyclerBaseFragment
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.details.DetailsDialog
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.loadingRow
import com.bernaferrari.sdkmonitor.logsEmptyItem
import com.bernaferrari.sdkmonitor.marquee
import com.bernaferrari.sdkmonitor.views.LogsItemModel_
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogsFragment : RecyclerBaseFragment() {

    private val viewModel: LogsRxViewModel by activityViewModel()

    private val pagingController = TestController()

    override fun epoxyController() = pagingController

    var mapOfApps = mapOf<String, App>()
    var numberOfApps = 0
    var hasLoaded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        recycler.addItemDecoration(standardItemDecorator)

        launch(Dispatchers.Main) {
            mapOfApps = viewModel.getAppList()
            numberOfApps = viewModel.getVersionCount()
            hasLoaded = true

            viewModel.pagedVersion()
                .observe(requireActivity(), Observer {
                    pagingController.submitList(it)
                })
        }
    }

    inner class TestController : PagedListEpoxyController<Version>() {
        override fun buildItemModel(currentPosition: Int, item: Version?): EpoxyModel<*> {

            if (item == null) {
                // this should never happen since placeholders are disabled.
                return LogsItemBindingModel_().id("error")
            }

            val sdk = item.targetSdk

            return LogsItemModel_()
                .id(item.versionId)
                .title(mapOfApps.getValue(item.packageName).title)
                .targetSDKVersion(sdk.toString())
                .targetSDKDescription(sdk.apiToVersion())
                .apiColor(sdk.apiToColor()) // 0xFF9812FF
                .packageName(item.packageName)
                .subtitle(item.lastUpdateTime.convertTimestampToDate())
                .onClick { v ->
                    DetailsDialog.show(requireActivity(), mapOfApps.getValue(item.packageName))
                }
        }

        init {
            isDebugLoggingEnabled = BuildConfig.DEBUG
        }

        override fun addModels(models: List<EpoxyModel<*>>) {

            when {
                models.isNotEmpty() -> marquee {
                    id("header")
                    title("TargetSDK Logs")
                    subtitle("$numberOfApps changes detected")
                }
                numberOfApps == 0 && hasLoaded -> logsEmptyItem { id("empty") }
                else -> loadingRow { id("loading") }
            }

            super.addModels(models)
        }

        override fun onExceptionSwallowed(exception: RuntimeException) {
            throw exception
        }
    }
}
