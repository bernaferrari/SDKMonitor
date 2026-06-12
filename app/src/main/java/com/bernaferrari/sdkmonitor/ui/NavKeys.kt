package com.bernaferrari.sdkmonitor.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// Main tabs - these are used for bottom navigation
@Serializable
data object MainTab : NavKey

@Serializable
data object LogsTab : NavKey

@Serializable
data object SettingsTab : NavKey

// Destination keys
@Serializable
data class AppDetails(
    val packageName: String,
) : NavKey

@Serializable
data object About : NavKey
