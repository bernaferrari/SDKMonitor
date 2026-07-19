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
internal val filledSecurity: ImageVector
  get() {
    if (_filledSecurity != null) {
      return _filledSecurity!!
    }
    _filledSecurity =
      ImageVector.Builder(
          name = "security",
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
            moveTo(12f, 19.9f)
            quadToRelative(2.43f, -0.75f, 4.05f, -2.96f)
            reflectiveQuadTo(17.95f, 12f)
            horizontalLineTo(12f)
            verticalLineTo(4.13f)
            lineTo(6f, 6.38f)
            verticalLineTo(11.1f)
            quadToRelative(0f, 0.28f, 0f, 0.45f)
            quadTo(6f, 11.73f, 6.05f, 12f)
            horizontalLineTo(12f)
            verticalLineToRelative(7.9f)
            close()
            moveToRelative(-0.32f, 1.98f)
            quadTo(11.53f, 21.85f, 11.38f, 21.8f)
            quadTo(8f, 20.68f, 6f, 17.64f)
            reflectiveQuadTo(4f, 11.1f)
            verticalLineTo(6.38f)
            quadTo(4f, 5.75f, 4.36f, 5.25f)
            quadTo(4.73f, 4.75f, 5.3f, 4.52f)
            lineToRelative(6f, -2.25f)
            quadTo(11.65f, 2.15f, 12f, 2.15f)
            reflectiveQuadToRelative(0.7f, 0.13f)
            lineToRelative(6f, 2.25f)
            quadToRelative(0.58f, 0.23f, 0.94f, 0.73f)
            reflectiveQuadTo(20f, 6.38f)
            verticalLineTo(11.1f)
            quadToRelative(0f, 3.5f, -2f, 6.54f)
            quadToRelative(-2f, 3.04f, -5.38f, 4.16f)
            quadToRelative(-0.15f, 0.05f, -0.3f, 0.07f)
            reflectiveQuadTo(12f, 21.9f)
            reflectiveQuadTo(11.68f, 21.88f)
            close()
          }
        }
        .build()
    return _filledSecurity!!
  }

internal var _filledSecurity: ImageVector? = null
