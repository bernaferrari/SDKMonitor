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
internal val filledEmail: ImageVector
  get() {
    if (_filledEmail != null) {
      return _filledEmail!!
    }
    _filledEmail =
      ImageVector.Builder(
          name = "email",
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
            moveTo(4f, 20f)
            quadTo(3.18f, 20f, 2.59f, 19.41f)
            reflectiveQuadTo(2f, 18f)
            verticalLineTo(6f)
            quadTo(2f, 5.18f, 2.59f, 4.59f)
            reflectiveQuadTo(4f, 4f)
            horizontalLineTo(20f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            quadTo(22f, 5.18f, 22f, 6f)
            verticalLineTo(18f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(20f, 20f)
            horizontalLineTo(4f)
            close()
            moveToRelative(8.26f, -7.21f)
            quadToRelative(0.14f, -0.04f, 0.26f, -0.11f)
            lineTo(19.6f, 8.25f)
            quadTo(19.8f, 8.13f, 19.9f, 7.94f)
            reflectiveQuadTo(20f, 7.52f)
            quadTo(20f, 7.02f, 19.58f, 6.77f)
            reflectiveQuadTo(18.7f, 6.8f)
            lineTo(12f, 11f)
            lineTo(5.3f, 6.8f)
            quadTo(4.85f, 6.52f, 4.43f, 6.79f)
            reflectiveQuadTo(4f, 7.52f)
            quadTo(4f, 7.77f, 4.1f, 7.96f)
            reflectiveQuadTo(4.4f, 8.25f)
            lineToRelative(7.08f, 4.42f)
            quadToRelative(0.13f, 0.08f, 0.26f, 0.11f)
            reflectiveQuadTo(12f, 12.83f)
            reflectiveQuadToRelative(0.26f, -0.04f)
            close()
          }
        }
        .build()
    return _filledEmail!!
  }

internal var _filledEmail: ImageVector? = null
