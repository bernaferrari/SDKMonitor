package com.bernaferrari.sdkmonitor.ui.settings

import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListCard
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.components.expressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.platform.PlatformAppIcon
import com.bernaferrari.sdkmonitor.ui.platform.apiToComposeColor
import com.bernaferrari.sdkmonitor.ui.platform.apiToVersionName
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings

@Composable
fun SdkDetailDialog(
    sdkVersion: Int,
    apps: List<AppVersion>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onAppClick: (String) -> Unit = {},
) {
    val s = sdkStrings()
    val apiColor = sdkVersion.apiToComposeColor()
    val sortedApps = remember(apps) { apps.sortedBy { it.title.lowercase() } }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier =
                modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .heightIn(max = 680.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 12.dp,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SdkDialogHeader(
                    sdkVersion = sdkVersion,
                    apiColor = apiColor,
                    onDismiss = onDismiss,
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 12.dp, top = 18.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = s.installedApps,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ) {
                        Text(
                            text = apps.size.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    }
                }

                if (sortedApps.isEmpty()) {
                    EmptySdkApps(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                        contentPadding = PaddingValues(bottom = 12.dp),
                    ) {
                        itemsIndexed(sortedApps, key = { _, app -> app.packageName }) { index, app ->
                            SdkAppRow(
                                app = app,
                                position = expressiveListItemPosition(index, sortedApps.lastIndex),
                                onClick = { onAppClick(app.packageName) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SdkDialogHeader(
    sdkVersion: Int,
    apiColor: Color,
    onDismiss: () -> Unit,
) {
    val s = sdkStrings()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = apiColor.copy(alpha = 0.1f),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 20.dp, end = 12.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(22.dp),
                color = apiColor.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, apiColor.copy(alpha = 0.35f)),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "API",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = apiColor,
                    )
                    Text(
                        text = sdkVersion.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = apiColor,
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = sdkVersion.apiToVersionName(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = s.targetSdk,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(onClick = onDismiss) {
                Icon(MaterialSymbols.Filled.Close, contentDescription = s.closeSdkDetails)
            }
        }
    }
}

@Composable
private fun SdkAppRow(
    app: AppVersion,
    position: ExpressiveListItemPosition,
    onClick: () -> Unit,
) {
    ExpressiveListCard(
        isSelected = false,
        position = position,
        onClick = onClick,
        endPadding = 16.dp,
    ) {
        PlatformAppIcon(packageName = app.packageName, size = 40.dp)
        Text(
            text = app.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = MaterialSymbols.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptySdkApps(modifier: Modifier = Modifier) {
    val s = sdkStrings()
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = MaterialSymbols.Outlined.Apps,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = s.noAppsFound,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = s.noInstalledAppsTargetSdk,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
