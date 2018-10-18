package com.bernaferrari.sdkmonitor

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import com.airbnb.epoxy.TypedEpoxyController
import com.bernaferrari.sdkmonitor.extensions.darken
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

internal class PhotoController : TypedEpoxyController<List<AppVersion>>(
    com.airbnb.epoxy.EpoxyAsyncUtil.getAsyncBackgroundHandler(),
    com.airbnb.epoxy.EpoxyAsyncUtil.getAsyncBackgroundHandler()
) {

    private fun createShape(color: Int, isBottom: Boolean, cornerRadius: Float): Drawable {
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

    override fun buildModels(data: List<AppVersion>) {
        data.forEach {

            println("user is: ${it.sdkVersion}")

            rowItemBinding {
                id("id is ${it.app.packageName}")

                this.appversion(it)

                val topShape = createShape(it.app.backgroundColor, false, it.cornerRadius)
                val bottomShape = createShape(it.app.backgroundColor.darken, true, it.cornerRadius)

                this.bottomShape(bottomShape)
                this.topShape(topShape)
            }

//            ItemDataClass(it.app, it.sdkVersion, it.lastUpdateTime, it.cornerRadius)
//                .id("data class \${app.packageName}")
//                .addTo(this)
        }
    }
}


object ImageBindingAdapter {

    @JvmStatic
    @BindingAdapter("app:imageUrl")
    fun setImageUrl(view: ImageView, url: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val drawable = withContext(Dispatchers.IO) { AppManager.getIconFromId(url) }
            view.setImageDrawable(drawable)
        }
    }

    @JvmStatic
    @BindingAdapter("app:asyncText", "android:textSize", requireAll = false)
    fun asyncText(view: AppCompatTextView, text: CharSequence, textSize: Int?) {
        // first, set all measurement affecting properties of the text
        // (size, locale, typeface, direction, etc)
        if (textSize != null) {
            // interpret the text size as SP
            view.textSize = textSize.toFloat()
        }
        val params = TextViewCompat.getTextMetricsParams(view)
        view.setTextFuture(PrecomputedTextCompat.getTextFuture(text, params, null))
    }

}
