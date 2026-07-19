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
internal val filledShop: ImageVector
  get() {
    if (_filledShop != null) {
      return _filledShop!!
    }
    _filledShop =
      ImageVector.Builder(
          name = "shop",
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
            moveTo(11.05f, 17f)
            lineTo(15.2f, 14.35f)
            quadToRelative(0.45f, -0.3f, 0.45f, -0.85f)
            reflectiveQuadTo(15.2f, 12.65f)
            lineTo(11.05f, 10f)
            quadTo(10.55f, 9.67f, 10.03f, 9.95f)
            reflectiveQuadTo(9.5f, 10.83f)
            verticalLineToRelative(5.35f)
            quadToRelative(0f, 0.6f, 0.53f, 0.88f)
            reflectiveQuadTo(11.05f, 17f)
            close()
            moveTo(4f, 21f)
            quadTo(3.18f, 21f, 2.59f, 20.41f)
            reflectiveQuadTo(2f, 19f)
            verticalLineTo(7f)
            quadTo(2f, 6.57f, 2.29f, 6.29f)
            reflectiveQuadTo(3f, 6f)
            horizontalLineTo(8f)
            verticalLineTo(4f)
            quadTo(8f, 3.17f, 8.59f, 2.59f)
            reflectiveQuadTo(10f, 2f)
            horizontalLineToRelative(4f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(16f, 4f)
            verticalLineTo(6f)
            horizontalLineToRelative(5f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(22f, 7f)
            verticalLineTo(19f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(20f, 21f)
            horizontalLineTo(4f)
            close()
            moveTo(10f, 6f)
            horizontalLineToRelative(4f)
            verticalLineTo(4f)
            horizontalLineTo(10f)
            verticalLineTo(6f)
            close()
          }
        }
        .build()
    return _filledShop!!
  }

internal var _filledShop: ImageVector? = null
