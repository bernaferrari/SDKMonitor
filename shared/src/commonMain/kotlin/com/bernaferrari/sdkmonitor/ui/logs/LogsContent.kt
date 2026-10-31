package com.bernaferrari.sdkmonitor.ui.logs

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.ui.components.StatusLoading
import com.bernaferrari.sdkmonitor.ui.components.StatusMessage
import com.bernaferrari.sdkmonitor.ui.components.StatusTone
import com.bernaferrari.sdkmonitor.ui.components.expressiveListItemPosition
import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols
import com.bernaferrari.sdkmonitor.ui.logs.components.LogsCard
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings
import com.bernaferrari.sdkmonitor.ui.settings.components.AnalyticsSection
import com.bernaferrari.sdkmonitor.ui.state.LogsUiState
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LogsContent(
    uiState: LogsUiState,
    selectedPackageName: String? = null,
    formatTime: (Long) -> String = { "" },
    onLogClick: (LogEntry) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit = {},
    contentModifier: Modifier = Modifier,
    insights: (@Composable () -> Unit)? = null,
) {
    val s = sdkStrings()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = contentModifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(s.changeLogs, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior,
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
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp),
                    ) {
                        if (insights != null) item(key = "insights") { insights() }
                        item(key = "summary") { ChangeSummary(uiState.logs) }
                        uiState.logs.groupBy { formatTime(it.timestamp) }.forEach { (period, logs) ->
                            item(key = "header_$period") {
                                LogPeriodHeader(period, logs.size)
                            }
                            itemsIndexed(logs, key = { _, log -> "${period}_${log.id}" }) { index, log ->
                                LogsCard(
                                    log = log,
                                    isSelected = log.packageName == selectedPackageName,
                                    position = expressiveListItemPosition(index, logs.lastIndex),
                                    formattedTime = period,
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
        Icon(MaterialSymbols.Filled.History, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Text(period, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) {
            Text(count.toString(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier) {
    val s = sdkStrings()
    Box(modifier, contentAlignment = Alignment.Center) {
        StatusLoading(title = s.loadingChangeHistory, body = s.analyzingAppEcosystem)
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier) {
    val s = sdkStrings()
    Box(modifier, contentAlignment = Alignment.Center) {
        StatusMessage(icon = MaterialSymbols.Filled.Warning, title = s.oopsSomethingWentWrong, body = message, tone = StatusTone.Error)
    }
}

@Composable
private fun EmptyLogsState(title: String, subtitle: String, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        StatusMessage(icon = MaterialSymbols.Filled.History, title = title, body = subtitle)
    }
}

/**
 * How many changes landed in the past week, month and six months. They nest (the week is part of the month,
 * the month part of the six months), so one bar shows them: the six months fill it, the month and the week
 * grow from the left in deeper shades, and a legend gives each count.
 */
@Composable
private fun ChangeSummary(logs: List<LogEntry>) {
    val s = sdkStrings()
    val now = Clock.System.now().toEpochMilliseconds()
    val day = 24 * 60 * 60 * 1000L
    val halfYear = logs.count { now - it.timestamp < 182 * day }
    val month = logs.count { now - it.timestamp < 30 * day }
    val week = logs.count { now - it.timestamp < 7 * day }
    val primary = MaterialTheme.colorScheme.primary
    val shades = listOf(primary, primary.copy(alpha = 0.55f), primary.copy(alpha = 0.22f))
    val windows = listOf(s.pastWeek to week, s.pastMonth to month, s.pastSixMonths to halfYear)

    AnalyticsSection(title = s.changeTimeline) {
        if (halfYear > 0) {
            NestedBar(
                fractions = listOf(week, month).map { it.toFloat() / halfYear },
                colors = shades,
                gapColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().height(20.dp),
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            windows.forEachIndexed { index, (label, count) ->
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.padding(top = 6.dp).size(10.dp).background(shades[index], CircleShape))
                    Column {
                        Text(
                            count.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    }
                }
            }
        }
    }
}

/** A rounded bar filled by [colors] last to first: the last spans it all, each earlier one the left [fractions] of it. */
@Composable
private fun NestedBar(
    fractions: List<Float>,
    colors: List<Color>,
    gapColor: Color,
    modifier: Modifier = Modifier,
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val grown =
        fractions.mapIndexed { index, fraction ->
            animateFloatAsState(
                targetValue = if (started) fraction else 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessVeryLow),
                label = "nested_bar_$index",
            ).value
        }
    Canvas(modifier.clip(RoundedCornerShape(50))) {
        val gap = 3.dp.toPx()
        val inner = CornerRadius(4.dp.toPx())
        drawRect(colors.last())
        // Widest first, so each narrower layer sits on top, set off by a sliver of the surface.
        for (index in grown.indices.reversed()) {
            val width = size.width * grown[index]
            if (width <= 0f) continue
            drawRect(gapColor, size = Size((width + gap).coerceAtMost(size.width), size.height))
            drawRoundRect(colors[index], size = Size(width, size.height), cornerRadius = inner)
        }
    }
}
