@file:OptIn(
    androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class,
    androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi::class,
)

package com.bernaferrari.sdkmonitor.shared.demo

import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import com.bernaferrari.sdkmonitor.data.repository.RoomAppsRepository
import com.bernaferrari.sdkmonitor.domain.AppDetails
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.AppListLogic
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.domain.SettingsPreferences
import com.bernaferrari.sdkmonitor.domain.SortOption
import com.bernaferrari.sdkmonitor.domain.ThemePalette
import com.bernaferrari.sdkmonitor.domain.logic.AnalyticsLogic
import com.bernaferrari.sdkmonitor.domain.logic.formatRelativeTimestamp
import com.bernaferrari.sdkmonitor.ui.details.DetailsContent
import com.bernaferrari.sdkmonitor.ui.logs.LogsContent
import com.bernaferrari.sdkmonitor.ui.main.MainContent
import com.bernaferrari.sdkmonitor.ui.platform.rememberComposeSdkStrings
import com.bernaferrari.sdkmonitor.ui.settings.SettingsContent
import com.bernaferrari.sdkmonitor.ui.settings.AboutContent
import com.bernaferrari.sdkmonitor.ui.state.DetailsUiState
import com.bernaferrari.sdkmonitor.ui.state.LogsUiState
import com.bernaferrari.sdkmonitor.ui.state.MainUiState
import com.bernaferrari.sdkmonitor.ui.state.SettingsUiState
import com.bernaferrari.sdkmonitor.ui.theme.SdkMonitorTheme
import kotlinx.coroutines.launch
import kotlin.time.Clock

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
    val uriHandler = LocalUriHandler.current
    val prefs by session.preferences.collectAsState()
    val searchQuery by session.searchQuery.collectAsState()
    val sortOption by session.sortOption.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var selectedPackage by remember { mutableStateOf<String?>(null) }
    val appsFlow = remember(roomRepository) { roomRepository.getAppsWithVersions() }
    val allAppsOrNull by appsFlow.collectAsState<List<AppVersion>, List<AppVersion>?>(initial = null)
    val allApps = allAppsOrNull.orEmpty()
    val filteredApps = remember(allApps, prefs, searchQuery, sortOption) {
        AppListLogic.applyListPipeline(allApps, prefs.appFilter, sortOption, prefs.orderBySdk, searchQuery)
    }
    val logsOrNull by remember(roomRepository, prefs.appFilter) {
        roomRepository.getChangeLogs(prefs.appFilter)
    }.collectAsState<List<LogEntry>, List<LogEntry>?>(initial = null)
    val logs = logsOrNull.orEmpty()
    val (distribution, filteredForSdk) = remember(allApps, prefs.appFilter) {
        AnalyticsLogic.sdkDistribution(allApps, prefs.appFilter)
    }

    SdkMonitorTheme(
        themeMode = prefs.themeMode,
        themePalette = prefs.themePalette,
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
            when {
                tab == DemoDestination.Apps.index -> DemoListDetailScaffold(
                    roomRepository = roomRepository,
                    allApps = allApps,
                    selectedPackage = selectedPackage,
                    onSelectedPackageChange = { selectedPackage = it },
                    showEmptyDetailState = true,
                    modifier = modifier,
                ) { selectedPackageName, onAppClick ->
                    MainContent(
                        uiState =
                            if (allAppsOrNull == null) {
                                MainUiState.Loading
                            } else {
                                MainUiState.Success(allApps, filteredApps, filteredApps.size)
                            },
                        searchQuery = searchQuery,
                        appFilter = prefs.appFilter,
                        sortOption = sortOption,
                        selectedPackageName = selectedPackageName,
                        onSearchQueryChange = session::setSearchQuery,
                        onAppFilterChange = session::setAppFilter,
                        onSortOptionChange = session::setSortOption,
                        onAppClick = onAppClick,
                        onRetry = {},
                    )
                }
                tab == DemoDestination.Logs.index -> DemoListDetailScaffold(
                    roomRepository = roomRepository,
                    allApps = allApps,
                    selectedPackage = selectedPackage,
                    onSelectedPackageChange = { selectedPackage = it },
                    showEmptyDetailState = true,
                    modifier = modifier,
                ) { selectedPackageName, onAppClick ->
                    LogsContent(
                        uiState =
                            if (logsOrNull == null) {
                                LogsUiState.Loading
                            } else {
                                LogsUiState.Success(logs, logs.size)
                            },
                        appFilter = prefs.appFilter,
                        selectedPackageName = selectedPackageName,
                        formatTime = ::demoLogDate,
                        onLogClick = { onAppClick(it.packageName) },
                        onRetry = {},
                    )
                }
                else -> DemoListDetailScaffold(
                    roomRepository = roomRepository,
                    allApps = allApps,
                    selectedPackage = selectedPackage,
                    onSelectedPackageChange = { selectedPackage = it },
                    showEmptyDetailState = false,
                    modifier = modifier,
                    aboutContent = { onNavigateBack, isDualPane ->
                        AboutContent(
                            appName = "SDK Monitor",
                            versionName = "2.0.3",
                            onNavigateBack = if (isDualPane) null else onNavigateBack,
                            showTopBar = !isDualPane,
                            onOpenUrl = uriHandler::openUri,
                            onContact = { uriHandler.openUri("mailto:bernaferrari2+sdkmonitor@gmail.com") },
                        )
                    },
                ) { _, onAppClick ->
                    SettingsContent(
                        uiState = SettingsUiState(
                            isLoading = false,
                            preferences =
                                SettingsPreferences(
                                    themeMode = prefs.themeMode,
                                    themePalette = prefs.themePalette,
                                    appFilter = prefs.appFilter,
                                    backgroundSync = prefs.backgroundSync,
                                ),
                            sdkDistribution = distribution,
                            totalApps = filteredForSdk.size,
                            allAppsForSdk = filteredForSdk,
                        ),
                        appVersionLabel = "SDK Monitor",
                        availableThemePalettes = ThemePalette.entries.filterNot { it == ThemePalette.DYNAMIC },
                        onThemeModeChange = session::setThemeMode,
                        onThemePaletteChange = session::setThemePalette,
                        onAppFilterChange = session::setAppFilter,
                        onBackgroundSyncToggle = {},
                        onSetSyncInterval = { _, _ -> },
                        onClearError = {},
                        onNavigateToAbout = { selectedPackage = AboutDestinationKey },
                        onNavigateToAppDetails = onAppClick,
                    )
                }
            }
        }
    }
}

