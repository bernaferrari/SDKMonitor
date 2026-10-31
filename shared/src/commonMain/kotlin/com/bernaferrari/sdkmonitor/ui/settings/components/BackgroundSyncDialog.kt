package com.bernaferrari.sdkmonitor.ui.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bernaferrari.sdkmonitor.domain.LocalTimeUnit
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.components.expressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings

enum class SyncPreset(
    val title: (com.bernaferrari.sdkmonitor.ui.platform.SdkStrings) -> String,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val intervalValue: String,
    val localTimeUnit: LocalTimeUnit,
) {
    DAILY({ it.daily }, MaterialSymbols.Outlined.CalendarToday, MaterialSymbols.Filled.CalendarToday, "1", LocalTimeUnit.DAYS),
    WEEKLY({ it.weekly }, MaterialSymbols.Outlined.DateRange, MaterialSymbols.Filled.DateRange, "7", LocalTimeUnit.DAYS),
    MONTHLY({ it.monthly }, MaterialSymbols.Outlined.CalendarMonth, MaterialSymbols.Filled.CalendarMonth, "30", LocalTimeUnit.DAYS),
    CUSTOM({ it.custom }, MaterialSymbols.Outlined.Tune, MaterialSymbols.Filled.Tune, "", LocalTimeUnit.HOURS),
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SyncHeader(enabled = enabled, title = s.syncDialogTitle, description = s.backgroundSyncDescription)

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

                // On or off: one large row, tinted when on.
                Surface(
                    onClick = { enabled = !enabled },
                    shape = RoundedCornerShape(20.dp),
                    color = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest,
                    contentColor = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(s.enableSync, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Switch(
                            checked = enabled,
                            onCheckedChange = null,
                            thumbContent = {
                                if (enabled) Icon(MaterialSymbols.Filled.Check, contentDescription = null, modifier = Modifier.size(SwitchDefaults.IconSize))
                            },
                        )
                    }
                }

                AnimatedVisibility(visible = enabled) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            s.interval,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                        )
                        // How often: a connected list, the chosen row tinted and checked.
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            SyncPreset.entries.forEachIndexed { index, option ->
                                SyncPresetRow(
                                    title = option.title(s),
                                    icon = if (preset == option) option.iconSelected else option.icon,
                                    selected = preset == option,
                                    position = expressiveListItemPosition(index, SyncPreset.entries.lastIndex),
                                    onClick = { preset = option },
                                )
                            }
                        }

                        AnimatedVisibility(visible = preset == SyncPreset.CUSTOM) {
                            Column(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                OutlinedTextField(
                                    value = customInterval,
                                    onValueChange = { value ->
                                        if (value.all(Char::isDigit) && value.length <= 3) customInterval = value
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    label = { Text(s.every) },
                                    isError = customInterval.isBlank() || customInterval == "0",
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                                    LocalTimeUnit.entries.forEachIndexed { index, unit ->
                                        ToggleButton(
                                            checked = customUnit == unit,
                                            onCheckedChange = { customUnit = unit },
                                            modifier = Modifier.weight(1f),
                                            colors = ToggleButtonDefaults.colors(
                                                checkedContainerColor = MaterialTheme.colorScheme.primary,
                                                checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            ),
                                            shapes =
                                                when (index) {
                                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                                    LocalTimeUnit.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                                },
                                        ) {
                                            Text(unitLabel(unit, customInterval.ifBlank { "1" }), maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(onClick = onDismiss) { Text(s.cancel) }
                    Button(
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

/** The dialog's crest: sync in a scalloped seal, bright when checking is on, quiet when off. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SyncHeader(
    enabled: Boolean,
    title: String,
    description: String,
) {
    val scheme = MaterialTheme.colorScheme
    val seal by animateColorAsState(if (enabled) scheme.primary else scheme.outline, label = "sync_seal")
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(72.dp).background(seal, MaterialShapes.Cookie12Sided.toShape()),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (enabled) MaterialSymbols.Filled.Sync else MaterialSymbols.Filled.SyncDisabled,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = scheme.surface,
            )
        }
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SyncPresetRow(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    position: ExpressiveListItemPosition,
    onClick: () -> Unit,
) {
    val outer = 20.dp
    val inner = 6.dp
    val shape =
        RoundedCornerShape(
            topStart = if (position == ExpressiveListItemPosition.First || position == ExpressiveListItemPosition.Single) outer else inner,
            topEnd = if (position == ExpressiveListItemPosition.First || position == ExpressiveListItemPosition.Single) outer else inner,
            bottomStart = if (position == ExpressiveListItemPosition.Last || position == ExpressiveListItemPosition.Single) outer else inner,
            bottomEnd = if (position == ExpressiveListItemPosition.Last || position == ExpressiveListItemPosition.Single) outer else inner,
        )
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = shape,
        color = if (selected) scheme.secondaryContainer else scheme.surfaceContainerLowest,
        contentColor = if (selected) scheme.onSecondaryContainer else scheme.onSurface,
        modifier = Modifier.fillMaxWidth().semantics { this.selected = selected },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null)
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, modifier = Modifier.weight(1f))
            if (selected) Icon(MaterialSymbols.Filled.Check, contentDescription = null)
        }
    }
}
