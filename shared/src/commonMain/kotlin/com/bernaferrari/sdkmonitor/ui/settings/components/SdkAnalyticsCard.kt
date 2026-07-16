package com.bernaferrari.sdkmonitor.ui.settings.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.SdkDistribution
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
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "chart_animation",
    )
    val maxCount = sdkDistribution.maxOfOrNull { it.appCount }?.coerceAtLeast(1) ?: 1

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(s.analytics, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                sdkDistribution.forEach { item ->
                    val color = item.sdkVersion.apiToComposeColor()
                    val fraction = (item.appCount.toFloat() / maxCount) * animationProgress
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onSdkClick(item.sdkVersion) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        Text(
                            text = item.appCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = color,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(fraction.coerceIn(0.08f, 1f))
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(color.copy(alpha = 0.85f)),
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.sdkVersion.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = item.sdkVersion.apiToVersionName(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
            ) {
                items(sdkDistribution.sortedByDescending { it.sdkVersion }, key = { it.sdkVersion }) { item ->
                    SdkLegendItem(item, s.appsCount, onClick = { onSdkClick(item.sdkVersion) })
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
private fun SdkLegendItem(item: SdkDistribution, appsLabel: String, onClick: () -> Unit) {
    val color = item.sdkVersion.apiToComposeColor()
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color,
            ) {
                Text(
                    text = item.sdkVersion.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            Text(item.sdkVersion.apiToVersionName(), style = MaterialTheme.typography.labelSmall, color = color)
            Text("${item.appCount} ${if (item.appCount == 1) "app" else appsLabel}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text("${(item.percentage * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
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
