package com.bernaferrari.sdkmonitor

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.toBitmap
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.extensions.setTextAsync
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

// This does not require annotations or annotation processing.
// The data class is required to generated equals/hashcode which Epoxy needs for diffing.
// Views are easily declared via property delegates
data class ItemDataClass(
    val app: App, val sdkVersion: Int, val lastUpdateTime: String, val cornerRadius: Float
) : KotlinModel(R.layout.row_navigation_item) {

    val icon by bind<ImageView>(R.id.icon)
    val label by bind<AppCompatTextView>(R.id.label)
    val top_view by bind<View>(R.id.top_view)
    val bottom_view by bind<View>(R.id.bottom_view)
    val minSdk by bind<AppCompatTextView>(R.id.minSdk)
    val lastUpdate by bind<AppCompatTextView>(R.id.lastUpdate)

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

    override fun bind() {

        label.setTextAsync(app.title)
        top_view.background = topShape
        bottom_view.background = bottomShape

        launch {
            minSdk.setTextAsync(sdkVersion.toString())
            lastUpdate.setTextAsync(lastUpdateTime)

            updateDrawable()
            icon.setImageBitmap(drawable)
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
