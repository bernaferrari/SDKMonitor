package com.bernaferrari.sdkmonitor.ui.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.ui.platform.apiToComposeColor
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings

@Composable
fun VersionCard(
    versionInfo: AppVersion,
    modifier: Modifier = Modifier,
    isLatest: Boolean = false,
    isLast: Boolean = false,
) {
    VersionTimelineEntry(
        modifier = modifier,
        versionInfo = versionInfo,
        isLatest = isLatest,
        isLast = isLast,
    )
}

@Composable
fun VersionTimelineEntry(
    modifier: Modifier = Modifier,
    versionInfo: AppVersion,
    isLatest: Boolean = false,
    isLast: Boolean = false,
) {
    val apiColor = versionInfo.sdkVersion.apiToComposeColor()
    val s = sdkStrings()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier =
                    Modifier
                        .size(12.dp)
                        .background(
                            if (isLatest) apiColor else MaterialTheme.colorScheme.outlineVariant,
                            CircleShape,
                        ),
            )
            if (!isLast) {
                Box(
                    modifier =
                        Modifier
                            .width(2.dp)
                            .height(48.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                )
            }
        }

        Surface(
            modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 8.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = versionInfo.versionName.ifBlank { versionInfo.versionCode.toString() },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isLatest) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (versionInfo.lastUpdateTime.isNotBlank()) {
                        Text(
                            text = versionInfo.lastUpdateTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (isLatest) {
                        Text(
                            text = s.version,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Box(
                    modifier =
                        Modifier
                            .background(apiColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = versionInfo.sdkVersion.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = apiColor,
                    )
                }
            }
        }
    }
}
