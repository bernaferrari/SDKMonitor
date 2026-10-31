@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.bernaferrari.sdkmonitor.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.material3.rememberDynamicMaterialThemeState
import com.bernaferrari.sdkmonitor.domain.ColorStyle
import com.bernaferrari.sdkmonitor.domain.ThemePalette
import com.bernaferrari.sdkmonitor.ui.theme.paletteStyle

// private val primary = Color(0xFF1976D2)
@Composable
fun SDKMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    themePalette: ThemePalette = ThemePalette.DYNAMIC,
    colorStyle: ColorStyle = ColorStyle.TONAL,
    content: @Composable () -> Unit,
) {
    // Wallpaper colors are used exactly as Android gives them, so the app matches the rest of the phone
    // (Android has its own style setting for them); the color style shapes the app's own colors.
    val styledScheme =
        rememberDynamicMaterialThemeState(
            seedColor = Color((themePalette.seedArgb ?: ThemePalette.EMBER.seedArgb)!!),
            style = colorStyle.paletteStyle(),
            isDark = darkTheme,
            specVersion = ColorSpec.SpecVersion.SPEC_2025,
        ).colorScheme
    val context = LocalContext.current
    val colorScheme =
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            styledScheme
        }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            // Set status bar content to be dark or light based on theme
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    return MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
