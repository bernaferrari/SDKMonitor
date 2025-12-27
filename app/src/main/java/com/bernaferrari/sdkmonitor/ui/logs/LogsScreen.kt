package com.bernaferrari.sdkmonitor.ui.logs

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.ui.logs.components.LogsCard

/**
 * Logs Screen - Change Log UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    onNavigateToAppDetails: (String) -> Unit = {},
    selectedPackageName: String? = null, // Add selected package parameter
    viewModel: LogsViewModel = hiltViewModel(),
) {
    val uiState: LogsUiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.change_logs),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            when (val state = uiState) {
                is LogsUiState.Loading -> {
                    LoadingState(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                    )
                }

                is LogsUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                    )
                }

                is LogsUiState.Success -> {
                    if (state.logs.isEmpty()) {
                        EmptyLogsContent(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                        )
                    } else {
                        LogsContent(
                            logs = state.logs,
                            onNavigateToAppDetails = onNavigateToAppDetails,
                            selectedPackageName = selectedPackageName, // Pass selection state
                            modifier = Modifier.padding(paddingValues),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                // Hypnotic pulsing animation
                val pulseScale by animateFloatAsState(
                    targetValue = 1.3f,
                    animationSpec =
                        infiniteRepeatable(
                            animation = tween(1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    label = "loading_pulse",
                )

                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .size(64.dp)
                            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp,
                )

                Text(
                    text = stringResource(R.string.loading_change_history),
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = stringResource(R.string.analyzing_app_ecosystem),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
    message: String,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(32.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
            elevation = CardDefaults.cardElevation(20.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            brush =
                                Brush.radialGradient(
                                    colors =
                                        listOf(
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                                            MaterialTheme.colorScheme.errorContainer,
                                        ),
                                ),
                        ),
            ) {
                Column(
                    modifier = Modifier.padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.oops_something_went_wrong),
                        style =
                            MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyLogsContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(48.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Large animated icon with gradient background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                Color.Transparent,
                            ),
                        ),
                        shape = RoundedCornerShape(32.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            // Text content - centered
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.no_changes_yet),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = stringResource(R.string.when_apps_update_description),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun LogsContent(
    modifier: Modifier = Modifier,
    logs: List<LogEntry>,
    onNavigateToAppDetails: (String) -> Unit,
    selectedPackageName: String? = null,
) {
    val context = LocalContext.current

    // Group logs by time periods with localized strings
    val groupedLogs =
        remember(logs, context) {
            groupLogsByTimePeriod(logs, context)
        }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        item {
            TimelineHeader(
                logs = logs,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        // Show grouped logs with time period headers
        groupedLogs.forEach { (timePeriod, logsInPeriod) ->
            // Time period header
            item(key = "header_$timePeriod") {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = timePeriod,
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        ) {
                            Text(
                                text = logsInPeriod.size.toString(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style =
                                    MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }

            // Logs in this time period with proper isLast calculation
            logsInPeriod.forEachIndexed { index, log ->
                item(key = "${timePeriod}_${log.id}") {
                    LogsCard(
                        log = log,
                        onClick = { onNavigateToAppDetails(log.packageName) },
                        isSelected = selectedPackageName == log.packageName,
                        isLast = index == logsInPeriod.lastIndex,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Helper function to group logs by time periods with localization
private fun groupLogsByTimePeriod(
    logs: List<LogEntry>,
    context: Context,
): LinkedHashMap<String, List<LogEntry>> {
    val now = System.currentTimeMillis()
    val calendar = java.util.Calendar.getInstance()

    // Get current year
    calendar.timeInMillis = now
    val currentYear = calendar.get(java.util.Calendar.YEAR)

    // Get start of today, this week, this month for proper grouping
    val startOfToday =
        calendar
            .apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis

    calendar.timeInMillis = now
    val startOfThisWeek =
        calendar
            .apply {
                set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis

    calendar.timeInMillis = now
    val startOfThisMonth =
        calendar
            .apply {
                set(java.util.Calendar.DAY_OF_MONTH, 1)
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis

    val grouped = LinkedHashMap<String, MutableList<LogEntry>>()

    logs.forEach { log ->
        calendar.timeInMillis = log.timestamp
        val logYear = calendar.get(java.util.Calendar.YEAR)

        val period =
            when {
                log.timestamp >= startOfToday -> {
                    // Use Android's localized "Today"
                    DateUtils
                        .formatDateTime(
                            context,
                            log.timestamp,
                            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_YEAR or DateUtils.FORMAT_ABBREV_RELATIVE,
                        ).takeIf { it.contains("today", ignoreCase = true) }
                        ?: context.getString(R.string.today)
                }

                log.timestamp >= startOfThisWeek -> {
                    context.getString(R.string.this_week)
                }

                log.timestamp >= startOfThisMonth -> {
                    context.getString(R.string.this_month)
                }

                logYear == currentYear -> {
                    context.getString(R.string.this_year)
                }

                else -> {
                    // For years, just use the year number (universal)
                    logYear.toString()
                }
            }

        grouped.getOrPut(period) { mutableListOf() }.add(log)
    }

    // Convert to LinkedHashMap with List values and sort by recency
    return grouped
        .mapValues { (_, logs) ->
            logs.sortedByDescending { it.timestamp }
        }.toMap(LinkedHashMap())
}

@Composable
private fun TimelineHeader(
    logs: List<LogEntry>,
    modifier: Modifier = Modifier,
) {
    val viewModel: LogsViewModel = hiltViewModel()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Header section with filter status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = RoundedCornerShape(16.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = "Timeline",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.change_timeline),
                        style =
                            MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    // Show actual filter status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.showing),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        ) {
                            Text(
                                text = getFilterDisplayName(viewModel.getCurrentFilter()),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style =
                                    MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                    ),
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
                            brush =
                                Brush.horizontalGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            Color.Transparent,
                                        ),
                                ),
                        ),
            )

            // Calculate actual time-based stats from logs
            ProgressSection(
                logs = logs,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ProgressSection(
    logs: List<LogEntry>,
    modifier: Modifier = Modifier,
) {
    val now = System.currentTimeMillis()
    val weekMs = 7 * 24 * 60 * 60 * 1000L
    val monthMs = 30 * 24 * 60 * 60 * 1000L
    val sixMonthsMs = 6 * monthMs

    // Calculate actual counts from logs
    val weeklyLogs = logs.count { now - it.timestamp < weekMs }
    val monthlyLogs = logs.count { now - it.timestamp < monthMs }
    val sixMonthLogs = logs.count { now - it.timestamp < sixMonthsMs }
    val totalLogs = logs.size

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.activity_overview),
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        // Weekly Progress
        EnhancedProgressIndicator(
            label = stringResource(R.string.past_week),
            count = weeklyLogs,
            total = totalLogs,
            color = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier.fillMaxWidth(),
        )

        // Monthly Progress
        EnhancedProgressIndicator(
            label = stringResource(R.string.past_month),
            count = monthlyLogs,
            total = totalLogs,
            color = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier.fillMaxWidth(),
        )

        // 6 Month Progress
        EnhancedProgressIndicator(
            label = stringResource(R.string.past_6_months),
            count = sixMonthLogs,
            total = totalLogs,
            color = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EnhancedProgressIndicator(
    label: String,
    count: Int,
    total: Int,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    val percentage = (progress * 100).toInt()

    // Animated progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec =
            tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing,
            ),
        label = "progress_animation",
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Label and count row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = label,
                    style =
                        MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = backgroundColor.copy(alpha = 0.8f),
                ) {
                    Text(
                        text = count.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        color = color,
                    )
                }
            }

            Text(
                text = "$percentage%",
                style =
                    MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                color = color,
            )
        }

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(12.dp),
            color = color,
            trackColor = backgroundColor,
        )
    }
}

@Composable
private fun getFilterDisplayName(filter: AppFilter): String =
    when (filter) {
        AppFilter.ALL_APPS -> stringResource(R.string.all_apps)
        AppFilter.USER_APPS -> stringResource(R.string.user_apps)
        AppFilter.SYSTEM_APPS -> stringResource(R.string.system_apps)
    }.lowercase()

fun formatLogTime(
    timestamp: Long,
    context: Context,
): String = timestamp.convertTimestampToDate(context)
