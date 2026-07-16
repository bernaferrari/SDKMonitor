package com.bernaferrari.sdkmonitor.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.AppListLogic
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.SortOption
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import com.bernaferrari.sdkmonitor.domain.logic.StringNormalize
import com.bernaferrari.sdkmonitor.ui.state.MainUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@org.koin.core.annotation.KoinViewModel
class MainViewModel(
    appsRepository: AppsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val appManager: AppManager,
) : ViewModel() {
        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

        private val _appFilter = MutableStateFlow(AppFilter.ALL_APPS)
        val appFilter: StateFlow<AppFilter> = _appFilter.asStateFlow()

        private val _sortOption = MutableStateFlow(SortOption.NAME)
        val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

        private val hasLoaded = MutableStateFlow(false)

        // Expose first sync state
        val isFirstSync = appManager.isFirstSync
        val syncProgress = appManager.syncProgress

        val uiState: StateFlow<MainUiState> =
            combine(
                appsRepository.getAppsWithVersions(),
                preferencesRepository.getUserPreferences(),
                _searchQuery,
                hasLoaded,
                isFirstSync,
            ) { apps, prefs, query, hasLoaded, isFirstSync ->
                try {
                    // Update filter and sort states from preferences
                    _appFilter.value = prefs.appFilter
                    _sortOption.value = if (prefs.orderBySdk) SortOption.SDK else SortOption.NAME

                    val sort = if (prefs.orderBySdk) SortOption.SDK else SortOption.NAME
                    val orderedApps =
                        AppListLogic.applyListPipeline(
                            apps = apps,
                            filter = prefs.appFilter,
                            sortOption = sort,
                            orderBySdk = prefs.orderBySdk,
                            searchQuery = query,
                            normalize = StringNormalize::normalize,
                        )
                    val searchFiltered = orderedApps

                    // Show loading only if first sync or empty and not loaded yet
                    if ((!hasLoaded && orderedApps.isEmpty()) || isFirstSync) {
                        MainUiState.Loading
                    } else {
                        MainUiState.Success(
                            apps = apps,
                            filteredApps = searchFiltered,
                            totalCount = orderedApps.size,
                        )
                    }
                } catch (e: Exception) {
                    MainUiState.Error(
                        message = e.message ?: "Failed to load apps",
                        throwable = e,
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MainUiState.Loading,
            )

        init {
            refreshAppsIfNeeded()
        }

        fun updateSearchQuery(query: String) {
            _searchQuery.value = query
        }

        fun loadApps() {
            viewModelScope.launch {
                try {
                    // Sync all apps (now includes cleanup automatically)
                    appManager.syncAllApps()
                } catch (e: Exception) {
                    // Handle error appropriately based on your UI state management
                    Napier.e("❌ Failed to load apps", e)
                }
            }
        }

        fun updateAppFilter(filter: AppFilter) {
            viewModelScope.launch {
                preferencesRepository.updateAppFilter(filter)
            }
        }

        fun updateSortOption(option: SortOption) {
            viewModelScope.launch {
                preferencesRepository.updateOrderBySdk(option == SortOption.SDK)
            }
        }

        fun retryLoadApps() {
            refreshAppsIfNeeded()
        }

        private fun refreshAppsIfNeeded() {
            viewModelScope.launch {
                if (!hasLoaded.value || uiState.value is MainUiState.Loading) {
                    refreshAllApps()
                }
                hasLoaded.value = true
            }
        }

        private suspend fun refreshAllApps() {
            try {
                val preferences = preferencesRepository.getUserPreferences().first()
                val installedApps =
                    if (preferences.appFilter === AppFilter.ALL_APPS) {
                        appManager.getPackages()
                    } else {
                        appManager.getPackagesWithUserPrefs()
                    }

                // If no apps found (probably emulator), show system apps
                if (installedApps.isEmpty()) {
                    preferencesRepository.updateAppFilter(AppFilter.ALL_APPS)
                }

                // Use the new integrated sync method instead of manual insertion
                appManager.syncAllApps()
            } catch (e: Exception) {
                // Handle error - could emit error state
                e.printStackTrace()
            }
        }
    }
