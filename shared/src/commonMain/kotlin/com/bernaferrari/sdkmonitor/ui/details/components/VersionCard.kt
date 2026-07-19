package com.bernaferrari.sdkmonitor.ui.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    val railColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)
    val cardShape = versionHistoryItemShape(isFirst = isLatest, isLast = isLast)
    val s = sdkStrings()

    Row(
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(
            modifier = Modifier.width(24.dp).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(if (isLatest) MaterialTheme.colorScheme.surface else railColor),
            )
            Surface(
                modifier = Modifier.size(18.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier =
                            Modifier
                                .size(if (isLatest) 12.dp else 10.dp)
                                .background(if (isLatest) apiColor else railColor, CircleShape),
                    )
                }
            }
            Box(
                modifier =
                    Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (isLast) MaterialTheme.colorScheme.surface else railColor),
            )
        }

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(bottom = if (isLast) 0.dp else 2.dp),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 90.dp),
                shape = cardShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = versionInfo.versionName.ifBlank { versionInfo.versionCode.toString() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isLatest) FontWeight.Bold else FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (versionInfo.lastUpdateTime.isNotBlank()) {
                                Text(
                                    text = versionInfo.lastUpdateTime,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            if (isLatest) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                ) {
                                    Text(
                                        text = s.latest,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    )
                                }
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = apiColor.copy(alpha = 0.12f),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "API",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = apiColor,
                            )
                            Text(
                                text = versionInfo.sdkVersion.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = apiColor,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Matches the expressive segmented-list shape used for settings rows. */
@Composable
private fun versionHistoryItemShape(
    isFirst: Boolean,
    isLast: Boolean,
): Shape {
    val base = MaterialTheme.shapes.small as? RoundedCornerShape ?: return MaterialTheme.shapes.small
    val emphasis = MaterialTheme.shapes.large as? RoundedCornerShape ?: return MaterialTheme.shapes.small

    return RoundedCornerShape(
        topStart = if (isFirst) emphasis.topStart else base.topStart,
        topEnd = if (isFirst) emphasis.topEnd else base.topEnd,
        bottomEnd = if (isLast) emphasis.bottomEnd else base.bottomEnd,
        bottomStart = if (isLast) emphasis.bottomStart else base.bottomStart,
    )
}
