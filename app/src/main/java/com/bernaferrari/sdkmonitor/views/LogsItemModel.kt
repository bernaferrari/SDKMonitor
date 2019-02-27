package com.bernaferrari.sdkmonitor.views

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.StringRes
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.bernaferrari.sdkmonitor.BR
import com.bernaferrari.sdkmonitor.core.AppManager
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@EpoxyModelClass(layout = com.bernaferrari.sdkmonitor.R.layout.epoxy_layout_logs_item)
abstract class LogsItemModel : DataBindingEpoxyModel(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    private var drawable: Drawable? = null

    @EpoxyAttribute
    @StringRes
    var packageName: String = ""

    @EpoxyAttribute
    @StringRes
    var title: String = ""

    @EpoxyAttribute
    @StringRes
    var subtitle: String = ""

    @EpoxyAttribute
    @StringRes
    var targetSDKVersion: String = ""

    @EpoxyAttribute
    @StringRes
    var targetSDKDescription: String = ""

    @EpoxyAttribute
    @StringRes
    var apiColor: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClick: View.OnClickListener? = null

    override fun bind(holder: DataBindingHolder) {
        super.bind(holder)

        job = Job()
        launch {
            updateDrawable()
            holder.dataBinding.setVariable(BR.image, drawable)
        }
    }

    private suspend inline fun updateDrawable() {
        if (drawable == null) {
            drawable = withContext(Dispatchers.IO) {
                AppManager.getIconFromId(packageName)
            }
        }
    }

    override fun unbind(holder: DataBindingHolder) {
        job.cancel()
        super.unbind(holder)
    }
}