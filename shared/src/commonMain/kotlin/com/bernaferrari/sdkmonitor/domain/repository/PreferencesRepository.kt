package com.bernaferrari.sdkmonitor.domain.repository

import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.domain.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferences>

    suspend fun updateBackgroundSync(enabled: Boolean)

    suspend fun updateOrderBySdk(enabled: Boolean)

    suspend fun updateSyncInterval(interval: String)

    suspend fun updateAppFilter(filter: AppFilter)

    suspend fun updateThemeMode(themeMode: ThemeMode)
}
