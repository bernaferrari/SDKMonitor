package com.bernaferrari.sdkmonitor.util

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter

object AsyncBindingAdapter {

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
        view.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                text,
                params,
                null
            )
        )
    }
}
