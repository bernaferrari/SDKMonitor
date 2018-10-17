package com.bernaferrari.sdkmonitor

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.extensions.setTextAsync
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_navigation_item.*
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext


class RowItem(val app: App, val sdkVersion: Int, val lastUpdate: Long) : Item(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun unbind(holder: ViewHolder) {
        job.cancel()
        holder.icon.setImageDrawable(null)
        super.unbind(holder)
    }

    private var drawable: Drawable? = null

    override fun getLayout() = R.layout.row_navigation_item

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.label.setTextAsync(app.title)

        viewHolder.card.setCardBackgroundColor(app.backgroundColor)
        viewHolder.view.background = ColorDrawable(app.backgroundColor.darken)

        job = Job()
        launch {
            viewHolder.minSdk.setTextAsync(sdkVersion.toString())
            viewHolder.lastUpdate.setTextAsync(lastUpdate.convertTimestampToDate())

            drawable = null
            updateDrawable()
            viewHolder.icon?.setImageDrawable(drawable)
        }
    }

    private suspend inline fun updateDrawable() {
        if (drawable == null) {
            drawable = withContext(Dispatchers.IO) { AppManager.getIconFromId(app.packageName) }
        }
    }
}
