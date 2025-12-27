package com.bernaferrari.sdkmonitor.ui.main.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.extensions.apiToColor
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import com.bernaferrari.sdkmonitor.extensions.normalizeString
import com.bernaferrari.sdkmonitor.ui.components.rememberCachedAppIcon
import com.bernaferrari.sdkmonitor.ui.isTablet
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

@Composable
fun createHighlightedText(
    text: String,
    searchQuery: String,
): AnnotatedString {
    if (searchQuery.isBlank()) {
        return AnnotatedString(text)
    }

    val normalizedText = text.normalizeString()
    val normalizedQuery = searchQuery.normalizeString()

    return buildAnnotatedString {
        var lastIndex = 0
        var startIndex = normalizedText.indexOf(normalizedQuery, lastIndex, ignoreCase = true)

        while (startIndex != -1) {
            // Add text before the match
            append(text.substring(lastIndex, startIndex))

            // Add highlighted match with prominent styling
            withStyle(
                style =
                    SpanStyle(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        // background = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
            ) {
                append(text.substring(startIndex, startIndex + normalizedQuery.length))
            }

            lastIndex = startIndex + normalizedQuery.length
            startIndex = normalizedText.indexOf(normalizedQuery, lastIndex, ignoreCase = true)
        }

        // Add remaining text
        append(text.substring(lastIndex))
    }
}

@Composable
fun MainAppCard(
    modifier: Modifier = Modifier,
    appVersion: AppVersion,
    appIcon: Bitmap? = null,
    showVersionPill: Boolean = true,
    searchQuery: String = "",
    isLast: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val apiColor = Color(appVersion.sdkVersion.apiToColor())
    val apiDescription = appVersion.sdkVersion.apiToVersion()

    Column {
        Row(
            modifier =
                modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (isSelected && isTablet()) {
                            Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                ).border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp),
                                )
                        } else {
                            Modifier
                        },
                    ).clickable { onClick() }
                    .padding(
                        horizontal = 16.dp,
                        vertical = 16.dp,
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // App Icon - clean and modern with better error handling
            Box(
                modifier =
                    Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (appIcon != null) {
                    Image(
                        bitmap = appIcon.asImageBitmap(),
                        contentDescription = "App icon for ${appVersion.title}",
                        modifier =
                            Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp)),
                    )
                } else if (isPreview) {
                    Icon(
                        imageVector = Icons.Outlined.Apps,
                        contentDescription = "App icon for ${appVersion.title}",
                        modifier =
                            Modifier
                                .size(32.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainer,
                                    RoundedCornerShape(16.dp),
                                ).padding(12.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    // Use centralized cached icon
                    val iconData = rememberCachedAppIcon(appVersion.packageName)

                    if (iconData != null) {
                        AsyncImage(
                            model =
                                ImageRequest
                                    .Builder(context)
                                    .data(iconData)
                                    .crossfade(true)
                                    .build(),
                            contentDescription = "App icon for ${appVersion.title}",
                            modifier =
                                Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                        )
                    } else {
                        // App is uninstalled - show placeholder with background
                        Icon(
                            imageVector = Icons.Outlined.Apps,
                            contentDescription = "App icon for ${appVersion.title}",
                            modifier =
                                Modifier
                                    .size(56.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceContainer,
                                        RoundedCornerShape(16.dp),
                                    ).padding(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                    }
                }
            }

            // Content section - takes up remaining space
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                // App title with highlighting
                Text(
                    text = createHighlightedText(appVersion.title, searchQuery),
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // Bottom row with date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Date - subtle and clean
                    Text(
                        text = appVersion.lastUpdateTime,
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                            ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (showVersionPill) {
                // Modern minimalist SDK pill with better contrast - CENTER ALIGNED
                Column(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(apiColor.copy(alpha = 0.07f))
                            .border(
                                width = 1.dp,
                                color = apiColor,
                                shape = RoundedCornerShape(12.dp),
                            ).padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    // Bold SDK number with high contrast
                    Text(
                        text = appVersion.sdkVersion.toString(),
                        style =
                            MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                            ),
                        color = apiColor,
                    )

                    // Clean API description without background
                    Text(
                        text = apiDescription,
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                        color = apiColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
private fun MainAppCardPreview() {
    SDKMonitorTheme {
        MainAppCard(
            appVersion =
                AppVersion(
                    packageName = "com.whatsapp",
                    title = "WhatsApp Messenger",
                    sdkVersion = 33,
                    lastUpdateTime = "3 weeks ago",
                    versionName = "2.24.1.75",
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainAppCardDarkPreview() {
    SDKMonitorTheme(darkTheme = true) {
        MainAppCard(
            appVersion =
                AppVersion(
                    packageName = "com.instagram.android",
                    title = "Instagram",
                    sdkVersion = 28,
                    lastUpdateTime = "1 day ago",
                    versionName = "305.0.0.37.120",
                ),
        )
    }
}
