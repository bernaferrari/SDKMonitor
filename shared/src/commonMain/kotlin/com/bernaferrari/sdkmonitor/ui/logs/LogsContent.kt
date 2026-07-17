package com.bernaferrari.sdkmonitor.ui.logs

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.ui.logs.components.LogsCard
import com.bernaferrari.sdkmonitor.ui.components.expressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings
import com.bernaferrari.sdkmonitor.ui.state.LogsUiState
import kotlin.time.Clock

@Composable
fun LogsContent(
    uiState: LogsUiState,
    appFilter: AppFilter = AppFilter.ALL_APPS,
    selectedPackageName: String? = null,
    formatTime: (Long) -> String = { "" },
    onLogClick: (LogEntry) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit = {},
    contentModifier: Modifier = Modifier,
) {
    val s = sdkStrings()

    Scaffold(
        modifier = contentModifier,
        topBar = {
            TopAppBar(
                title = { Text("Change logs", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold) },
            )
        },
    ) { paddingValues ->
        when (uiState) {
            is LogsUiState.Loading -> LoadingState(Modifier.fillMaxSize().padding(paddingValues))

            is LogsUiState.Error -> ErrorState(uiState.message, Modifier.fillMaxSize().padding(paddingValues))

            is LogsUiState.Success -> {
                if (uiState.logs.isEmpty()) {
                    EmptyLogsState(s.noLogsYet, s.noLogsSubtitle, Modifier.fillMaxSize().padding(paddingValues))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
                    ) {
                        item {
                            TimelineHeader(
                                logs = uiState.logs,
                                appFilter = appFilter,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                        uiState.logs.groupBy { formatTime(it.timestamp) }.forEach { (period, logs) ->
                            item(key = "header_$period") {
                                LogPeriodHeader(period, logs.size)
                            }
                            itemsIndexed(logs, key = { _, log -> "${period}_${log.id}" }) { index, log ->
                                LogsCard(
                                    log = log,
                                    isSelected = log.packageName == selectedPackageName,
                                    position = expressiveListItemPosition(index, logs.lastIndex),
                                    onClick = { onLogClick(log) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogPeriodHeader(period: String, count: Int) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 32.dp,
                    end = 16.dp,
                    top = 24.dp,
                    bottom = 8.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Text(period, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) {
            Text(count.toString(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(Modifier.padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 6.dp)
                Text("Loading change history", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                Text("Analyzing app ecosystem", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.padding(32.dp), shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), elevation = CardDefaults.cardElevation(20.dp)) {
            Box(Modifier.fillMaxWidth().background(Brush.radialGradient(listOf(MaterialTheme.colorScheme.error.copy(alpha = 0.05f), MaterialTheme.colorScheme.errorContainer)))) {
                Column(Modifier.padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Surface(modifier = Modifier.size(80.dp), shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.Warning, null, Modifier.size(40.dp), MaterialTheme.colorScheme.error) }
                    }
                    Text("Oops, something went wrong", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer, textAlign = TextAlign.Center)
                    Text(message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun EmptyLogsState(title: String, subtitle: String, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(Modifier.padding(48.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.size(120.dp).background(Brush.radialGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), Color.Transparent)), RoundedCornerShape(32.dp)), contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(80.dp), shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.History, null, Modifier.size(40.dp), MaterialTheme.colorScheme.primary) }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun TimelineHeader(logs: List<LogEntry>, appFilter: AppFilter, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerHighest) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Change timeline", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Showing", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceContainerHighest) {
                            Text(
                                text = appFilter.displayName(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    Color.Transparent,
                                ),
                            ),
                        ),
            )
            ProgressSection(logs = logs)
        }
    }
}

@Composable
private fun ProgressSection(logs: List<LogEntry>) {
    val now = Clock.System.now().toEpochMilliseconds()
    val weekMillis = 7 * 24 * 60 * 60 * 1000L
    val monthMillis = 30 * 24 * 60 * 60 * 1000L
    val sixMonthsMillis = 6 * monthMillis
    val total = logs.size

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Activity overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ActivityProgress("Past week", logs.count { now - it.timestamp < weekMillis }, total)
        ActivityProgress("Past month", logs.count { now - it.timestamp < monthMillis }, total)
        ActivityProgress("Past 6 months", logs.count { now - it.timestamp < sixMonthsMillis }, total)
    }
}

@Composable
private fun ActivityProgress(label: String, count: Int, total: Int) {
    val progress = if (total == 0) 0f else count.toFloat() / total
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "activity_progress_$label",
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8f)) {
                    Text(count.toString(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(12.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }
}

private fun AppFilter.displayName(): String =
    when (this) {
        AppFilter.ALL_APPS -> "all apps"
        AppFilter.USER_APPS -> "user apps"
        AppFilter.SYSTEM_APPS -> "system apps"
    }
