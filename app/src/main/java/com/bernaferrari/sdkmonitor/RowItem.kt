package com.bernaferrari.sdkmonitor

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import androidx.core.text.bold
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.extensions.setTextAsync
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_navigation_item.*
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext


class RowItem(val snap: App) : Item(), CoroutineScope {

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

        viewHolder.label.setTextAsync(snap.title)

        val s = SpannableStringBuilder()
            .bold { append("28") }

        viewHolder.minSdk.setTextAsync(s)

        viewHolder.card.setCardBackgroundColor(snap.backgroundColor)
        viewHolder.view.background = ColorDrawable(snap.backgroundColor.darken)

        job = Job()
        launch {
            updateDrawable()
            viewHolder.icon?.setImageDrawable(drawable)
        }
    }

    private suspend inline fun updateDrawable() {
        if (drawable == null) {
            drawable = withContext(Dispatchers.IO) { AppManager.getIconFromId(snap.packageName) }
        }
    }
}
