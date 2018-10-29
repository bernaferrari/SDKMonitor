package com.bernaferrari.sdkmonitor.views

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.toBitmap
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.main.AppVersion
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@EpoxyModelClass(layout = R.layout.row_item)
abstract class MainRowModel : EpoxyModelWithHolder<MainRowModel.Holder>(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    @EpoxyAttribute
    lateinit var topShape: Drawable

    @EpoxyAttribute
    lateinit var bottomShape: Drawable

    @EpoxyAttribute
    lateinit var app: AppVersion

    @EpoxyAttribute
    lateinit var version: Version

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: View.OnClickListener? = null

    private var drawable: Bitmap? = null

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.label.text = app.app.title
        holder.top_view.background = topShape
        holder.bottom_view.background = bottomShape
        holder.container.setOnClickListener(clickListener)

        job = Job()
        launch {
            holder.minSdk.text = app.sdkVersion.toString()
            holder.lastUpdate.text = app.lastUpdateTime

            updateDrawable()
            holder.icon.setImageBitmap(drawable)
        }
    }

    private suspend inline fun updateDrawable() {
        if (drawable == null) {
            drawable = withContext(Dispatchers.IO) {
                AppManager.getIconFromId(app.app.packageName)?.toBitmap()
            }
        }
    }

    override fun unbind(holder: Holder) {
        job.cancel()
        holder.icon.setImageDrawable(null)
        holder.top_view.background = null
        holder.bottom_view.background = null
        super.unbind(holder)
    }

    class Holder : KotlinEpoxyHolder() {
        val label by bind<AppCompatTextView>(R.id.label)
        val icon by bind<ImageView>(R.id.icon)
        val minSdk by bind<AppCompatTextView>(R.id.targetSdk)
        val lastUpdate by bind<AppCompatTextView>(R.id.lastUpdate)

        val top_view by bind<View>(R.id.top_view)
        val bottom_view by bind<View>(R.id.bottom_view)
        val container by bind<View>(R.id.container)
    }
}