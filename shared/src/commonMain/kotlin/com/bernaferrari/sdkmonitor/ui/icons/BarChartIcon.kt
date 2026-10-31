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
internal val filledBarChart: ImageVector
  get() {
    if (_filledBarChart != null) {
      return _filledBarChart!!
    }
    _filledBarChart =
      ImageVector.Builder(
          name = "bar_chart",
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
            moveTo(17f, 20f)
            quadToRelative(-0.43f, 0f, -0.71f, -0.29f)
            quadTo(16f, 19.43f, 16f, 19f)
            verticalLineTo(14f)
            quadToRelative(0f, -0.43f, 0.29f, -0.71f)
            reflectiveQuadTo(17f, 13f)
            horizontalLineToRelative(2f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(20f, 14f)
            verticalLineToRelative(5f)
            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
            reflectiveQuadTo(19f, 20f)
            horizontalLineTo(17f)
            close()
            moveToRelative(-6f, 0f)
            quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
            quadTo(10f, 19.43f, 10f, 19f)
            verticalLineTo(5f)
            quadTo(10f, 4.57f, 10.29f, 4.29f)
            reflectiveQuadTo(11f, 4f)
            horizontalLineToRelative(2f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(14f, 5f)
            verticalLineTo(19f)
            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
            reflectiveQuadTo(13f, 20f)
            horizontalLineTo(11f)
            close()
            moveTo(5f, 20f)
            quadTo(4.58f, 20f, 4.29f, 19.71f)
            quadTo(4f, 19.43f, 4f, 19f)
            verticalLineTo(10f)
            quadTo(4f, 9.57f, 4.29f, 9.29f)
            reflectiveQuadTo(5f, 9f)
            horizontalLineTo(7f)
            quadTo(7.43f, 9f, 7.71f, 9.29f)
            reflectiveQuadTo(8f, 10f)
            verticalLineToRelative(9f)
            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
            reflectiveQuadTo(7f, 20f)
            horizontalLineTo(5f)
            close()
          }
        }
        .build()
    return _filledBarChart!!
  }

internal var _filledBarChart: ImageVector? = null
