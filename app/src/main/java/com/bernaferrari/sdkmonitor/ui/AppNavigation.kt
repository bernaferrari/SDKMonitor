@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.bernaferrari.sdkmonitor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.ui.details.DetailsScreen
import com.bernaferrari.sdkmonitor.ui.logs.LogsScreen
import com.bernaferrari.sdkmonitor.ui.main.MainScreen
import com.bernaferrari.sdkmonitor.ui.settings.AboutScreen
import com.bernaferrari.sdkmonitor.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    initialPackageName: String? = null,
) {
    // Navigation 3 back stack
    val backStack = rememberNavBackStack(MainTab)

    // Track current tab for bottom navigation highlighting
    val currentTab = backStack.lastOrNull { it is MainTab || it is LogsTab || it is SettingsTab } ?: MainTab

    val bottomNavItems = listOf(
        BottomNavItem.Main,
        BottomNavItem.Logs,
        BottomNavItem.Settings,
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            bottomNavItems.forEach { item ->
                val isSelected = when (item) {
                    BottomNavItem.Main -> currentTab is MainTab
                    BottomNavItem.Logs -> currentTab is LogsTab
                    BottomNavItem.Settings -> currentTab is SettingsTab
                }
                item(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(item.label)) },
                    selected = isSelected,
                    onClick = {
                        // Clear back stack and navigate to new tab
                        val targetTab: NavKey = when (item) {
                            BottomNavItem.Main -> MainTab
                            BottomNavItem.Logs -> LogsTab
                            BottomNavItem.Settings -> SettingsTab
                        }
                        if (currentTab != targetTab) {
                            backStack.clear()
                            backStack.add(targetTab)
                        }
                    },
                )
            }
        },
        modifier = modifier,
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.fillMaxSize(),
            entryProvider = entryProvider {
                entry<MainTab> {
                    MainScreenWithListDetail(
                        appStartupPackageName = initialPackageName,
                        onNavigateToDetails = { packageName ->
                            backStack.add(AppDetails(packageName))
                        },
                    )
                }
                entry<LogsTab> {
                    LogsScreenWithListDetail(
                        onNavigateToDetails = { packageName ->
                            backStack.add(AppDetails(packageName))
                        },
                    )
                }
                entry<SettingsTab> {
                    SettingsScreenWithListDetail(
                        onNavigateToDetails = { packageName ->
                            backStack.add(AppDetails(packageName))
                        },
                        onNavigateToAbout = {
                            backStack.add(About)
                        },
                    )
                }
                entry<AppDetails> { key ->
                    DetailsScreen(
                        packageName = key.packageName,
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        },
                        isTabletSize = isTablet(),
                    )
                }
                entry<About> {
                    AboutScreen(
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        },
                        isTabletSize = isTablet(),
                    )
                }
            },
        )
    }
}

@Composable
private fun MainScreenWithListDetail(
    modifier: Modifier = Modifier,
    appStartupPackageName: String?,
    onNavigateToDetails: (String) -> Unit,
) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()
    var startupDeepLinkApplied by remember { mutableStateOf(false) }

    LaunchedEffect(listDetailNavigator, appStartupPackageName) {
        // Only apply the startup deep link if appStartupPackageName is present
        // and it hasn't been applied for the current active state of this screen.
        if (!appStartupPackageName.isNullOrEmpty() && !startupDeepLinkApplied) {
            val currentDestination = listDetailNavigator.currentDestination
            // Navigate only if not already on the correct detail item and pane.
            if (currentDestination?.contentKey != appStartupPackageName ||
                currentDestination.pane != ListDetailPaneScaffoldRole.Detail
            ) {
                listDetailNavigator.navigateTo(
                    ListDetailPaneScaffoldRole.Detail,
                    appStartupPackageName,
                )
            }
        }
    }

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            AnimatedPane {
                MainScreen(
                    onNavigateToAppDetails = { packageName ->
                        scope.launch {
                            listDetailNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                packageName,
                            )
                        }
                    },
                    selectedPackageName = listDetailNavigator.currentDestination?.contentKey,
                )
            }
        },
        detailPane = {
            AnimatedPane {
                listDetailNavigator.currentDestination?.contentKey?.let { packageName ->
                    DetailsScreen(
                        packageName = packageName,
                        onNavigateBack = {
                            scope.launch {
                                listDetailNavigator.navigateBack()
                            }
                        },
                        isTabletSize = isTablet(),
                    )
                } ?: EmptyDetailState()
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun LogsScreenWithListDetail(
    modifier: Modifier = Modifier,
    onNavigateToDetails: (String) -> Unit,
) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            AnimatedPane {
                LogsScreen(
                    onNavigateToAppDetails = { packageName ->
                        scope.launch {
                            listDetailNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                packageName,
                            )
                        }
                    },
                    selectedPackageName = listDetailNavigator.currentDestination?.contentKey,
                )
            }
        },
        detailPane = {
            AnimatedPane {
                listDetailNavigator.currentDestination?.contentKey?.let { packageName ->
                    DetailsScreen(
                        packageName = packageName,
                        onNavigateBack = {
                            scope.launch {
                                listDetailNavigator.navigateBack()
                            }
                        },
                        isTabletSize = isTablet(),
                    )
                } ?: EmptyDetailState()
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun SettingsScreenWithListDetail(
    modifier: Modifier = Modifier,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToAbout: () -> Unit,
) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            AnimatedPane {
                SettingsScreen(
                    onNavigateToAppDetails = { packageName ->
                        scope.launch {
                            listDetailNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                packageName,
                            )
                        }
                    },
                    onNavigateToAbout = {
                        scope.launch {
                            listDetailNavigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                "about",
                            )
                        }
                    },
                )
            }
        },
        detailPane = {
            AnimatedPane {
                when (val contentKey = listDetailNavigator.currentDestination?.contentKey) {
                    "about" -> {
                        AboutScreen(
                            onNavigateBack = {
                                scope.launch {
                                    listDetailNavigator.navigateBack()
                                }
                            },
                            isTabletSize = isTablet(),
                        )
                    }

                    null -> { // Empty - no default content needed
                    }

                    else -> {
                        DetailsScreen(
                            packageName = contentKey,
                            onNavigateBack = {
                                scope.launch {
                                    listDetailNavigator.navigateBack()
                                }
                            },
                            isTabletSize = isTablet(),
                        )
                    }
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun EmptyDetailState() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(R.string.select_app_to_get_started),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun isTablet(): Boolean {
    val windowInfo = androidx.compose.ui.platform.LocalWindowInfo.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val widthDp = with(density) { windowInfo.containerSize.width.toDp() }
    return widthDp >= 600.dp
}

private sealed class BottomNavItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: Int,
) {
    data object Main : BottomNavItem(Icons.Default.Apps, R.string.main_title)

    data object Logs : BottomNavItem(Icons.Default.History, R.string.logs_title)

    data object Settings : BottomNavItem(Icons.Default.Settings, R.string.settings_title)
}
