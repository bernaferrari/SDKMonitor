package com.bernaferrari.sdkmonitor.ui.logs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.ui.components.rememberCachedAppIcon
import com.bernaferrari.sdkmonitor.ui.logs.formatLogTime
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

@Composable
fun LogsCard(
    modifier: Modifier = Modifier,
    log: LogEntry,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    isLast: Boolean = false,
) {
    val context = LocalContext.current
    val apiColor = Color(log.newSdk.apiToColor())
    val apiDescription = log.newSdk.apiToVersion()

    Column {
        Surface(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            color =
                if (isSelected) {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                } else {
                    MaterialTheme.colorScheme.surface
                },
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    // Use centralized cached icon
                    val iconData = rememberCachedAppIcon(log.packageName)
                    
                    AsyncImage(
                        model =
                            ImageRequest
                                .Builder(context)
                                .data(iconData ?: R.drawable.ic_android)
                                .crossfade(true)
                                .build(),
                        contentDescription = "App icon",
                        modifier = Modifier.size(56.dp),
                    )
                }

                // App information
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = log.appName,
                        style =
                            MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = formatLogTime(log.timestamp, context),
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

//                    if (hasVersionChange) {
//                        Surface(
//                            shape = RoundedCornerShape(8.dp),
//                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
//                        ) {
//                            Text(
//                                text = "v${log.newVersion}",
//                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//                                style = MaterialTheme.typography.labelMedium.copy(
//                                    fontWeight = FontWeight.Bold
//                                ),
//                                color = MaterialTheme.colorScheme.secondary,
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                        }
//                    }
                    }
                }

                // SDK display
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    ApiVersionBadge(
                        apiDescription = apiDescription,
                        apiColor = apiColor,
                    )

                    SdkTransitionBadge(
                        oldSdk = log.oldSdk,
                        newSdk = log.newSdk,
                        apiColor = apiColor,
                    )
                }
            }
        }

        // Subtle divider line - only show if not last item
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 88.dp, end = 16.dp)
                    .height(0.5.dp)
                    .background(if (!isLast && !isSelected) MaterialTheme.colorScheme.outlineVariant else Color.Transparent),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LogCardPreview() {
    SDKMonitorTheme {
        LogsCard(
            log =
                LogEntry(
                    id = 1,
                    packageName = "com.whatsapp",
                    appName = "WhatsApp Messenger",
                    oldSdk = 31,
                    newSdk = 34,
                    oldVersion = "2.24.1.74",
                    newVersion = "2.24.1.75",
                    timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                ),
        )
    }
}

@Composable
private fun ApiVersionBadge(
    apiDescription: String,
    apiColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = apiColor.copy(alpha = 0.12f),
        modifier = modifier,
    ) {
        Text(
            text = apiDescription,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                ),
            color = apiColor,
        )
    }
}

@Composable
private fun SdkTransitionBadge(
    oldSdk: Int?,
    newSdk: Int,
    apiColor: Color,
    modifier: Modifier = Modifier,
) {
    val hasSdkChange = oldSdk != null && oldSdk != newSdk

    if (hasSdkChange) {
        // Bold SDK transition with API color background
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = apiColor,
            shadowElevation = 4.dp,
            modifier = modifier,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = oldSdk.toString(),
                    style =
                        MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    color = Color.White.copy(alpha = 0.8f),
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Updated to",
                    modifier = Modifier.size(12.dp),
                    tint = Color.White,
                )

                Text(
                    text = newSdk.toString(),
                    style =
                        MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                        ),
                    color = Color.White,
                )
            }
        }
    } else {
        // Current SDK badge (when no change)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = apiColor,
            shadowElevation = 6.dp,
            modifier = modifier,
        ) {
            Text(
                text = newSdk.toString(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                    ),
                color = Color.White,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LogCardDarkPreview() {
    SDKMonitorTheme(darkTheme = true) {
        LogsCard(
            log =
                LogEntry(
                    id = 2,
                    packageName = "com.instagram.android",
                    appName = "Instagram",
                    oldSdk = 28,
                    newSdk = 33,
                    oldVersion = "305.0.0.36.120",
                    newVersion = "305.0.0.37.120",
                    timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LogCardLongVersionPreview() {
    SDKMonitorTheme {
        LogsCard(
            log =
                LogEntry(
                    id = 3,
                    packageName = "com.google.gmail",
                    appName = "Gmail",
                    oldSdk = null,
                    newSdk = 34,
                    oldVersion = "15.0.3.build.4.release.candidate.final",
                    newVersion = "15.0.4.build.1.release.candidate.final.new",
                    timestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                ),
        )
    }
}
