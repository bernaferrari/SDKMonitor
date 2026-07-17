package com.bernaferrari.sdkmonitor.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernaferrari.sdkmonitor.BuildConfig
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.ThemePalette
import org.koin.compose.viewmodel.koinViewModel

/**
 * Android shell: permissions/notifications + ViewModel; UI is shared [SettingsContent].
 */
@Composable
fun SettingsScreen(
    onNavigateToAppDetails: (String) -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val notificationManager = NotificationManagerCompat.from(context)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var notificationsEnabled by remember { mutableStateOf(notificationManager.areNotificationsEnabled()) }
    var hasRequestedPermission by remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            notificationsEnabled = isGranted
            hasRequestedPermission = true
        }

    val canRequestPermission =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    notificationsEnabled = notificationManager.areNotificationsEnabled()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val appVersionLabel =
        context.getString(
            R.string.app_version_format,
            context.getString(R.string.app_name),
            BuildConfig.VERSION_NAME,
        )

    SettingsContent(
        uiState = uiState,
        appVersionLabel = appVersionLabel,
        availableThemePalettes =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ThemePalette.entries
            } else {
                ThemePalette.entries.filterNot { it == ThemePalette.DYNAMIC }
            },
        onThemeModeChange = viewModel::updateThemeMode,
        onThemePaletteChange = viewModel::updateThemePalette,
        onAppFilterChange = viewModel::updateAppFilter,
        onBackgroundSyncToggle = viewModel::toggleBackgroundSync,
        onSetSyncInterval = viewModel::setSyncInterval,
        onClearError = viewModel::clearError,
        onNavigateToAbout = onNavigateToAbout,
        onNavigateToAppDetails = onNavigateToAppDetails,
        notificationsEnabled = notificationsEnabled,
        canRequestPermission = canRequestPermission,
        hasRequestedPermission = hasRequestedPermission,
        onRequestPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        onOpenNotificationSettings = {
            val intent =
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
            context.startActivity(intent)
        },
        onPermissionRequested = { hasRequestedPermission = true },
        contentModifier = modifier,
    )
}
