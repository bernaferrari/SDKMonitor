package com.bernaferrari.sdkmonitor.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.logic.LogEntryLogic
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import com.bernaferrari.sdkmonitor.ui.state.LogsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@org.koin.core.annotation.KoinViewModel
class LogsViewModel(
    private val appsRepository: AppsRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LogsUiState>(LogsUiState.Loading)
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    private val _appFilter = MutableStateFlow(AppFilter.ALL_APPS)
    val appFilter: StateFlow<AppFilter> = _appFilter.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.getUserPreferences().collect { preferences ->
                _appFilter.value = preferences.appFilter
                loadLogsWithFilter(preferences.appFilter)
            }
        }
    }

    private fun loadLogsWithFilter(appFilter: AppFilter) {
        viewModelScope.launch {
            try {
                _uiState.value = LogsUiState.Loading
                val allVersions = appsRepository.getAllVersions()
                val allApps = appsRepository.getAllApps()
                val logEntries = LogEntryLogic.buildChangeLogs(allVersions, allApps, appFilter)
                _uiState.value =
                    LogsUiState.Success(
                        logs = logEntries,
                        totalCount = logEntries.size,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    LogsUiState.Error(
                        e.message ?: "Failed to load logs",
                    )
            }
        }
    }

    fun updateAppFilter(filter: AppFilter) {
        viewModelScope.launch {
            preferencesRepository.updateAppFilter(filter)
        }
    }

    fun loadLogs() {
        viewModelScope.launch {
            val preferences = preferencesRepository.getUserPreferences().first()
            loadLogsWithFilter(preferences.appFilter)
        }
    }

    fun refreshLogs() {
        loadLogs()
    }
}
