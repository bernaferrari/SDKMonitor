package com.bernaferrari.sdkmonitor.ui.icons

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * SDK Monitor's icon, as the launcher shows it (store-listing/brand, the 72-wide visible middle of the adaptive
 * canvas): the brand gradient, red into amber, with the app's sun seal in white holding SDK in red. It keeps its
 * own colors; clip it to the tile's shape.
 */
val SdkMonitorMark: ImageVector
    get() {
        _sdkMonitorMark?.let { return it }
        return ImageVector
            .Builder(name = "SdkMonitorMark", defaultWidth = 72.dp, defaultHeight = 72.dp, viewportWidth = 72f, viewportHeight = 72f)
            .apply {
                path(
                    fill =
                        Brush.linearGradient(
                            0f to Color(0xFFE2341F),
                            0.6f to Color(0xFFF4701B),
                            1f to Color(0xFFFBB21A),
                            start = Offset(-18f, -18f),
                            end = Offset(90f, 90f),
                        ),
                ) {
                    moveTo(0f, 0f)
                    horizontalLineTo(72f)
                    verticalLineTo(72f)
                    horizontalLineTo(0f)
                    close()
                }
                addPath(pathData = addPathNodes(SEAL), fill = SolidColor(Color.White))
                group(scaleX = 0.8f, scaleY = 0.8f, pivotX = 54f, pivotY = 53.9f, translationX = -18f, translationY = -18f) {
                    LETTERS.forEach { addPath(pathData = addPathNodes(it), fill = SolidColor(Color(0xFFE2401F))) }
                }
            }.build()
            .also { _sdkMonitorMark = it }
    }

private var _sdkMonitorMark: ImageVector? = null

/** Material's Sunny, eight soft points, centred in the visible 72. */
private const val SEAL = "M67.00,36.00L66.91,37.01L66.64,38.01L66.22,38.98L65.66,39.90L65.00,40.79L64.28,41.62L63.53,42.42L62.80,43.18L62.12,43.92L61.52,44.66L61.02,45.42L60.63,46.20L60.35,47.03L60.17,47.92L60.07,48.87L60.03,49.87L60.01,50.93L59.97,52.02L59.89,53.12L59.73,54.21L59.47,55.26L59.09,56.25L58.57,57.14L57.92,57.92L57.14,58.57L56.25,59.09L55.26,59.47L54.21,59.73L53.12,59.89L52.02,59.97L50.93,60.01L49.87,60.03L48.87,60.07L47.92,60.17L47.03,60.35L46.20,60.63L45.42,61.02L44.66,61.52L43.92,62.12L43.18,62.80L42.42,63.53L41.62,64.28L40.79,65.00L39.90,65.66L38.98,66.22L38.01,66.64L37.01,66.91L36.00,67.00L34.99,66.91L33.99,66.64L33.02,66.22L32.10,65.66L31.21,65.00L30.38,64.28L29.58,63.53L28.82,62.80L28.08,62.12L27.34,61.52L26.58,61.02L25.80,60.63L24.97,60.35L24.08,60.17L23.13,60.07L22.13,60.03L21.07,60.01L19.98,59.97L18.88,59.89L17.79,59.73L16.74,59.47L15.75,59.09L14.86,58.57L14.08,57.92L13.43,57.14L12.91,56.25L12.53,55.26L12.27,54.21L12.11,53.12L12.03,52.02L11.99,50.93L11.97,49.87L11.93,48.87L11.83,47.92L11.65,47.03L11.37,46.20L10.98,45.42L10.48,44.66L9.88,43.92L9.20,43.18L8.47,42.42L7.72,41.62L7.00,40.79L6.34,39.90L5.78,38.98L5.36,38.01L5.09,37.01L5.00,36.00L5.09,34.99L5.36,33.99L5.78,33.02L6.34,32.10L7.00,31.21L7.72,30.38L8.47,29.58L9.20,28.82L9.88,28.08L10.48,27.34L10.98,26.58L11.37,25.80L11.65,24.97L11.83,24.08L11.93,23.13L11.97,22.13L11.99,21.07L12.03,19.98L12.11,18.88L12.27,17.79L12.53,16.74L12.91,15.75L13.43,14.86L14.08,14.08L14.86,13.43L15.75,12.91L16.74,12.53L17.79,12.27L18.88,12.11L19.98,12.03L21.07,11.99L22.13,11.97L23.13,11.93L24.08,11.83L24.97,11.65L25.80,11.37L26.58,10.98L27.34,10.48L28.08,9.88L28.82,9.20L29.58,8.47L30.38,7.72L31.21,7.00L32.10,6.34L33.02,5.78L33.99,5.36L34.99,5.09L36.00,5.00L37.01,5.09L38.01,5.36L38.98,5.78L39.90,6.34L40.79,7.00L41.62,7.72L42.42,8.47L43.18,9.20L43.92,9.88L44.66,10.48L45.42,10.98L46.20,11.37L47.03,11.65L47.92,11.83L48.87,11.93L49.87,11.97L50.93,11.99L52.02,12.03L53.12,12.11L54.21,12.27L55.26,12.53L56.25,12.91L57.14,13.43L57.92,14.08L58.57,14.86L59.09,15.75L59.47,16.74L59.73,17.79L59.89,18.88L59.97,19.98L60.01,21.07L60.03,22.13L60.07,23.13L60.17,24.08L60.35,24.97L60.63,25.80L61.02,26.58L61.52,27.34L62.12,28.08L62.80,28.82L63.53,29.58L64.28,30.38L65.00,31.21L65.66,32.10L66.22,33.02L66.64,33.99L66.91,34.99Z"

