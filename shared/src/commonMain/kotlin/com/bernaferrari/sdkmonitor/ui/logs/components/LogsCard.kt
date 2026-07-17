package com.bernaferrari.sdkmonitor.ui.logs.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListCard
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.components.SdkVersionBadge
import com.bernaferrari.sdkmonitor.ui.platform.PlatformAppIcon

@Composable
fun LogsCard(
    log: LogEntry,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    position: ExpressiveListItemPosition = ExpressiveListItemPosition.Single,
    onClick: () -> Unit = {},
) {
    ExpressiveListCard(
        modifier = modifier,
        isSelected = isSelected,
        position = position,
        onClick = onClick,
    ) {
        PlatformAppIcon(packageName = log.packageName, size = 56.dp)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = log.appName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (log.oldSdk != null && log.oldSdk != log.newSdk) {
                Text(
                    text = "Previously SDK ${log.oldSdk}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }

        SdkVersionBadge(sdkVersion = log.newSdk)
    }
}
