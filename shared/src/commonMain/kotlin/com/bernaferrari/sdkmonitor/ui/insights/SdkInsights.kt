package com.bernaferrari.sdkmonitor.ui.insights

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.ui.settings.SdkDetailDialog
import com.bernaferrari.sdkmonitor.ui.settings.components.AppFilterSelector
import com.bernaferrari.sdkmonitor.ui.settings.components.SdkAnalyticsCard
import com.bernaferrari.sdkmonitor.ui.settings.components.SdkAnalyticsEmptyState
import com.bernaferrari.sdkmonitor.ui.settings.components.SdkAnalyticsPlaceholder
import com.bernaferrari.sdkmonitor.ui.state.SettingsUiState

/**
 * Where the apps stand today: how many target each SDK, which apps the chart counts (the filter), and the
 * apps behind a bar when it is tapped. It leads the Logs tab, above what changed and when.
 */
@Composable
fun SdkInsights(
    state: SettingsUiState,
    onAppFilterChange: (AppFilter) -> Unit,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var openSdk by remember { mutableStateOf<Int?>(null) }

    val filter: @Composable () -> Unit = {
        AppFilterSelector(currentFilter = state.preferences.appFilter, onFilterChange = onAppFilterChange)
    }
    Column(modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        if (state.isAnalyticsLoading || state.totalApps == 0) {
            // Without the chart there is no header to hold the filter, and an empty filter must stay changeable.
            Box(Modifier.fillMaxWidth().padding(bottom = 12.dp), contentAlignment = Alignment.CenterEnd) { filter() }
        }
        // Loading, empty and the chart share one slot: the chart fades in as the card grows to it, no jump.
        val phase =
            when {
                state.isAnalyticsLoading -> 0
                state.totalApps == 0 -> 1
                else -> 2
            }
        AnimatedContent(
            targetState = phase,
            transitionSpec = {
                (fadeIn(tween(220, delayMillis = 90)) togetherWith fadeOut(tween(90)))
                    .using(SizeTransform(clip = false))
            },
            label = "sdkInsights",
        ) { shown ->
            when (shown) {
                0 -> SdkAnalyticsPlaceholder()
                1 -> SdkAnalyticsEmptyState()
                else ->
                    SdkAnalyticsCard(
                        sdkDistribution = state.sdkDistribution,
                        totalApps = state.totalApps,
                        onSdkClick = { openSdk = it },
                        action = filter,
                    )
            }
        }
    }

    openSdk?.let { sdk ->
        SdkDetailDialog(
            sdkVersion = sdk,
            apps = state.allAppsForSdk.filter { it.sdkVersion == sdk },
            onDismiss = { openSdk = null },
            onAppClick = { packageName ->
                openSdk = null
                onAppClick(packageName)
            },
        )
    }
}
