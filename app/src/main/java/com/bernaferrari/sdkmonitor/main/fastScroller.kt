package com.bernaferrari.sdkmonitor.main

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bernaferrari.base.misc.hideKeyboard
import com.bernaferrari.base.view.inflate
import com.bernaferrari.sdkmonitor.R
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerThumbView
import com.reddit.indicatorfastscroll.FastScrollerView

private fun getItemIndicator(itemFromList: AppVersion?): FastScrollItemIndicator? {
    // it might be null when model is updated really fast
    if (itemFromList == null) return null

    val letter = itemFromList.app.title.substring(0, 1)
    val index = if (!letter[0].isLetter()) "#" else letter.uppercase()

    return FastScrollItemIndicator.Text(index) // Return a text indicator
}

internal fun View.setupFastScroller(
    recyclerView: RecyclerView,
    activity: Activity?,
    items: (Int) -> (AppVersion?)
) {
    val linearLayoutManager = recyclerView.layoutManager ?: return

    findViewById<FastScrollerView>(R.id.fastscroller).setupWithRecyclerView(
        recyclerView = recyclerView,
        useDefaultScroller = false,
        getItemIndicator = { pos -> getItemIndicator(items(pos)) }
    )

    val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int = SNAP_TO_START
    }

    findViewById<FastScrollerView>(R.id.fastscroller).itemIndicatorSelectedCallbacks += object :
        FastScrollerView.ItemIndicatorSelectedCallback {
        override fun onItemIndicatorSelected(
            indicator: FastScrollItemIndicator,
            indicatorCenterY: Int,
            itemPosition: Int
        ) {
            recyclerView.stopScroll()
            activity?.hideKeyboard()
            smoothScroller.targetPosition = itemPosition
            linearLayoutManager.startSmoothScroll(smoothScroller)
        }
    }

    findViewById<FastScrollerThumbView>(R.id.fastscroller_thumb).setupWithFastScroller(
        findViewById(R.id.fastscroller)
    )
}

fun FrameLayout.inflateFastScroll(): View {
    val fastScroll = this.inflate(R.layout.reddit_fast_scroller, false)
    this.addView(fastScroll)
    return fastScroll
}
