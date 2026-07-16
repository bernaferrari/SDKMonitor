package com.bernaferrari.sdkmonitor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.ui.platform.AppIconProvider
import com.bernaferrari.sdkmonitor.ui.platform.DefaultSdkStrings
import com.bernaferrari.sdkmonitor.ui.platform.LocalAppIconProvider
import com.bernaferrari.sdkmonitor.ui.platform.LocalSdkStrings
import com.bernaferrari.sdkmonitor.ui.platform.PlaceholderAppIconProvider
import com.bernaferrari.sdkmonitor.ui.platform.SdkStrings

private val Seed = Color(0xFFFF8364)

private val LightColors =
    lightColorScheme(
        primary = Seed,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFDAD0),
    )

private val DarkColors =
    darkColorScheme(
        primary = Color(0xFFFFB4A3),
        primaryContainer = Color(0xFF862200),
    )

/**
 * Shared Material3 theme. Android may wrap this with dynamic/Material You colors in [androidMain].
 */
@Composable
fun SdkMonitorTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean =
        when (themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM, ThemeMode.MATERIAL_YOU -> isSystemInDarkTheme()
        },
    strings: SdkStrings = DefaultSdkStrings,
    appIconProvider: AppIconProvider = PlaceholderAppIconProvider,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalSdkStrings provides strings,
        LocalAppIconProvider provides appIconProvider,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = Typography,
            content = content,
        )
    }
}
