package com.bernaferrari.sdkmonitor

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.drawable.toBitmap
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.extensions.setTextAsync
import com.bumptech.glide.Glide
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_navigation_item.*
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

class RowItem(val app: App, val sdkVersion: Int, val lastUpdate: String, val cornerRadius: Float) :
    Item(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    override fun unbind(holder: ViewHolder) {
        job.cancel()
        holder.icon.setImageDrawable(null)
        holder.top_view.background = null
        holder.bottom_view.background = null
        super.unbind(holder)
    }

    private fun createShape(color: Int, isBottom: Boolean): Drawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadii = if (isBottom) {
            floatArrayOf(0f, 0f, 0f, 0f, cornerRadius, cornerRadius, cornerRadius, cornerRadius)
        } else {
            floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f, 0f, 0f)
        }
        shape.setColor(color)
        return shape
    }

    private val topShape = createShape(app.backgroundColor, false)
    private val bottomShape = createShape(app.backgroundColor.darken, true)
    private var drawable: Bitmap? = null

    override fun getLayout() = R.layout.row_navigation_item

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.label.setTextAsync(app.title)
        viewHolder.top_view.background = topShape
        viewHolder.bottom_view.background = bottomShape

        job = Job()
        launch {
            viewHolder.minSdk.setTextAsync(sdkVersion.toString())
            viewHolder.lastUpdate.setTextAsync(lastUpdate)

            updateDrawable()
            Glide.with(Injector.get().appContext())
                .load(drawable)
                .into(viewHolder.icon)
//            viewHolder.icon?.setImageBitmap(drawable)
        }
    }

    private suspend inline fun updateDrawable() {
        if (drawable == null) {
            drawable = withContext(Dispatchers.IO) {
                AppManager.getIconFromId(app.packageName)?.toBitmap()
            }
        }
    }
}
