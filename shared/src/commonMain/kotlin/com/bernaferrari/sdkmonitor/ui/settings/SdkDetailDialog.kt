package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListCard
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.components.SdkSeal
import com.bernaferrari.sdkmonitor.ui.components.StatusMessage
import com.bernaferrari.sdkmonitor.ui.components.expressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols
import com.bernaferrari.sdkmonitor.ui.platform.PlatformAppIcon
import com.bernaferrari.sdkmonitor.ui.platform.apiToComposeColor
import com.bernaferrari.sdkmonitor.ui.platform.apiToVersionName
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings

/**
 * The apps that target one SDK, in a modal bottom sheet: it rises from the chart the user just tapped and opens
 * fully, as tall as its list. On large screens the sheet keeps M3's 640dp width, centered.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SdkDetailDialog(
    sdkVersion: Int,
    apps: List<AppVersion>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onAppClick: (String) -> Unit = {},
) {
    val s = sdkStrings()
    val sortedApps = remember(apps) { apps.sortedBy { it.title.lowercase() } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        // Opens all the way: as tall as its list, up to the full screen, with no half-open stop.
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        SdkDialogHeader(sdkVersion = sdkVersion)

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = s.installedApps,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
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
            EmptySdkApps()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 24.dp),
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

@Composable
private fun SdkDialogHeader(sdkVersion: Int) {
    val s = sdkStrings()
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SdkSeal(
            level = sdkVersion,
            size = 72.dp,
            fill = sdkVersion.apiToComposeColor(),
            textColor = Color.White,
            textStyle = MaterialTheme.typography.headlineSmall,
        )
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
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
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
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
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        StatusMessage(icon = MaterialSymbols.Outlined.Apps, title = s.noAppsFound, body = s.noInstalledAppsTargetSdk)
    }
}
