package com.bernaferrari.sdkmonitor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.domain.ThemePalette
import com.bernaferrari.sdkmonitor.ui.platform.AppIconProvider
import com.bernaferrari.sdkmonitor.ui.platform.DefaultSdkStrings
import com.bernaferrari.sdkmonitor.ui.platform.LocalAppIconProvider
import com.bernaferrari.sdkmonitor.ui.platform.LocalSdkStrings
import com.bernaferrari.sdkmonitor.ui.platform.PlaceholderAppIconProvider
import com.bernaferrari.sdkmonitor.ui.platform.SdkStrings

/**
 * Shared Material3 theme. Android may wrap this with dynamic/Material You colors in [androidMain].
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SdkMonitorTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    themePalette: ThemePalette = ThemePalette.EMBER,
    darkTheme: Boolean =
        when (themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        },
    strings: SdkStrings = DefaultSdkStrings,
    appIconProvider: AppIconProvider = PlaceholderAppIconProvider,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalSdkStrings provides strings,
        LocalAppIconProvider provides appIconProvider,
    ) {
        MaterialExpressiveTheme(
            colorScheme =
                com.materialkolor.rememberDynamicMaterialThemeState(
                    seedColor = Color((themePalette.seedArgb ?: ThemePalette.EMBER.seedArgb)!!),
                    style = com.materialkolor.PaletteStyle.TonalSpot,
                    isDark = darkTheme,
                    specVersion = com.materialkolor.dynamiccolor.ColorSpec.SpecVersion.SPEC_2025,
                ).colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
