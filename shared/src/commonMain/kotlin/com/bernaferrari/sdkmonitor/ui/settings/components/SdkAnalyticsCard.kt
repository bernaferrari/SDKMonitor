package com.bernaferrari.sdkmonitor.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.SdkDistribution
import com.bernaferrari.sdkmonitor.ui.platform.SdkStrings
import com.bernaferrari.sdkmonitor.ui.platform.apiToComposeColor
import com.bernaferrari.sdkmonitor.ui.platform.apiToVersionName
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings

@Composable
fun SdkAnalyticsCard(
    sdkDistribution: List<SdkDistribution>,
    totalApps: Int,
    modifier: Modifier = Modifier,
    onSdkClick: (Int) -> Unit = {},
) {
    val s = sdkStrings()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = s.analytics,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AnalyticsStat(
                            value = totalApps.toString(),
                            label = s.appsCount,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        AnalyticsStat(
                            value = sdkDistribution.size.toString(),
                            label = "SDKs",
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }

            SdkBarChart(
                data = sdkDistribution,
                onSdkClick = onSdkClick,
                modifier = Modifier.fillMaxWidth().height(160.dp),
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(sdkDistribution, key = { it.sdkVersion }) { item ->
                    SdkLegendItem(
                        item = item,
                        appsLabel = s.appsCount,
                        onClick = { onSdkClick(item.sdkVersion) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsStat(
    value: String,
    label: String,
    containerColor: Color,
    contentColor: Color,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(shape = RoundedCornerShape(6.dp), color = containerColor) {
            Text(
                text = value,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
        }
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SdkBarChart(
    data: List<SdkDistribution>,
    onSdkClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val maxCount = data.maxOfOrNull { it.appCount }?.coerceAtLeast(1) ?: 1
    BoxWithConstraints(modifier = modifier) {
        val spacing = 8.dp
        val visibleItems = data.size.coerceIn(1, 5)
        val availableWidth = maxWidth - 40.dp
        val itemWidth = ((availableWidth - spacing * (visibleItems - 1)) / visibleItems).coerceIn(44.dp, 88.dp)

        LazyRow(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            items(data, key = { it.sdkVersion }) { item ->
                val color = item.sdkVersion.apiToComposeColor()
                val fraction = (item.appCount.toFloat() / maxCount).coerceIn(0.06f, 1f)
                Column(
                    modifier =
                        Modifier
                            .width(itemWidth)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSdkClick(item.sdkVersion) }
                            .padding(horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .width(itemWidth * 0.62f)
                                    .fillMaxHeight(fraction)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 8.dp,
                                            topEnd = 8.dp,
                                            bottomStart = 4.dp,
                                            bottomEnd = 4.dp,
                                        ),
                                    ).background(
                                        Brush.verticalGradient(
                                            listOf(color, color.copy(alpha = 0.68f)),
                                        ),
                                    ),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            if (fraction >= 0.18f) {
                                Text(
                                    text = item.appCount.toString(),
                                    modifier = Modifier.padding(top = 5.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                    Text(
                        text = item.sdkVersion.toString(),
                        modifier = Modifier.padding(top = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun SdkLegendItem(
    item: SdkDistribution,
    appsLabel: String,
    onClick: () -> Unit,
) {
    val color = item.sdkVersion.apiToComposeColor()
    Surface(
        onClick = onClick,
        modifier = Modifier.width(112.dp),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Surface(shape = RoundedCornerShape(8.dp), color = color) {
                Text(
                    text = item.sdkVersion.toString(),
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            Text(
                text = item.sdkVersion.apiToVersionName(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
                maxLines = 1,
            )
            Text(
                text = "${item.appCount} ${if (item.appCount == 1) "app" else appsLabel}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${(item.percentage * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun AppFilterSelector(
    currentFilter: AppFilter,
    onFilterChange: (AppFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = sdkStrings()
    var showFilterMenu by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Surface(
            onClick = { showFilterMenu = true },
            modifier = Modifier.defaultMinSize(minHeight = 36.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = currentFilter.icon(),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = currentFilter.label(s),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        DropdownMenu(
            expanded = showFilterMenu,
            onDismissRequest = { showFilterMenu = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Text(
                text = s.appFilter,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            AppFilter.entries.forEach { filter ->
                val isSelected = currentFilter == filter
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = filter.icon(),
                            contentDescription = null,
                            tint =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                        )
                    },
                    text = {
                        Text(
                            text = filter.label(s),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                        )
                    },
                    onClick = {
                        onFilterChange(filter)
                        showFilterMenu = false
                    },
                )
            }
        }
    }
}

private fun AppFilter.icon(): ImageVector =
    when (this) {
        AppFilter.ALL_APPS -> Icons.Outlined.Apps
        AppFilter.USER_APPS -> Icons.Outlined.Person
        AppFilter.SYSTEM_APPS -> Icons.Default.Android
    }

private fun AppFilter.label(strings: SdkStrings): String =
    when (this) {
        AppFilter.ALL_APPS -> strings.allApps
        AppFilter.USER_APPS -> strings.userApps
        AppFilter.SYSTEM_APPS -> strings.systemApps
    }

@Composable
fun SdkAnalyticsPlaceholder(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().height(160.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun SdkAnalyticsEmptyState(modifier: Modifier = Modifier) {
    val s = sdkStrings()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(s.noAnalyticsData, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Text(
                s.noAnalyticsSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
