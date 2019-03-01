package com.bernaferrari.sdkmonitor

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("isItemVisible")
fun isItemVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("srcRes")
fun imageViewSrcRes(view: ImageView, drawableRes: Int) {
    if (drawableRes != 0) {
        view.setImageResource(drawableRes)
    } else {
        view.setImageDrawable(null)
    }
}
