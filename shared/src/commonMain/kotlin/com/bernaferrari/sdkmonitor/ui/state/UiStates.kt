package com.bernaferrari.sdkmonitor.ui.state

import com.bernaferrari.sdkmonitor.domain.AppDetails
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.domain.SdkDistribution
import com.bernaferrari.sdkmonitor.domain.SettingsPreferences

sealed class MainUiState {
    data object Loading : MainUiState()

    data class Success(
        val apps: List<AppVersion>,
        val filteredApps: List<AppVersion>,
        val totalCount: Int,
    ) : MainUiState()

    data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : MainUiState()
}

sealed class LogsUiState {
    data object Loading : LogsUiState()

    data class Success(
        val logs: List<LogEntry>,
        val totalCount: Int,
    ) : LogsUiState()

    data class Error(
        val message: String,
    ) : LogsUiState()
}

sealed class DetailsUiState {
    data object Loading : DetailsUiState()

    data class Success(
        val appDetails: AppDetails,
        val versions: List<AppVersion> = emptyList(),
    ) : DetailsUiState()

    data class Error(
        val message: String,
    ) : DetailsUiState()
}

data class SettingsUiState(
    val isLoading: Boolean = true,
    val preferences: SettingsPreferences = SettingsPreferences(),
    val errorMessage: String? = null,
    val sdkDistribution: List<SdkDistribution> = emptyList(),
    val totalApps: Int = 0,
    val allAppsForSdk: List<AppVersion> = emptyList(),
    val isAnalyticsLoading: Boolean = false,
) {
    val hasError: Boolean get() = errorMessage != null
}
