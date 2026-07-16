package com.bernaferrari.sdkmonitor.ui.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Tune
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bernaferrari.sdkmonitor.domain.LocalTimeUnit
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings

enum class SyncPreset(
    val title: (com.bernaferrari.sdkmonitor.ui.platform.SdkStrings) -> String,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val intervalValue: String,
    val localTimeUnit: LocalTimeUnit,
) {
    DAILY({ it.daily }, Icons.Outlined.CalendarToday, Icons.Filled.CalendarToday, "1", LocalTimeUnit.DAYS),
    WEEKLY({ it.weekly }, Icons.Outlined.DateRange, Icons.Filled.DateRange, "7", LocalTimeUnit.DAYS),
    MONTHLY({ it.monthly }, Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth, "30", LocalTimeUnit.DAYS),
    CUSTOM({ it.custom }, Icons.Outlined.Tune, Icons.Filled.Tune, "", LocalTimeUnit.HOURS),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundSyncDialog(
    isEnabled: Boolean = false,
    currentInterval: String = "30",
    currentUnit: LocalTimeUnit = LocalTimeUnit.MINUTES,
    onDismiss: () -> Unit = {},
    onSave: (enabled: Boolean, interval: String, unit: LocalTimeUnit) -> Unit = { _, _, _ -> },
    notificationsEnabled: Boolean = true,
    canRequestPermission: Boolean = false,
    hasRequestedPermission: Boolean = false,
    onRequestPermission: () -> Unit = {},
    onOpenNotificationSettings: () -> Unit = {},
) {
    val s = sdkStrings()
    var enabled by remember(isEnabled) { mutableStateOf(isEnabled) }

    val selectedPreset =
        remember(currentInterval, currentUnit) {
            when {
                currentInterval == "1" && currentUnit == LocalTimeUnit.DAYS -> SyncPreset.DAILY
                currentInterval == "7" && currentUnit == LocalTimeUnit.DAYS -> SyncPreset.WEEKLY
                currentInterval == "30" && currentUnit == LocalTimeUnit.DAYS -> SyncPreset.MONTHLY
                else -> SyncPreset.CUSTOM
            }
        }
    var preset by remember { mutableStateOf(selectedPreset) }
    var customInterval by remember {
        mutableStateOf(if (selectedPreset == SyncPreset.CUSTOM) currentInterval else "1")
    }
    var customUnit by remember {
        mutableStateOf(if (selectedPreset == SyncPreset.CUSTOM) currentUnit else LocalTimeUnit.HOURS)
    }
    var unitMenuExpanded by remember { mutableStateOf(false) }

    fun unitLabel(unit: LocalTimeUnit, value: String): String {
        val one = value.toIntOrNull() == 1
        return when (unit) {
            LocalTimeUnit.MINUTES -> if (one) s.singularMinute else s.pluralMinutes
            LocalTimeUnit.HOURS -> if (one) s.singularHour else s.pluralHours
            LocalTimeUnit.DAYS -> if (one) s.singularDay else s.pluralDays
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier =
                Modifier
                    .padding(24.dp)
                    .widthIn(max = 480.dp)
                    .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(s.syncDialogTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                AnimatedVisibility(
                    visible = !notificationsEnabled,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    if (canRequestPermission && !hasRequestedPermission) {
                        NotificationPermissionRequestCard(onRequestPermission = onRequestPermission)
                    } else {
                        NotificationWarningCard(onOpenSettings = onOpenNotificationSettings)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(s.enableSync, style = MaterialTheme.typography.titleMedium)
                    Switch(checked = enabled, onCheckedChange = { enabled = it })
                }

                AnimatedVisibility(visible = enabled) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(s.interval, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SyncPreset.entries.forEach { option ->
                                val selected = preset == option
                                OutlinedCard(
                                    onClick = { preset = option },
                                    modifier = Modifier.weight(1f),
                                    border =
                                        BorderStroke(
                                            if (selected) 2.dp else 1.dp,
                                            if (selected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outlineVariant
                                            },
                                        ),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Icon(
                                            if (selected) option.iconSelected else option.icon,
                                            contentDescription = null,
                                            tint =
                                                if (selected) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                },
                                        )
                                        Text(
                                            option.title(s),
                                            style = MaterialTheme.typography.labelSmall,
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = preset == SyncPreset.CUSTOM) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                OutlinedTextField(
                                    value = customInterval,
                                    onValueChange = { v -> if (v.all(Char::isDigit) && v.length <= 3) customInterval = v },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    label = { Text(s.interval) },
                                )
                                ExposedDropdownMenuBox(
                                    expanded = unitMenuExpanded,
                                    onExpandedChange = { unitMenuExpanded = it },
                                ) {
                                    OutlinedTextField(
                                        value = unitLabel(customUnit, customInterval.ifBlank { "1" }),
                                        onValueChange = {},
                                        readOnly = true,
                                        modifier =
                                            Modifier
                                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                                .weight(1f),
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(unitMenuExpanded) },
                                    )
                                    androidx.compose.material3.DropdownMenu(
                                        expanded = unitMenuExpanded,
                                        onDismissRequest = { unitMenuExpanded = false },
                                    ) {
                                        LocalTimeUnit.entries.forEach { unit ->
                                            DropdownMenuItem(
                                                text = { Text(unitLabel(unit, customInterval.ifBlank { "1" })) },
                                                onClick = {
                                                    customUnit = unit
                                                    unitMenuExpanded = false
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                ) {
                    OutlinedButton(onClick = onDismiss) { Text(s.cancel) }
                    FilledTonalButton(
                        onClick = {
                            val interval =
                                when (preset) {
                                    SyncPreset.CUSTOM -> customInterval.ifBlank { "1" }
                                    else -> preset.intervalValue
                                }
                            val unit =
                                when (preset) {
                                    SyncPreset.CUSTOM -> customUnit
                                    else -> preset.localTimeUnit
                                }
                            onSave(enabled, interval, unit)
                            onDismiss()
                        },
                    ) { Text(s.save) }
                }
            }
        }
    }
}
