package com.bernaferrari.sdkmonitor.ui.settings.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols
import com.bernaferrari.sdkmonitor.ui.icons.SdkMonitorMark
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        content()
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    isSwitch: Boolean = false,
    switchValue: Boolean = false,
    onSwitchToggle: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
) {
    val selectionTransition = updateTransition(isSelected, label = "settingsItemSelection")
    val cornerRadius by
        selectionTransition.animateDp(
            transitionSpec = { tween(durationMillis = SettingsSelectionAnimationDurationMillis) },
            label = "cornerRadius",
        ) { selected ->
            if (selected) 20.dp else 16.dp
        }
    val containerColor by
        selectionTransition.animateColor(
            transitionSpec = { tween(durationMillis = SettingsSelectionAnimationDurationMillis) },
            label = "containerColor",
        ) { selected ->
            if (selected) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
        }
    val borderColor by
        selectionTransition.animateColor(
            transitionSpec = { tween(durationMillis = SettingsSelectionAnimationDurationMillis) },
            label = "borderColor",
        ) { selected ->
            if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
            } else {
                Color.Transparent
            }
        }

    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius))
                .clickable {
                    if (isSwitch) {
                        onSwitchToggle?.invoke(!switchValue)
                    } else {
                        onClick?.invoke()
                    }
                }.semantics { selected = isSelected },
        shape = RoundedCornerShape(cornerRadius),
        color = containerColor,
        border = BorderStroke(2.dp, borderColor),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (leading != null) {
                leading()
            } else {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            when {
                isSwitch -> {
                    Switch(checked = switchValue, onCheckedChange = { onSwitchToggle?.invoke(it) })
                }

                onClick != null -> {
                    Icon(
                        MaterialSymbols.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private const val SettingsSelectionAnimationDurationMillis = 150

@Composable
fun AnalyticsSection(
    title: String,
    modifier: Modifier = Modifier,
    action: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            action()
        }
        content()
    }
}

@Composable
fun NotificationPermissionRequestCard(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = sdkStrings()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(s.notificationPermissionTitle, fontWeight = FontWeight.SemiBold)
            Text(s.notificationPermissionBody, style = MaterialTheme.typography.bodySmall)
            androidx.compose.material3.TextButton(onClick = onRequestPermission) {
                Text(s.allowNotifications)
            }
        }
    }
}

@Composable
fun NotificationWarningCard(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = sdkStrings()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(s.notificationWarningTitle, fontWeight = FontWeight.SemiBold)
            Text(s.notificationWarningBody, style = MaterialTheme.typography.bodySmall)
            androidx.compose.material3.TextButton(onClick = onOpenSettings) {
                Text(s.openSettings)
            }
        }
    }
}

/**
 * What SDK Monitor is doing for you: whether it checks apps in the background, how often, and over how many
 * apps. A seal holds the state (sync when watching, sync-off when not); tapping the card sets the schedule.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MonitorHero(
    watching: Boolean,
    title: String,
    status: String,
    appCount: Int,
    appsLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Off is an invitation, not a disabled control: a calmer tone of the same card, never grey.
    val scheme = MaterialTheme.colorScheme
    val container = if (watching) scheme.primaryContainer else scheme.secondaryContainer
    val content = if (watching) scheme.onPrimaryContainer else scheme.onSecondaryContainer
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = container,
        contentColor = content,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .background(
                            if (watching) scheme.primary else scheme.secondary,
                            MaterialShapes.Cookie12Sided.toShape(),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (watching) MaterialSymbols.Filled.Sync else MaterialSymbols.Filled.SyncDisabled,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = if (watching) scheme.onPrimary else scheme.onSecondary,
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(status, style = MaterialTheme.typography.bodyMedium, color = content.copy(alpha = 0.8f))
                if (appCount > 0) {
                    Surface(shape = RoundedCornerShape(8.dp), color = content.copy(alpha = 0.1f)) {
                        Text(
                            "$appCount $appsLabel",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            Icon(MaterialSymbols.Filled.KeyboardArrowRight, contentDescription = null, tint = content.copy(alpha = 0.7f))
        }
    }
}

/** The app's icon, as on the home screen: the identity row of Settings' About. */
@Composable
fun AppMarkTile(modifier: Modifier = Modifier) {
    Icon(
        SdkMonitorMark,
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier.size(48.dp).clip(RoundedCornerShape(14.dp)),
    )
}
