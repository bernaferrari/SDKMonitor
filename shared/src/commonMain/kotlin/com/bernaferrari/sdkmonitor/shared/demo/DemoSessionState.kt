package com.bernaferrari.sdkmonitor.shared.demo

import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.SortOption
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.domain.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Ephemeral demo UI state (filter/theme/search). App/version data comes from Room only.
 */
class DemoSessionState {
    // The web demo has no Android dynamic-color source; follow the browser/system appearance by default.
    private val _preferences = MutableStateFlow(UserPreferences(themeMode = ThemeMode.SYSTEM))
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.SDK)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun setAppFilter(filter: AppFilter) {
        _preferences.update { it.copy(appFilter = filter) }
    }

    fun setThemeMode(mode: ThemeMode) {
        _preferences.update { it.copy(themeMode = mode) }
    }
}
