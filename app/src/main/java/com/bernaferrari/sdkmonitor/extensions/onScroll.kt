package com.bernaferrari.sdkmonitor.extensions

import android.support.v7.widget.RecyclerView

internal inline fun RecyclerView.onScroll(crossinline body: (dx: Int, dy: Int) -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            body(dx, dy)
        }
    })
}