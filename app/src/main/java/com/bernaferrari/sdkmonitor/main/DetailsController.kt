package com.bernaferrari.sdkmonitor.main

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

        versions.forEach {
            sdkHistory {
                id(it.targetSdk)
                this.targetSDKVersion(it.targetSdk.toString())
                this.title(it.lastUpdateTime.convertTimestampToDate())
                this.version("Version: ${it.version}")
                this.versionName("VersionName: ${it.versionName}")
            }
        }
    }
}