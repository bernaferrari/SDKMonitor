package com.bernaferrari.sdkmonitor.ui.platform

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import com.bernaferrari.sdkmonitor.domain.logic.ApiLevel

fun Int.apiToComposeColor(): Color = Color(ApiLevel.colorArgb(this))

fun Int.apiToVersionName(): String = ApiLevel.versionName(this)

/** The SDK's color as text or a mark: the raw color is too dark to read on a dark surface, so it lightens there. */
@Composable
fun Int.apiContentColor(): Color {
    val color = apiToComposeColor()
    return if (isDarkSurface()) lerp(color, Color.White, 0.38f) else color
}

/** A quiet fill in the SDK's color, behind [apiContentColor]; [strong] for a small shape that must still read. */
@Composable
fun Int.apiContainerColor(strong: Boolean = false): Color =
    apiToComposeColor().copy(
        alpha =
            when {
                isDarkSurface() -> if (strong) 0.32f else 0.22f
                else -> if (strong) 0.2f else 0.12f
            },
    )

/**
 * The SDK's shape, a second signal beside its color: soft and sunny when current, more angular as it falls
 * behind, down to a warning triangle. Newest: Sunny; one behind: a 9-scallop cookie; two: pentagon; three:
 * diamond; four or more: triangle.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Int.apiShape(): Shape =
    when (ApiLevel.LATEST_SUPPORTED - this) {
        in Int.MIN_VALUE..0 -> MaterialShapes.Sunny
        1 -> MaterialShapes.Cookie9Sided
        2 -> MaterialShapes.Pentagon
        3 -> MaterialShapes.Diamond
        else -> MaterialShapes.Triangle
    }.toShape()

@Composable
private fun isDarkSurface(): Boolean = MaterialTheme.colorScheme.surface.luminance() < 0.5f
