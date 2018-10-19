package com.bernaferrari.sdkmonitor.main

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.rowItemBinding

internal class MainController : TypedEpoxyController<List<AppVersion>>(
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

    var onClick: ((AppVersion, View) -> (Unit))? = null

    override fun buildModels(data: List<AppVersion>) {
        data.forEach {
            rowItemBinding {
                id(it.app.packageName)

                this.onClick { model, _, view, _ ->
                    onClick?.invoke(model.appversion(), view)
                }

                this.appversion(it)

                val topShape = createShape(it.app.backgroundColor, false, it.cornerRadius)
                val bottomShape = createShape(it.app.backgroundColor.darken, true, it.cornerRadius)

                this.bottomShape(bottomShape)
                this.topShape(topShape)
            }
        }
    }
}
