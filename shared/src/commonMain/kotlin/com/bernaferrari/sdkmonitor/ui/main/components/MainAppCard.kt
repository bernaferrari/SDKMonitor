package com.bernaferrari.sdkmonitor.ui.main.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.logic.StringNormalize
import com.bernaferrari.sdkmonitor.ui.platform.PlatformAppIcon
import com.bernaferrari.sdkmonitor.ui.platform.apiToComposeColor
import com.bernaferrari.sdkmonitor.ui.platform.apiToVersionName

@Composable
fun createHighlightedText(
    text: String,
    searchQuery: String,
): AnnotatedString {
    if (searchQuery.isBlank()) return AnnotatedString(text)

    val normalizedText = StringNormalize.normalize(text)
    val normalizedQuery = StringNormalize.normalize(searchQuery)

    return buildAnnotatedString {
        var lastIndex = 0
        var startIndex = normalizedText.indexOf(normalizedQuery, lastIndex, ignoreCase = true)

        while (startIndex != -1) {
            append(text.substring(lastIndex, startIndex))
            withStyle(
                style =
                    SpanStyle(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                append(text.substring(startIndex, startIndex + normalizedQuery.length))
            }
            lastIndex = startIndex + normalizedQuery.length
            startIndex = normalizedText.indexOf(normalizedQuery, lastIndex, ignoreCase = true)
        }
        if (lastIndex < text.length) append(text.substring(lastIndex))
    }
}

@Composable
fun MainAppCard(
    appVersion: AppVersion,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    isSelected: Boolean = false,
    isLast: Boolean = false,
    onClick: () -> Unit = {},
) {
    val apiColor = appVersion.sdkVersion.apiToComposeColor()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp),
                                )
                        } else {
                            Modifier
                        },
                    ).clickable(onClick = onClick)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PlatformAppIcon(packageName = appVersion.packageName, size = 56.dp)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = createHighlightedText(appVersion.title, searchQuery),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (appVersion.lastUpdateTime.isNotBlank()) {
                    Text(
                        text = appVersion.lastUpdateTime,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(apiColor.copy(alpha = 0.07f))
                        .border(1.dp, apiColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = appVersion.sdkVersion.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = apiColor,
                    )
                    Text(
                        text = appVersion.sdkVersion.apiToVersionName(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = apiColor,
                        maxLines = 1,
                    )
                }
            }
        }

        if (!isLast) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 88.dp, end = 16.dp)
                        .height(0.5.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant),
            )
        }
    }
}
