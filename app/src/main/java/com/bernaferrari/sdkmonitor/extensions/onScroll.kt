package com.bernaferrari.sdkmonitor.extensions

internal inline fun androidx.recyclerview.widget.RecyclerView.onScroll(crossinline body: (dx: Int, dy: Int) -> Unit) {
    addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
        override fun onScrolled(
            recyclerView: androidx.recyclerview.widget.RecyclerView,
            dx: Int,
            dy: Int
        ) {
            body(dx, dy)
        }
    })
}