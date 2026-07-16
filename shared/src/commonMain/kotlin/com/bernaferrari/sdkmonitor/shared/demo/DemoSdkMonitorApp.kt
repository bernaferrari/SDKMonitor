@file:OptIn(androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)

package com.bernaferrari.sdkmonitor.shared.demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bernaferrari.sdkmonitor.data.repository.RoomAppsRepository
import com.bernaferrari.sdkmonitor.domain.AppDetails
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.AppListLogic
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.domain.SettingsPreferences
import com.bernaferrari.sdkmonitor.domain.SortOption
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.domain.logic.AnalyticsLogic
import com.bernaferrari.sdkmonitor.ui.details.DetailsContent
import com.bernaferrari.sdkmonitor.ui.logs.LogsContent
import com.bernaferrari.sdkmonitor.ui.main.MainContent
import com.bernaferrari.sdkmonitor.ui.platform.rememberComposeSdkStrings
import com.bernaferrari.sdkmonitor.ui.settings.SettingsContent
import com.bernaferrari.sdkmonitor.ui.state.DetailsUiState
import com.bernaferrari.sdkmonitor.ui.state.LogsUiState
import com.bernaferrari.sdkmonitor.ui.state.MainUiState
import com.bernaferrari.sdkmonitor.ui.state.SettingsUiState
import com.bernaferrari.sdkmonitor.ui.theme.SdkMonitorTheme

/**
 * Web/desktop data host. It uses the same Material adaptive navigation suite and shared screen
 * components as Android; only the Room data source and unavailable system capabilities differ.
 */
@Composable
fun DemoSdkMonitorApp(
    roomRepository: RoomAppsRepository,
    showWebBanner: Boolean = false,
) {
    val session = remember { DemoSessionState() }
    val prefs by session.preferences.collectAsState()
    val searchQuery by session.searchQuery.collectAsState()
    val sortOption by session.sortOption.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var selectedPackage by remember { mutableStateOf<String?>(null) }
    val allApps by roomRepository.getAppsWithVersions().collectAsState(initial = emptyList())
    val filteredApps = remember(allApps, prefs, searchQuery, sortOption) {
        AppListLogic.applyListPipeline(allApps, prefs.appFilter, sortOption, prefs.orderBySdk, searchQuery)
    }
    var logs by remember { mutableStateOf<List<LogEntry>>(emptyList()) }
    LaunchedEffect(roomRepository, prefs.appFilter, allApps) {
        logs = AppListLogic.filterLogsByAppFilter(roomRepository.buildChangeLogs(prefs.appFilter), allApps, prefs.appFilter)
    }
    val (distribution, filteredForSdk) = remember(allApps, prefs.appFilter) {
        AnalyticsLogic.sdkDistribution(allApps, prefs.appFilter)
    }

    SdkMonitorTheme(
        themeMode = prefs.themeMode,
        strings = rememberComposeSdkStrings(),
        appIconProvider = DemoAppIconProvider,
    ) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                DemoDestination.entries.forEach { destination ->
                    item(
                        selected = tab == destination.index,
                        onClick = {
                            tab = destination.index
                            selectedPackage = null
                        },
                        icon = { Icon(destination.icon, contentDescription = null) },
                        label = { Text(destination.label) },
                    )
                }
            },
        ) {
            val modifier = Modifier.fillMaxSize()
            val details = selectedPackage?.let { detailsFromRoom(allApps, it) }
            when {
                selectedPackage != null && tab != DemoDestination.Settings.index -> DetailsContent(
                    uiState = if (details == null) DetailsUiState.Error("App not found") else DetailsUiState.Success(details),
                    onRetry = { selectedPackage = null },
                    contentModifier = modifier,
                )
                tab == DemoDestination.Apps.index -> MainContent(
                    uiState = MainUiState.Success(allApps, filteredApps, filteredApps.size),
                    searchQuery = searchQuery,
                    appFilter = prefs.appFilter,
                    sortOption = sortOption,
                    onSearchQueryChange = session::setSearchQuery,
                    onAppFilterChange = session::setAppFilter,
                    onSortOptionChange = session::setSortOption,
                    onAppClick = { selectedPackage = it },
                    onRetry = {},
                    contentModifier = modifier,
                )
                tab == DemoDestination.Logs.index -> LogsContent(
                    uiState = LogsUiState.Success(logs, logs.size),
                    formatTime = { timestamp ->
                        when (timestamp) {
                            1_748_500_000_000L -> "Jun 19"
                            1_748_400_000_000L -> "Jun 18"
                            1_747_900_000_000L -> "Jun 12"
                            1_747_500_000_000L -> "Jun 7"
                            else -> "Jan 2024"
                        }
                    },
                    onLogClick = { selectedPackage = it.packageName },
                    onRetry = {},
                    contentModifier = modifier,
                )
                else -> SettingsContent(
                    uiState = SettingsUiState(
                        isLoading = false,
                        preferences = SettingsPreferences(prefs.themeMode, prefs.appFilter, prefs.backgroundSync),
                        sdkDistribution = distribution,
                        totalApps = filteredForSdk.size,
                        allAppsForSdk = filteredForSdk,
                    ),
                    appVersionLabel = "SDK Monitor",
                    availableThemeModes = ThemeMode.entries.filterNot { it == ThemeMode.MATERIAL_YOU },
                    onThemeModeChange = session::setThemeMode,
                    onAppFilterChange = session::setAppFilter,
                    onBackgroundSyncToggle = {},
                    onSetSyncInterval = { _, _ -> },
                    onClearError = {},
                    onNavigateToAbout = {},
                    onNavigateToAppDetails = { selectedPackage = it },
                    contentModifier = modifier,
                )
            }
        }
    }
}

private enum class DemoDestination(
    val index: Int,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Apps(0, "Apps", Icons.Default.Apps),
    Logs(1, "Logs", Icons.Default.History),
    Settings(2, "Settings", Icons.Default.Settings),
}

private fun detailsFromRoom(allApps: List<AppVersion>, packageName: String): AppDetails? {
    val app = allApps.find { it.packageName == packageName } ?: return null
    return AppDetails(
        packageName = app.packageName,
        title = app.title,
        versionName = app.versionName,
        versionCode = app.versionCode,
        targetSdk = app.sdkVersion,
        minSdk = if (app.isSystemApp) 28 else 24,
        size = 45_000_000L + app.versionCode % 100_000_000,
        lastUpdateTime = app.lastUpdateTime,
        isSystemApp = app.isSystemApp,
    )
}
