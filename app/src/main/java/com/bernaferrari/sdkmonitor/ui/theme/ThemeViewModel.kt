package com.bernaferrari.sdkmonitor.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.domain.ColorStyle
import com.bernaferrari.sdkmonitor.domain.ThemePalette
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ThemeViewModel(
        private val preferencesRepository: PreferencesRepository,
    ) : ViewModel() {
        private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
        val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()
        private val _themePalette = MutableStateFlow(ThemePalette.DYNAMIC)
        val themePalette: StateFlow<ThemePalette> = _themePalette.asStateFlow()
        private val _colorStyle = MutableStateFlow(ColorStyle.TONAL)
        val colorStyle: StateFlow<ColorStyle> = _colorStyle.asStateFlow()

        init {
            observeThemePreferences()
        }

        private fun observeThemePreferences() {
            viewModelScope.launch {
                preferencesRepository
                    .getUserPreferences()
                    .catch { /* Handle error silently, use default */ }
                    .collect { preferences ->
                        _themeMode.value = preferences.themeMode
                        _themePalette.value = preferences.themePalette
                        _colorStyle.value = preferences.colorStyle
                    }
            }
        }

        @Composable
        fun shouldUseDarkTheme(): Boolean {
            val currentTheme by themeMode.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()

            return when (currentTheme) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemInDarkTheme
            }
        }

        @Composable
        fun currentThemePalette(): ThemePalette {
            val currentPalette by themePalette.collectAsState()
            return currentPalette
        }

        @Composable
        fun currentColorStyle(): ColorStyle {
            val style by colorStyle.collectAsState()
            return style
        }

        @Composable
        fun shouldUseDynamicColor(): Boolean {
            val currentPalette by themePalette.collectAsState()
            return currentPalette == ThemePalette.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        }
    }
