package com.bernaferrari.sdkmonitor.ui

import android.graphics.Rect
import android.view.View
import androidx.annotation.Dimension
import androidx.recyclerview.widget.RecyclerView

/**
 * An ItemDecoration which applies an even visual padding on the left and right edges of a grid and
 * between each item, while also applying an even amount of inset to each item.  This ensures that
 * all items remain the same size.
 *
 *
 * It assumes all items in a row have the same span size, and it assumes it's the only item
 * decorator.
 */
class InsetDecoration(
    @param:Dimension private val padding: Int,
    private val keyLeft: Boolean,
    private val keyRight: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (keyLeft) outRect.left = padding
        if (keyRight) outRect.right = padding
    }
}
