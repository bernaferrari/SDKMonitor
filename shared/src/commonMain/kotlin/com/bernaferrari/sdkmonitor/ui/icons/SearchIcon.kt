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
internal val filledSearch: ImageVector
  get() {
    if (_filledSearch != null) {
      return _filledSearch!!
    }
    _filledSearch =
      ImageVector.Builder(
          name = "search",
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
            moveTo(9.5f, 16f)
            quadTo(6.78f, 16f, 4.89f, 14.11f)
            quadTo(3f, 12.23f, 3f, 9.5f)
            quadTo(3f, 6.77f, 4.89f, 4.89f)
            reflectiveQuadTo(9.5f, 3f)
            reflectiveQuadToRelative(4.61f, 1.89f)
            reflectiveQuadTo(16f, 9.5f)
            quadToRelative(0f, 1.1f, -0.35f, 2.07f)
            reflectiveQuadTo(14.7f, 13.3f)
            lineToRelative(5.6f, 5.6f)
            quadToRelative(0.28f, 0.28f, 0.28f, 0.7f)
            quadToRelative(0f, 0.42f, -0.28f, 0.7f)
            quadToRelative(-0.27f, 0.27f, -0.7f, 0.27f)
            reflectiveQuadTo(18.9f, 20.3f)
            lineTo(13.3f, 14.7f)
            quadToRelative(-0.75f, 0.6f, -1.72f, 0.95f)
            reflectiveQuadTo(9.5f, 16f)
            close()
            moveToRelative(0f, -2f)
            quadToRelative(1.88f, 0f, 3.19f, -1.31f)
            reflectiveQuadTo(14f, 9.5f)
            reflectiveQuadTo(12.69f, 6.31f)
            reflectiveQuadTo(9.5f, 5f)
            reflectiveQuadTo(6.31f, 6.31f)
            reflectiveQuadTo(5f, 9.5f)
            reflectiveQuadToRelative(1.31f, 3.19f)
            reflectiveQuadTo(9.5f, 14f)
            close()
          }
        }
        .build()
    return _filledSearch!!
  }

internal var _filledSearch: ImageVector? = null
