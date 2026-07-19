package com.bernaferrari.sdkmonitor.ui.settings

import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.LocalTimeUnit
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.domain.ThemePalette
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings
import com.bernaferrari.sdkmonitor.ui.settings.components.AnalyticsSection
import com.bernaferrari.sdkmonitor.ui.settings.components.AppFilterSelector
import com.bernaferrari.sdkmonitor.ui.settings.components.BackgroundSyncDialog
import com.bernaferrari.sdkmonitor.ui.settings.components.NotificationPermissionRequestCard
import com.bernaferrari.sdkmonitor.ui.settings.components.NotificationWarningCard
import com.bernaferrari.sdkmonitor.ui.settings.components.SdkAnalyticsCard
import com.bernaferrari.sdkmonitor.ui.settings.components.SdkAnalyticsEmptyState
import com.bernaferrari.sdkmonitor.ui.settings.components.SdkAnalyticsPlaceholder
import com.bernaferrari.sdkmonitor.ui.settings.components.SettingsItem
import com.bernaferrari.sdkmonitor.ui.settings.components.SettingsSection
import com.bernaferrari.sdkmonitor.ui.settings.components.ThemeAppearanceSelector
import com.bernaferrari.sdkmonitor.ui.state.SettingsUiState

/**
 * Full settings UI in commonMain. Platform supplies notification/permission hooks and VM callbacks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    appVersionLabel: String,
    availableThemePalettes: List<ThemePalette> = ThemePalette.entries,
    onThemeModeChange: (ThemeMode) -> Unit,
    onThemePaletteChange: (ThemePalette) -> Unit,
    onAppFilterChange: (AppFilter) -> Unit,
    onBackgroundSyncToggle: () -> Unit,
    onSetSyncInterval: (interval: String, unit: LocalTimeUnit) -> Unit,
    onClearError: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToAppDetails: (String) -> Unit,
    notificationsEnabled: Boolean = true,
    canRequestPermission: Boolean = false,
    hasRequestedPermission: Boolean = false,
    onRequestPermission: () -> Unit = {},
    onOpenNotificationSettings: () -> Unit = {},
    onPermissionRequested: () -> Unit = {},
    contentModifier: Modifier = Modifier,
) {
    val s = sdkStrings()
    var showSyncDialog by remember { mutableStateOf(false) }
    var selectedSdkVersion by remember { mutableIntStateOf(0) }
    var showSdkDialog by remember { mutableStateOf(false) }

    fun timeUnitDisplayName(
        unit: LocalTimeUnit,
        value: String,
    ): String {
        val one = value.toIntOrNull() == 1
        return when (unit) {
            LocalTimeUnit.MINUTES -> if (one) s.singularMinute else s.pluralMinutes
            LocalTimeUnit.HOURS -> if (one) s.singularHour else s.pluralHours
            LocalTimeUnit.DAYS -> if (one) s.singularDay else s.pluralDays
        }
    }

    Scaffold(
        modifier = contentModifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = s.settingsTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        Text(s.loadingSettings, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            uiState.hasError -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Card(
                        modifier = Modifier.padding(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                s.errorLoadingSettings,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                uiState.errorMessage ?: s.unknownError,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            FilledTonalButton(onClick = onClearError) { Text(s.retry) }
                        }
                    }
                }
            }

            else -> {
                val prefs = uiState.preferences
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState()),
                ) {
                    SettingsSection(title = s.appearance) {
                        ThemeAppearanceSelector(
                            selectedMode = prefs.themeMode,
                            selectedPalette =
                                prefs.themePalette.takeIf { it in availableThemePalettes }
                                    ?: ThemePalette.EMBER,
                            availablePalettes = availableThemePalettes,
                            onModeSelected = onThemeModeChange,
                            onPaletteSelected = onThemePaletteChange,
                        )
                    }

                    AnalyticsSection(
                        title = s.analyticsSection,
                        action = {
                            AppFilterSelector(
                                currentFilter = prefs.appFilter,
                                onFilterChange = onAppFilterChange,
                            )
                        },
                    ) {
                        when {
                            uiState.isAnalyticsLoading -> {
                                SdkAnalyticsPlaceholder()
                            }

                            uiState.totalApps == 0 && !uiState.isAnalyticsLoading -> {
                                SdkAnalyticsEmptyState()
                            }

                            else -> {
                                SdkAnalyticsCard(
                                    sdkDistribution = uiState.sdkDistribution,
                                    totalApps = uiState.totalApps,
                                    onSdkClick = { sdkVersion ->
                                        selectedSdkVersion = sdkVersion
                                        showSdkDialog = true
                                    },
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsSection(title = s.backgroundSync) {
                        AnimatedVisibility(
                            visible = !notificationsEnabled,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut(),
                        ) {
                            Column {
                                if (canRequestPermission && !hasRequestedPermission) {
                                    NotificationPermissionRequestCard(
                                        onRequestPermission = {
                                            onPermissionRequested()
                                            onRequestPermission()
                                        },
                                    )
                                } else {
                                    NotificationWarningCard(onOpenSettings = onOpenNotificationSettings)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        SettingsItem(
                            title = s.backgroundSync,
                            subtitle =
                                when {
                                    !notificationsEnabled -> {
                                        s.notificationsRequired
                                    }

                                    prefs.backgroundSync -> {
                                        when {
                                            prefs.syncInterval == "1" && prefs.syncLocalTimeUnit == LocalTimeUnit.DAYS -> {
                                                s.enabledDaily
                                            }

                                            prefs.syncInterval == "7" && prefs.syncLocalTimeUnit == LocalTimeUnit.DAYS -> {
                                                s.enabledWeekly
                                            }

                                            prefs.syncInterval == "30" && prefs.syncLocalTimeUnit == LocalTimeUnit.DAYS -> {
                                                s.enabledMonthly
                                            }

                                            else -> {
                                                "${s.enabledEvery} ${prefs.syncInterval} ${
                                                    timeUnitDisplayName(
                                                        prefs.syncLocalTimeUnit,
                                                        prefs.syncInterval,
                                                    )
                                                }"
                                            }
                                        }
                                    }

                                    else -> {
                                        s.tapToConfigureSync
                                    }
                                },
                            icon = if (prefs.backgroundSync) MaterialSymbols.Filled.Sync else MaterialSymbols.Filled.SyncDisabled,
                            onClick = { showSyncDialog = true },
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsSection(title = s.aboutSection) {
                        SettingsItem(
                            title = appVersionLabel,
                            subtitle = s.learnMoreAboutApp,
                            icon = MaterialSymbols.Filled.Info,
                            onClick = onNavigateToAbout,
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (showSyncDialog) {
            BackgroundSyncDialog(
                isEnabled = uiState.preferences.backgroundSync,
                currentInterval = uiState.preferences.syncInterval,
                currentUnit = uiState.preferences.syncLocalTimeUnit,
                onDismiss = { showSyncDialog = false },
                onSave = { enabled, interval, unit ->
                    if (enabled != uiState.preferences.backgroundSync) {
                        onBackgroundSyncToggle()
                    }
                    if (enabled &&
                        (
                            interval != uiState.preferences.syncInterval ||
                                unit != uiState.preferences.syncLocalTimeUnit
                        )
                    ) {
                        onSetSyncInterval(interval, unit)
                    }
                },
                notificationsEnabled = notificationsEnabled,
                canRequestPermission = canRequestPermission,
                hasRequestedPermission = hasRequestedPermission,
                onRequestPermission = {
                    onPermissionRequested()
                    onRequestPermission()
                },
                onOpenNotificationSettings = onOpenNotificationSettings,
            )
        }

        if (showSdkDialog) {
            val appsWithSdk = uiState.allAppsForSdk.filter { it.sdkVersion == selectedSdkVersion }
            SdkDetailDialog(
                sdkVersion = selectedSdkVersion,
                apps = appsWithSdk,
                onDismiss = { showSdkDialog = false },
                onAppClick = { packageName ->
                    showSdkDialog = false
                    onNavigateToAppDetails(packageName)
                },
            )
        }
    }
}
