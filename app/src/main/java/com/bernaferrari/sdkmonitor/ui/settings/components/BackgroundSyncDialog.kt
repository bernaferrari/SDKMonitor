package com.bernaferrari.sdkmonitor.ui.settings.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.ui.settings.LocalTimeUnit
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

enum class SyncPreset(
    val displayNameRes: Int,
    val shortNameRes: Int,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val intervalValue: String,
    val localTimeUnit: LocalTimeUnit,
) {
    DAILY(
        R.string.daily,
        R.string.daily,
        Icons.Outlined.CalendarToday,
        Icons.Filled.CalendarToday,
        "1",
        LocalTimeUnit.DAYS,
    ),
    WEEKLY(
        R.string.weekly,
        R.string.weekly,
        Icons.Outlined.DateRange,
        Icons.Filled.DateRange,
        "7",
        LocalTimeUnit.DAYS,
    ),
    MONTHLY(
        R.string.monthly,
        R.string.monthly,
        Icons.Outlined.CalendarMonth,
        Icons.Filled.CalendarMonth,
        "30",
        LocalTimeUnit.DAYS,
    ),
    CUSTOM(
        R.string.custom,
        R.string.custom,
        Icons.Outlined.Tune,
        Icons.Filled.Tune,
        "",
        LocalTimeUnit.HOURS,
    ),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundSyncDialog(
    isEnabled: Boolean = false,
    currentInterval: String = "30",
    currentUnit: LocalTimeUnit = LocalTimeUnit.MINUTES,
    onDismiss: () -> Unit = {},
    onSave: (enabled: Boolean, interval: String, unit: LocalTimeUnit) -> Unit = { _, _, _ -> },
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val notificationManager = NotificationManagerCompat.from(context)

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
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED

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

    var enabled by remember { mutableStateOf(isEnabled) }
    var selectedPreset by remember {
        mutableStateOf(
            when {
                currentInterval == "1" && currentUnit == LocalTimeUnit.DAYS -> SyncPreset.DAILY
                currentInterval == "7" && currentUnit == LocalTimeUnit.DAYS -> SyncPreset.WEEKLY
                currentInterval == "30" && currentUnit == LocalTimeUnit.DAYS -> SyncPreset.MONTHLY
                else -> SyncPreset.CUSTOM
            },
        )
    }
    var customInterval by remember { mutableStateOf(if (selectedPreset == SyncPreset.CUSTOM) currentInterval else "1") }
    var customUnit by remember { mutableStateOf(if (selectedPreset == SyncPreset.CUSTOM) currentUnit else LocalTimeUnit.HOURS) }

    val singularTimeArray = stringArrayResource(R.array.singularTime)
    val pluralTimeArray = stringArrayResource(R.array.pluralTime)

    // Helper function to get the correct time unit display name
    fun getTimeUnitDisplayName(
        unit: LocalTimeUnit,
        value: Int,
    ): String =
        if (value == 1) {
            singularTimeArray[unit.code]
        } else {
            pluralTimeArray[unit.code]
        }

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false,
            ),
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp,
        ) {
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp), // Reduced from 20.dp
            ) {
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
                                            putExtra(
                                                Settings.EXTRA_APP_PACKAGE,
                                                context.packageName,
                                            )
                                        }
                                    context.startActivity(intent)
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Fully Clickable Enable/Disable Card
                Card(
                    onClick = { enabled = !enabled }, // Always allow toggle
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                when {
                                    !notificationsEnabled && enabled ->
                                        MaterialTheme.colorScheme.errorContainer.copy(
                                            alpha = 0.4f,
                                        )

                                    !notificationsEnabled ->
                                        MaterialTheme.colorScheme.errorContainer.copy(
                                            alpha = 0.2f,
                                        )

                                    enabled -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                },
                        ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // Icon indicating notification status
                        Icon(
                            imageVector = if (notificationsEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint =
                                if (notificationsEnabled) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = stringResource(R.string.background_sync),
                                style =
                                    MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text =
                                    when {
                                        !notificationsEnabled && enabled ->
                                            stringResource(
                                                R.string.notifications_required_for_background_sync,
                                            )

                                        !notificationsEnabled -> stringResource(R.string.notifications_required_for_background_sync)
                                        enabled -> stringResource(R.string.apps_will_update_automatically)
                                        else -> stringResource(R.string.tap_to_enable_automatic_updates)
                                    },
                                style = MaterialTheme.typography.bodyMedium,
                                color =
                                    if (notificationsEnabled) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    },
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Switch(
                            checked = enabled,
                            onCheckedChange = null, // Disable switch click since card handles it
                            enabled = true, // Always enabled
                        )
                    }
                }

                // Sync Frequency Selection (only when enabled)
                AnimatedVisibility(
                    visible = enabled, // Show when enabled, regardless of notifications
                    enter =
                        expandVertically(
                            animationSpec =
                                tween(
                                    durationMillis = 300,
                                    easing = androidx.compose.animation.core.FastOutSlowInEasing,
                                ),
                        ) +
                            fadeIn(
                                animationSpec =
                                    tween(
                                        durationMillis = 300,
                                        delayMillis = 50,
                                    ),
                            ),
                    exit =
                        fadeOut(
                            animationSpec = tween(durationMillis = 200),
                        ) +
                            shrinkVertically(
                                animationSpec =
                                    tween(
                                        durationMillis = 300,
                                        easing = androidx.compose.animation.core.FastOutSlowInEasing,
                                    ),
                            ),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // Add top spacing within the animated container
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.sync_frequency),
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        // Beautiful Grid Layout with External Descriptions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            listOf(
                                SyncPreset.DAILY,
                                SyncPreset.WEEKLY,
                                SyncPreset.MONTHLY,
                                SyncPreset.CUSTOM,
                            ).forEach { preset ->
                                ElegantSyncToggleWithDescription(
                                    preset = preset,
                                    isSelected = selectedPreset == preset,
                                    onClick = { selectedPreset = preset },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }

                        // Custom Interval Input
                        AnimatedVisibility(
                            visible = selectedPreset == SyncPreset.CUSTOM,
                            enter =
                                expandVertically(
                                    animationSpec =
                                        tween(
                                            durationMillis = 250,
                                            easing = androidx.compose.animation.core.FastOutSlowInEasing,
                                        ),
                                ) +
                                    fadeIn(
                                        animationSpec =
                                            tween(
                                                durationMillis = 250,
                                                delayMillis = 100,
                                            ),
                                    ),
                            exit =
                                fadeOut(
                                    animationSpec = tween(durationMillis = 150),
                                ) +
                                    shrinkVertically(
                                        animationSpec =
                                            tween(
                                                durationMillis = 250,
                                                easing = androidx.compose.animation.core.FastOutSlowInEasing,
                                            ),
                                    ),
                        ) {
                            Card(
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            MaterialTheme.colorScheme.tertiaryContainer.copy(
                                                alpha = 0.3f,
                                            ),
                                    ),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            text = stringResource(R.string.set_custom_interval),
                                            style =
                                                MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                ),
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.Top,
                                    ) {
                                        OutlinedTextField(
                                            value = customInterval,
                                            onValueChange = { value ->
                                                if (value.all { it.isDigit() } && value.length <= 2) {
                                                    customInterval = value
                                                }
                                            },
                                            label = { Text(stringResource(R.string.every)) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            supportingText = {
                                                if (customInterval.isEmpty() || customInterval.toIntOrNull() == null) {
                                                    Text(
                                                        stringResource(R.string.required),
                                                        color = MaterialTheme.colorScheme.error,
                                                    )
                                                }
                                            },
                                            isError = customInterval.isEmpty() || customInterval.toIntOrNull() == null,
                                        )

                                        var expanded by remember { mutableStateOf(false) }

                                        ExposedDropdownMenuBox(
                                            expanded = expanded,
                                            onExpandedChange = { expanded = it },
                                            modifier = Modifier.weight(1f),
                                        ) {
                                            OutlinedTextField(
                                                value =
                                                    getTimeUnitDisplayName(
                                                        customUnit,
                                                        customInterval.toIntOrNull() ?: 1,
                                                    ),
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text(stringResource(R.string.unit)) },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = expanded,
                                                    )
                                                },
                                                modifier =
                                                    Modifier.menuAnchor(
                                                        ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                                        enabled,
                                                    ),
                                            )

                                            ExposedDropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                            ) {
                                                LocalTimeUnit.entries.forEach { unit ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                getTimeUnitDisplayName(
                                                                    unit,
                                                                    customInterval.toIntOrNull()
                                                                        ?: 1,
                                                                ),
                                                            )
                                                        },
                                                        onClick = {
                                                            customUnit = unit
                                                            expanded = false
                                                        },
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    FilledTonalButton(
                        onClick = {
                            val (interval, unit) =
                                if (enabled) { // Remove notification check here
                                    when (selectedPreset) {
                                        SyncPreset.CUSTOM -> {
                                            val validInterval = customInterval.toIntOrNull()
                                            if (validInterval != null && validInterval > 0) {
                                                Pair(customInterval, customUnit)
                                            } else {
                                                Pair("1", LocalTimeUnit.HOURS)
                                            }
                                        }

                                        else ->
                                            Pair(
                                                selectedPreset.intervalValue,
                                                selectedPreset.localTimeUnit,
                                            )
                                    }
                                } else {
                                    Pair("0", LocalTimeUnit.HOURS)
                                }

                            // Call onSave with the new values - this will trigger scheduling in ViewModel
                            onSave(enabled, interval, unit)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        enabled =
                            if (!enabled) {
                                true
                            } else if (selectedPreset == SyncPreset.CUSTOM) {
                                customInterval.isNotEmpty() && customInterval.toIntOrNull() != null
                            } else {
                                true
                            },
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationPermissionRequestCard(
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationAdd,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.enable_notifications),
                        style =
                            MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    Text(
                        text = stringResource(R.string.notifications_help_background_sync),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                FilledTonalButton(
                    onClick = onRequestPermission,
                    colors =
                        ButtonDefaults.filledTonalButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(R.string.allow_notifications),
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationWarningCard(
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit = {},
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
            ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.notifications_disabled),
                        style =
                            MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                        color = MaterialTheme.colorScheme.error,
                    )

                    Text(
                        text = stringResource(R.string.background_sync_requires_notifications),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                FilledTonalButton(
                    onClick = {
                        val intent =
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                        context.startActivity(intent)
                        onOpenSettings()
                    },
                    colors =
                        ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(R.string.open_settings),
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ElegantSyncToggleWithDescription(
    preset: SyncPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Animate corner radius: 16.dp (rounded) -> 32.dp (circular)
    val cornerRadius by animateDpAsState(
        targetValue = if (isSelected) 32.dp else 16.dp,
        animationSpec = tween(durationMillis = 300),
        label = "cornerRadius",
    )

    // Animate border width for extra feedback
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 300),
        label = "borderWidth",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedCard(
            onClick = onClick,
            modifier = Modifier.height(64.dp),
            shape = RoundedCornerShape(cornerRadius),
            colors =
                CardDefaults.outlinedCardColors(
                    containerColor =
                        if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                ),
            border =
                BorderStroke(
                    width = borderWidth,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.inversePrimary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = if (isSelected) preset.iconSelected else preset.icon,
                    contentDescription = stringResource(preset.displayNameRes),
                    modifier = Modifier.size(20.dp),
                    tint =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }

        // Description outside the card
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(preset.shortNameRes),
            style = MaterialTheme.typography.labelSmall,
            color =
                if (isSelected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BackgroundSyncDialogPreview() {
    SDKMonitorTheme {
        BackgroundSyncDialog(
            isEnabled = true,
            currentInterval = "7",
            currentUnit = LocalTimeUnit.DAYS,
        )
    }
}
