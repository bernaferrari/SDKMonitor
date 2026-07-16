package com.bernaferrari.sdkmonitor.shared.demo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.bernaferrari.sdkmonitor.domain.ThemeMode

private val Seed = Color(0xFFFF8364)

@Composable
fun DemoSdkMonitorTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme =
        when (themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM, ThemeMode.MATERIAL_YOU -> systemDark
        }

    val light =
        lightColorScheme(
            primary = Seed,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFDAD0),
        )
    val dark =
        darkColorScheme(
            primary = Color(0xFFFFB4A3),
            primaryContainer = Color(0xFF862200),
        )

    MaterialTheme(
        colorScheme = if (darkTheme) dark else light,
        typography = Typography(),
        content = content,
    )
}