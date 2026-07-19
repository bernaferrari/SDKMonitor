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
internal val filledClose: ImageVector
  get() {
    if (_filledClose != null) {
      return _filledClose!!
    }
    _filledClose =
      ImageVector.Builder(
          name = "close",
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
            moveTo(12f, 13.4f)
            lineTo(7.1f, 18.3f)
            quadTo(6.83f, 18.58f, 6.4f, 18.58f)
            reflectiveQuadTo(5.7f, 18.3f)
            quadTo(5.43f, 18.02f, 5.43f, 17.6f)
            reflectiveQuadTo(5.7f, 16.9f)
            lineTo(10.6f, 12f)
            lineTo(5.7f, 7.1f)
            quadTo(5.43f, 6.82f, 5.43f, 6.4f)
            reflectiveQuadTo(5.7f, 5.7f)
            reflectiveQuadTo(6.4f, 5.43f)
            reflectiveQuadTo(7.1f, 5.7f)
            lineTo(12f, 10.6f)
            lineTo(16.9f, 5.7f)
            quadTo(17.18f, 5.43f, 17.6f, 5.43f)
            reflectiveQuadTo(18.3f, 5.7f)
            reflectiveQuadToRelative(0.27f, 0.7f)
            reflectiveQuadTo(18.3f, 7.1f)
            lineTo(13.4f, 12f)
            lineToRelative(4.9f, 4.9f)
            quadToRelative(0.27f, 0.28f, 0.27f, 0.7f)
            quadToRelative(0f, 0.42f, -0.27f, 0.7f)
            reflectiveQuadToRelative(-0.7f, 0.27f)
            reflectiveQuadTo(16.9f, 18.3f)
            lineTo(12f, 13.4f)
            close()
          }
        }
        .build()
    return _filledClose!!
  }

internal var _filledClose: ImageVector? = null
