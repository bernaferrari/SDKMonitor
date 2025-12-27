package com.bernaferrari.sdkmonitor.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bernaferrari.sdkmonitor.BuildConfig
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.ThemeMode
import com.bernaferrari.sdkmonitor.ui.settings.components.AnalyticsSection
import com.bernaferrari.sdkmonitor.ui.settings.components.BackgroundSyncDialog
import com.bernaferrari.sdkmonitor.ui.settings.components.NotificationPermissionRequestCard
import com.bernaferrari.sdkmonitor.ui.settings.components.NotificationWarningCard
import com.bernaferrari.sdkmonitor.ui.settings.components.SdkAnalyticsCard
import com.bernaferrari.sdkmonitor.ui.settings.components.SdkAnalyticsEmptyState
import com.bernaferrari.sdkmonitor.ui.settings.components.SdkAnalyticsPlaceholder
import com.bernaferrari.sdkmonitor.ui.settings.components.SettingsItem
import com.bernaferrari.sdkmonitor.ui.settings.components.SettingsSection
import com.bernaferrari.sdkmonitor.ui.settings.components.ThemeModeToggle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAppDetails: (String) -> Unit,
    onNavigateToAbout: (() -> Unit)? = null,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val notificationManager = NotificationManagerCompat.from(context)

    val uiState by viewModel.uiState.collectAsState()
    var showSyncDialog by remember { mutableStateOf(false) }
    var selectedSdkVersion by remember { mutableIntStateOf(0) }
    var showSdkDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(notificationManager.areNotificationsEnabled()) }
    var hasRequestedPermission by remember { mutableStateOf(false) }

    // Permission launcher for Android 13+
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            notificationsEnabled = isGranted
            hasRequestedPermission = true
        }

    // Check if we can request notification permission (Android 13+)
    val canRequestPermission =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

    // Listen for app resume to refresh notification permission
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    notificationsEnabled = notificationManager.areNotificationsEnabled()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val singularTimeArray = stringArrayResource(R.array.singularTime)
    val pluralTimeArray = stringArrayResource(R.array.pluralTime)

    // Helper function to get the correct time unit display name
    fun getTimeUnitDisplayName(
        unit: LocalTimeUnit,
        value: String,
    ): String {
        val intValue = value.toIntOrNull() ?: 1
        return if (intValue == 1) {
            singularTimeArray[unit.code]
        } else {
            pluralTimeArray[unit.code]
        }
    }

    val surface = MaterialTheme.colorScheme.surface

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_screen_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(R.string.loading_settings),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            uiState.hasError -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Card(
                        modifier = Modifier.padding(24.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.error_loading_settings),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text =
                                    uiState.errorMessage
                                        ?: stringResource(R.string.unknown_error),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            FilledTonalButton(onClick = { viewModel.clearError() }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState()),
                ) {
                    val prefs = uiState.preferences

                    SettingsSection(title = stringResource(R.string.appearance)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ThemeMode.entries.forEach { theme ->
                                ThemeModeToggle(
                                    themeMode = theme,
                                    isSelected = prefs.themeMode == theme,
                                    onClick = { viewModel.updateThemeMode(theme) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }

                    // Analytics Section - Always show, handle states internally
                    AnalyticsSection(
                        title = stringResource(R.string.analytics),
                        currentFilter = prefs.appFilter,
                        onFilterChange = { filter -> viewModel.updateAppFilter(filter) },
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

                    SettingsSection(title = stringResource(R.string.background_sync)) {
                        // Notification Permission Request/Warning
                        AnimatedVisibility(
                            visible = !notificationsEnabled,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut(),
                        ) {
                            Column {
                                if (canRequestPermission && !hasRequestedPermission) {
                                    // Friendly permission request for Android 13+
                                    NotificationPermissionRequestCard(
                                        onRequestPermission = {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        },
                                    )
                                } else {
                                    // Warning card for manual settings or after permission denied
                                    NotificationWarningCard(
                                        onOpenSettings = {
                                            val intent =
                                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                                }
                                            context.startActivity(intent)
                                        },
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        SettingsItem(
                            title = stringResource(R.string.background_sync),
                            subtitle =
                                when {
                                    !notificationsEnabled -> stringResource(R.string.notifications_required_for_background_sync)
                                    prefs.backgroundSync -> {
                                        when {
                                            prefs.syncInterval == "1" && prefs.syncLocalTimeUnit == LocalTimeUnit.DAYS ->
                                                stringResource(R.string.enabled_daily_updates)

                                            prefs.syncInterval == "7" && prefs.syncLocalTimeUnit == LocalTimeUnit.DAYS ->
                                                stringResource(R.string.enabled_weekly_updates)

                                            prefs.syncInterval == "30" && prefs.syncLocalTimeUnit == LocalTimeUnit.DAYS ->
                                                stringResource(R.string.enabled_monthly_updates)

                                            else ->
                                                stringResource(
                                                    R.string.enabled_every,
                                                    prefs.syncInterval,
                                                    getTimeUnitDisplayName(
                                                        prefs.syncLocalTimeUnit,
                                                        prefs.syncInterval,
                                                    ).lowercase(),
                                                )
                                        }
                                    }

                                    else -> stringResource(R.string.tap_to_configure_automatic_updates)
                                },
                            icon = if (prefs.backgroundSync) Icons.Default.Sync else Icons.Default.SyncDisabled,
                            onClick = { showSyncDialog = true },
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // About Section
                    SettingsSection(title = stringResource(R.string.about_section)) {
                        SettingsItem(
                            title =
                                stringResource(
                                    R.string.app_version_format,
                                    stringResource(R.string.app_name),
                                    BuildConfig.VERSION_NAME,
                                ),
                            subtitle = stringResource(R.string.learn_more_about_app),
                            icon = Icons.Default.Info,
                            onClick = {
                                onNavigateToAbout?.invoke()
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Background Sync Dialog - NOW HANDLES EVERYTHING
        if (showSyncDialog) {
            BackgroundSyncDialog(
                isEnabled = uiState.preferences.backgroundSync,
                currentInterval = uiState.preferences.syncInterval,
                currentUnit = uiState.preferences.syncLocalTimeUnit,
                onDismiss = { showSyncDialog = false },
                onSave = { enabled, interval, unit ->
                    // Handle background sync toggle
                    if (enabled != uiState.preferences.backgroundSync) {
                        viewModel.toggleBackgroundSync()
                    }
                    // Handle interval change (will also reschedule if sync is enabled)
                    if (enabled && (interval != uiState.preferences.syncInterval || unit != uiState.preferences.syncLocalTimeUnit)) {
                        viewModel.setSyncInterval(interval, unit)
                    }
                },
            )
        }

        // SDK Detail Dialog - Enhanced for better navigation
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
