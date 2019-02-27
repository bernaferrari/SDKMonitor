package com.bernaferrari.sdkmonitor.views

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
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

@EpoxyModelClass(layout = R.layout.main_row_item)
abstract class MainRowModel : EpoxyModelWithHolder<MainRowModel.Holder>(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    @EpoxyAttribute
    var cardColor: Int = 0

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
        holder.cardView.setCardBackgroundColor(cardColor)
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
        super.unbind(holder)
    }

    class Holder : KotlinEpoxyHolder() {
        val label by bind<AppCompatTextView>(R.id.label)
        val icon by bind<ImageView>(R.id.icon)
        val minSdk by bind<AppCompatTextView>(R.id.targetSdk)
        val lastUpdate by bind<AppCompatTextView>(R.id.lastUpdate)
        val cardView by bind<CardView>(R.id.cardView)
        val container by bind<View>(R.id.container)
    }
}