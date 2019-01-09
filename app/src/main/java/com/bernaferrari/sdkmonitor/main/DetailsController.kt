package com.bernaferrari.sdkmonitor.main

import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.Typed2EpoxyController
import com.bernaferrari.sdkmonitor.*
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate

internal class DetailsController : Typed2EpoxyController<List<AppDetails>, List<Version>>() {

    override fun buildModels(apps: List<AppDetails>, versions: List<Version>) {

        apps.forEach { app ->
            detailsText {
                id(app.title)
                this.title(app.title)
                this.subtitle(app.subtitle)
            }
        }

        textSeparator {
            id("separator")
            this.label(Injector.get().appContext().getString(R.string.target_history))
        }

        val historyModels = mutableListOf<SdkHistoryBindingModel_>()

        versions.forEach {
            historyModels.add(
                SdkHistoryBindingModel_()
                    .id(it.targetSdk)
                    .targetSDKVersion(it.targetSdk.toString())
                    .title(it.lastUpdateTime.convertTimestampToDate())
                    .version("V. Code: ${it.version}")
                    .versionName("V. Name: ${it.versionName}")
            )
        }

        CarouselModel_()
            .id("carousel")
            .models(historyModels)
            .addTo(this)
    }
}