/**
 * The same adaptive list-detail scaffold used by the original Android navigation host.
 * It makes the active detail a second pane when there is room and a full-screen destination
 * on compact windows, for Android, desktop, and WebAssembly alike.
 */
@Composable
private fun DemoListDetailScaffold(
    roomRepository: RoomAppsRepository,
    allApps: List<AppVersion>,
    selectedPackage: String?,
    onSelectedPackageChange: (String?) -> Unit,
    showEmptyDetailState: Boolean,
    modifier: Modifier = Modifier,
    aboutContent: (@Composable (onNavigateBack: () -> Unit, isDualPane: Boolean) -> Unit)? = null,
    listPane: @Composable (selectedPackageName: String?, onAppClick: (String) -> Unit) -> Unit,
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()
    val isDualPane = navigator.scaffoldValue.secondary == PaneAdaptedValue.Expanded
    val versions by remember(roomRepository, selectedPackage) {
        selectedPackage?.let(roomRepository::getAppVersionHistory)
            ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    LaunchedEffect(selectedPackage) {
        if (selectedPackage == null) {
            if (navigator.canNavigateBack()) navigator.navigateBack()
        } else if (navigator.currentDestination?.contentKey != selectedPackage ||
            navigator.currentDestination?.pane != ListDetailPaneScaffoldRole.Detail
        ) {
            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, selectedPackage)
        }
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        scaffoldState = navigator.scaffoldState,
        listPane = {
            AnimatedPane {
                listPane(selectedPackage) { packageName ->
                    onSelectedPackageChange(packageName)
                    scope.launch {
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, packageName)
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                val details = selectedPackage?.let { detailsFromRoom(allApps, it) }
                val navigateBack: () -> Unit = {
                    onSelectedPackageChange(null)
                    scope.launch { navigator.navigateBack() }
                    Unit
                }
                when {
                    selectedPackage == null && showEmptyDetailState -> EmptyDetailState()
                    selectedPackage == null -> Unit
                    selectedPackage == AboutDestinationKey && aboutContent != null ->
                        aboutContent(navigateBack, isDualPane)
                    details == null -> DetailsContent(
                        uiState = DetailsUiState.Error("App not found"),
                        onRetry = { onSelectedPackageChange(null) },
                    )
                    else -> DetailsContent(
                        uiState = DetailsUiState.Success(details, versions),
                        onRetry = { onSelectedPackageChange(null) },
                        onNavigateBack = if (isDualPane) null else {
                            navigateBack
                        },
                    )
                }
            }
        },
        modifier = modifier,
    )
}

private const val AboutDestinationKey = "about"

@Composable
private fun EmptyDetailState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = "Select an app to get started",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun demoLogDate(timestamp: Long): String =
    formatRelativeTimestamp(timestamp, Clock.System.now().toEpochMilliseconds())

private enum class DemoDestination(
    val index: Int,
    val label: String,
    val icon: ImageVector,
) {
    Apps(0, "Apps", MaterialSymbols.Filled.Apps),
    Logs(1, "Logs", MaterialSymbols.Filled.History),
    Settings(2, "Settings", MaterialSymbols.Filled.Settings),
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
