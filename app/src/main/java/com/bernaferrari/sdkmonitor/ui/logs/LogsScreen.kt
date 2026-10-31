package com.bernaferrari.sdkmonitor.ui.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.ui.insights.SdkInsights
import com.bernaferrari.sdkmonitor.ui.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Android shell for [LogsContent].
 */
@Composable
fun LogsScreen(
    onNavigateToAppDetails: (String) -> Unit,
    selectedPackageName: String? = null,
    isTabletSize: Boolean = false,
    viewModel: LogsViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // The SDK insights that lead this tab come from the same model as Settings.
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LogsContent(
        uiState = uiState,
        selectedPackageName = selectedPackageName,
        formatTime = { ts -> ts.convertTimestampToDate(context) },
        onLogClick = { onNavigateToAppDetails(it.packageName) },
        onRetry = viewModel::refreshLogs,
        onRefresh = viewModel::refreshLogs,
        contentModifier = modifier,
        insights = {
            SdkInsights(
                state = settingsState,
                onAppFilterChange = settingsViewModel::updateAppFilter,
                onAppClick = onNavigateToAppDetails,
            )
        },
    )
}
