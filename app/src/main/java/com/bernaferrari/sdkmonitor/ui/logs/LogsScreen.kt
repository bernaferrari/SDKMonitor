package com.bernaferrari.sdkmonitor.ui.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.ui.state.LogsUiState
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
    val appFilter by viewModel.appFilter.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LogsContent(
        uiState = uiState,
        appFilter = appFilter,
        selectedPackageName = selectedPackageName,
        formatTime = { ts -> ts.convertTimestampToDate(context) },
        onLogClick = { onNavigateToAppDetails(it.packageName) },
        onRetry = viewModel::refreshLogs,
        onRefresh = viewModel::refreshLogs,
        contentModifier = modifier,
    )
}