/** S, D and K, in the adaptive canvas's coordinates (the group moves them into the visible 72). */
private val LETTERS =
    listOf(
        "M38.94,62.58C37.53,62.58 36.31,62.35 35.27,61.88C34.23,61.42 33.39,60.79 32.75,59.99L34.84,57.68C35.45,58.35 36.11,58.85 36.83,59.17C37.57,59.49 38.31,59.65 39.06,59.65C39.93,59.65 40.58,59.46 41.03,59.07C41.48,58.69 41.7,58.14 41.7,57.42C41.7,56.83 41.53,56.38 41.17,56.07C40.84,55.77 40.26,55.55 39.45,55.43L37.69,55.14C36.19,54.88 35.1,54.33 34.43,53.48C33.76,52.62 33.42,51.57 33.42,50.34C33.42,48.72 33.95,47.47 35.01,46.59C36.06,45.7 37.54,45.25 39.45,45.25C40.74,45.25 41.87,45.45 42.83,45.85C43.79,46.25 44.57,46.79 45.16,47.48L43.12,49.76C42.67,49.27 42.14,48.88 41.53,48.61C40.93,48.32 40.24,48.18 39.47,48.18C37.85,48.18 37.05,48.84 37.05,50.17C37.05,50.75 37.22,51.18 37.57,51.47C37.94,51.75 38.53,51.97 39.35,52.11L41.1,52.43C42.46,52.67 43.51,53.18 44.25,53.96C44.98,54.75 45.35,55.8 45.35,57.13C45.35,57.91 45.21,58.64 44.94,59.31C44.67,59.97 44.26,60.55 43.72,61.04C43.17,61.52 42.5,61.9 41.7,62.17C40.92,62.44 40,62.58 38.94,62.58Z",
        "M47.93,45.54H53.33C55.59,45.54 57.26,46.26 58.35,47.7C59.44,49.14 59.98,51.21 59.98,53.91C59.98,56.62 59.44,58.69 58.35,60.13C57.26,61.57 55.59,62.29 53.33,62.29H47.93V45.54ZM53.09,59.46C54.16,59.46 54.95,59.11 55.44,58.43C55.94,57.74 56.19,56.74 56.19,55.43V52.38C56.19,51.08 55.94,50.09 55.44,49.4C54.95,48.71 54.16,48.37 53.09,48.37H51.48V59.46H53.09Z",
        "M67.51,55.31L66.02,57.39V62.29H62.42V45.54H66.02V52.55H66.21L68.04,49.76L70.99,45.54H75.02L70,52.62L75.26,62.29H71.2L67.51,55.31Z",
    )
