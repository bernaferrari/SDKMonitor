package com.bernaferrari.sdkmonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.bernaferrari.sdkmonitor.domain.logic.ApiLevel
import com.bernaferrari.sdkmonitor.ui.platform.apiShape

/**
 * An SDK level written inside its shape (see [apiShape]). A triangle's visual middle sits below its box's
 * middle, and a pentagon's a little, so the number drops to meet it instead of riding high.
 */
@Composable
fun SdkSeal(
    level: Int,
    size: Dp,
    fill: Color,
    textColor: Color,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    graded: Boolean = true,
) {
    val shape: Shape = if (graded) level.apiShape() else CircleShape
    val drop = if (graded) opticalDrop(level) else 0f
    Box(
        modifier = modifier.size(size).background(fill, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = level.toString(),
            style = textStyle,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            modifier = Modifier.offset(y = size * drop),
        )
    }
}

/** How far below center the shape's visual middle sits, as a share of its size. */
private fun opticalDrop(level: Int): Float =
    when (ApiLevel.LATEST_SUPPORTED - level) {
        in Int.MIN_VALUE..1 -> 0f
        2 -> 0.03f
        3 -> 0f
        else -> 0.11f
    }
