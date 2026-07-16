package com.bernaferrari.sdkmonitor.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings

data class ThemeModeUi(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

@Composable
fun ThemeMode.ui(): ThemeModeUi {
    val s = sdkStrings()
    return when (this) {
        ThemeMode.SYSTEM ->
            ThemeModeUi(
                s.themeSystem,
                s.themeSystemDescription,
                Icons.Outlined.PhoneAndroid,
                Icons.Filled.PhoneAndroid,
            )
        ThemeMode.MATERIAL_YOU ->
            ThemeModeUi(
                s.themeMaterialYou,
                s.themeMaterialYouDescription,
                Icons.Outlined.Palette,
                Icons.Filled.Palette,
            )
        ThemeMode.LIGHT ->
            ThemeModeUi(
                s.themeLight,
                s.themeLightDescription,
                Icons.Outlined.LightMode,
                Icons.Filled.LightMode,
            )
        ThemeMode.DARK ->
            ThemeModeUi(
                s.themeDark,
                s.themeDarkDescription,
                Icons.Outlined.DarkMode,
                Icons.Filled.DarkMode,
            )
    }
}
