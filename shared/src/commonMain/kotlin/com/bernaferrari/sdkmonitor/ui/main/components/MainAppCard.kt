package com.bernaferrari.sdkmonitor.ui.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.logic.StringNormalize
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListCard
import com.bernaferrari.sdkmonitor.ui.components.ExpressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.components.SdkVersionBadge
import com.bernaferrari.sdkmonitor.ui.platform.PlatformAppIcon

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
    position: ExpressiveListItemPosition = ExpressiveListItemPosition.Single,
    endPadding: Dp = 16.dp,
    onClick: () -> Unit = {},
) {
    ExpressiveListCard(
        modifier = modifier,
        isSelected = isSelected,
        position = position,
        endPadding = endPadding,
        onClick = onClick,
    ) {
        PlatformAppIcon(packageName = appVersion.packageName, size = 56.dp)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
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

        SdkVersionBadge(sdkVersion = appVersion.sdkVersion)
    }
}
