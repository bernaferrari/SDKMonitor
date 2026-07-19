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
internal val filledDarkMode: ImageVector
  get() {
    if (_filledDarkMode != null) {
      return _filledDarkMode!!
    }
    _filledDarkMode =
      ImageVector.Builder(
          name = "dark_mode",
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
            moveTo(12f, 21f)
            quadTo(8.23f, 21f, 5.61f, 18.39f)
            quadTo(3f, 15.78f, 3f, 12f)
            quadTo(3f, 8.55f, 5.25f, 6.01f)
            reflectiveQuadTo(11f, 3.05f)
            quadTo(11.33f, 3f, 11.58f, 3.14f)
            reflectiveQuadToRelative(0.4f, 0.36f)
            quadToRelative(0.15f, 0.22f, 0.16f, 0.52f)
            reflectiveQuadTo(11.95f, 4.6f)
            quadTo(11.53f, 5.25f, 11.31f, 5.97f)
            reflectiveQuadTo(11.1f, 7.5f)
            quadToRelative(0f, 2.25f, 1.57f, 3.82f)
            reflectiveQuadTo(16.5f, 12.9f)
            quadToRelative(0.78f, 0f, 1.54f, -0.22f)
            reflectiveQuadTo(19.4f, 12.05f)
            quadToRelative(0.27f, -0.17f, 0.56f, -0.16f)
            reflectiveQuadToRelative(0.51f, 0.14f)
            quadToRelative(0.25f, 0.13f, 0.39f, 0.38f)
            reflectiveQuadTo(20.95f, 13f)
            quadToRelative(-0.35f, 3.45f, -2.94f, 5.73f)
            quadTo(15.43f, 21f, 12f, 21f)
            close()
          }
        }
        .build()
    return _filledDarkMode!!
  }

internal var _filledDarkMode: ImageVector? = null
