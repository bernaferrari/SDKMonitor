package com.bernaferrari.sdkmonitor

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import androidx.core.text.bold
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.extensions.setTextAsync
import com.bumptech.glide.Glide
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

        // Suppose id = 1111 and name = neil (just what you want).
        val s = SpannableStringBuilder()
            .append("min ")
            .bold { append("28") }
            .append(" target ")
            .bold { append("28") }

        viewHolder.minSdk.setTextAsync(s)

        viewHolder.card.setCardBackgroundColor(snap.backgroundColor)
        viewHolder.view.background = ColorDrawable(snap.backgroundColor.darken)

        job = Job()

        if (drawable == null) {
            launch {
                updateDrawable()
                loadGlideInto(viewHolder)
            }
        } else {
            loadGlideInto(viewHolder)
        }
    }

    private suspend inline fun updateDrawable() {
        if (drawable == null) {
            drawable = withContext(Dispatchers.IO) { AppManager.getIconFromId(snap.packageName) }
        }
    }

    private fun loadGlideInto(viewHolder: ViewHolder) {
        // Using the application context avoids
        // IllegalArgumentException: You cannot start a load for a destroyed activity
        // which might happen when the user is scrolling and closes the app.
        // I believe the cost of using AppContext here is possibly less than checking if the
        // context is alive on every interaction.

        Glide.with(Injector.get().appContext())
            .load(drawable)
            .into(viewHolder.icon)
    }
}
