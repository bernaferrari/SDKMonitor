package com.bernaferrari.sdkmonitor.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernaferrari.sdkmonitor.ui.state.MainUiState
import org.koin.compose.viewmodel.koinViewModel

/**
 * Android shell: Koin ViewModel + lifecycle collection.
 * UI lives in [MainContent] (shared/commonMain).
 */
@Composable
fun MainScreen(
    onNavigateToAppDetails: (String) -> Unit,
    selectedPackageName: String? = null,
    viewModel: MainViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val appFilter by viewModel.appFilter.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val isFirstSync by viewModel.isFirstSync.collectAsStateWithLifecycle()
    val syncProgressState by viewModel.syncProgress.collectAsStateWithLifecycle()
    val syncProgress =
        if (syncProgressState.isActive && syncProgressState.total > 0) {
            syncProgressState.current.toFloat() / syncProgressState.total.toFloat()
        } else {
            0f
        }

    MainContent(
        uiState = uiState,
        searchQuery = searchQuery,
        appFilter = appFilter,
        sortOption = sortOption,
        isFirstSync = isFirstSync,
        syncProgress = syncProgress,
        selectedPackageName = selectedPackageName,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onAppFilterChange = viewModel::updateAppFilter,
        onSortOptionChange = viewModel::updateSortOption,
        onAppClick = onNavigateToAppDetails,
        onRetry = viewModel::retryLoadApps,
        onRefresh = viewModel::loadApps,
        contentModifier = modifier,
    )
}
