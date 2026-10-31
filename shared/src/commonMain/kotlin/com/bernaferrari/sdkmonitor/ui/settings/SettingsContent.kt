package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.ColorStyle
import com.bernaferrari.sdkmonitor.domain.LocalTimeUnit
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.domain.ThemePalette
import com.bernaferrari.sdkmonitor.ui.components.StatusLoading
import com.bernaferrari.sdkmonitor.ui.components.StatusMessage
import com.bernaferrari.sdkmonitor.ui.components.StatusTone
import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings
import com.bernaferrari.sdkmonitor.ui.settings.components.AppMarkTile
import com.bernaferrari.sdkmonitor.ui.settings.components.BackgroundSyncDialog
import com.bernaferrari.sdkmonitor.ui.settings.components.MonitorHero
import com.bernaferrari.sdkmonitor.ui.settings.components.NotificationPermissionRequestCard
import com.bernaferrari.sdkmonitor.ui.settings.components.NotificationWarningCard
import com.bernaferrari.sdkmonitor.ui.settings.components.SettingsItem
import com.bernaferrari.sdkmonitor.ui.settings.components.SettingsSection
import com.bernaferrari.sdkmonitor.ui.settings.components.ThemeAppearanceSelector
import com.bernaferrari.sdkmonitor.ui.state.SettingsUiState

/**
 * Full settings UI in commonMain. Platform supplies notification/permission hooks and VM callbacks.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    appVersionLabel: String,
    availableThemePalettes: List<ThemePalette> = ThemePalette.entries,
    onThemeModeChange: (ThemeMode) -> Unit,
    onThemePaletteChange: (ThemePalette) -> Unit,
    onColorStyleChange: (ColorStyle) -> Unit = {},
    onBackgroundSyncToggle: () -> Unit,
    onSetSyncInterval: (interval: String, unit: LocalTimeUnit) -> Unit,
    onClearError: () -> Unit,
    onNavigateToAbout: () -> Unit,
    isAboutSelected: Boolean = false,
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

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = contentModifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(s.settingsTitle, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    StatusLoading(title = s.loadingSettings)
                }
            }

            uiState.hasError -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    StatusMessage(
                        icon = MaterialSymbols.Filled.Warning,
                        title = s.errorLoadingSettings,
                        body = uiState.errorMessage ?: s.unknownError,
                        tone = StatusTone.Error,
                        action = { FilledTonalButton(onClick = onClearError) { Text(s.retry) } },
                    )
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
                    // What the app is doing for you, first: background checks, how often, over how many apps.
                    val syncStatus =
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
                                        s.enabledEvery
                                            .replace("%1\$s", prefs.syncInterval)
                                            .replace("%2\$s", timeUnitDisplayName(prefs.syncLocalTimeUnit, prefs.syncInterval))
                                    }
                                }
                            }

                            else -> {
                                s.tapToConfigureSync
                            }
                        }
                    MonitorHero(
                        watching = prefs.backgroundSync && notificationsEnabled,
                        title = s.backgroundSync,
                        status = syncStatus,
                        appCount = uiState.totalApps,
                        appsLabel = s.appsCount,
                        onClick = { showSyncDialog = true },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                    Column(Modifier.padding(horizontal = 16.dp)) {
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

                    }

                    SettingsSection(title = s.appearance) {
                        ThemeAppearanceSelector(
                            selectedMode = prefs.themeMode,
                            selectedPalette =
                                prefs.themePalette.takeIf { it in availableThemePalettes }
                                    ?: ThemePalette.EMBER,
                            availablePalettes = availableThemePalettes,
                            onModeSelected = onThemeModeChange,
                            onPaletteSelected = onThemePaletteChange,
                            selectedStyle = prefs.colorStyle,
                            onStyleSelected = onColorStyleChange,
                        )
                    }

                    SettingsSection(title = s.aboutSection) {
                        SettingsItem(
                            title = appVersionLabel,
                            subtitle = s.learnMoreAboutApp,
                            icon = MaterialSymbols.Filled.Info,
                            leading = { AppMarkTile() },
                            onClick = onNavigateToAbout,
                            isSelected = isAboutSelected,
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

    }
}
