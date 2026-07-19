package com.bernaferrari.sdkmonitor.ui.logs.components

import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListCard
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.platform.PlatformAppIcon
import com.bernaferrari.sdkmonitor.ui.platform.apiToComposeColor
import com.bernaferrari.sdkmonitor.ui.platform.apiToVersionName

@Composable
fun LogsCard(
    log: LogEntry,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    position: ExpressiveListItemPosition = ExpressiveListItemPosition.Single,
    formattedTime: String = "",
    onClick: () -> Unit = {},
) {
    val apiColor = log.newSdk.apiToComposeColor()

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
            if (formattedTime.isNotBlank()) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ApiVersionBadge(color = apiColor, label = log.newSdk.apiToVersionName())
            SdkTransitionBadge(oldSdk = log.oldSdk, newSdk = log.newSdk, color = apiColor)
        }
    }
}

@Composable
private fun ApiVersionBadge(
    color: Color,
    label: String,
) {
    Surface(shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.12f)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

@Composable
private fun SdkTransitionBadge(
    oldSdk: Int?,
    newSdk: Int,
    color: Color,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (oldSdk != null && oldSdk != newSdk) {
                Text(
                    text = oldSdk.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f),
                )
                Icon(
                    imageVector = MaterialSymbols.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.White,
                )
            }
            Text(
                text = newSdk.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )
        }
    }
}
