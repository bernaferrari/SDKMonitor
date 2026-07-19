package com.bernaferrari.sdkmonitor.ui.icons

// Generated from Google Material Symbols Rounded's Kotlin vector endpoint.
// The FILL axis is explicit: FILL=1 for Filled and FILL=0 for Outlined.
// opsz=24, wght=400, GRAD=0, ROND=50.
// Source: https://fonts.gstatic.com/render/v1/Material+Symbols+Rounded/24dp/<name>.kt

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
internal val filledPlayArrow: ImageVector
  get() {
    if (_filledPlayArrow != null) {
      return _filledPlayArrow!!
    }
    _filledPlayArrow =
      ImageVector.Builder(
          name = "play_arrow",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f,
        )
        .apply {
          path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.Companion.NonZero,
          ) {
            moveTo(8f, 17.18f)
            verticalLineTo(6.82f)
            quadTo(8f, 6.4f, 8.3f, 6.11f)
            quadTo(8.6f, 5.82f, 9f, 5.82f)
            quadToRelative(0.13f, 0f, 0.26f, 0.04f)
            reflectiveQuadTo(9.53f, 5.97f)
            lineToRelative(8.15f, 5.18f)
            quadToRelative(0.23f, 0.15f, 0.34f, 0.38f)
            quadToRelative(0.11f, 0.23f, 0.11f, 0.48f)
            reflectiveQuadToRelative(-0.11f, 0.47f)
            reflectiveQuadToRelative(-0.34f, 0.38f)
            lineTo(9.53f, 18.02f)
            quadTo(9.4f, 18.1f, 9.26f, 18.14f)
            quadTo(9.13f, 18.18f, 9f, 18.18f)
            quadToRelative(-0.4f, 0f, -0.7f, -0.29f)
            reflectiveQuadTo(8f, 17.18f)
            close()
          }
        }
        .build()
    return _filledPlayArrow!!
  }

internal var _filledPlayArrow: ImageVector? = null
