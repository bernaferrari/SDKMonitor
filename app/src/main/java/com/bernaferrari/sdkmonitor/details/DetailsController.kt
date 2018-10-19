package com.bernaferrari.sdkmonitor.details

import com.airbnb.epoxy.TypedEpoxyController
import com.bernaferrari.sdkmonitor.main.AppVersion

internal class DetailsController : TypedEpoxyController<List<AppVersion>>(
    com.airbnb.epoxy.EpoxyAsyncUtil.getAsyncBackgroundHandler(),
    com.airbnb.epoxy.EpoxyAsyncUtil.getAsyncBackgroundHandler()
) {

//    var onClick: ((RowItemBindingBindingModel_, View) -> (Unit))? = null

    override fun buildModels(data: List<AppVersion>) {
        data.forEach {
            //            rowItemBinding {
//                id(it.app.packageName)
//
//                this.onClick { model, _, view, _ ->
//                    onClick?.invoke(model, view)
//                }
//
//                this.appversion(it)
//            }
        }
    }
}
