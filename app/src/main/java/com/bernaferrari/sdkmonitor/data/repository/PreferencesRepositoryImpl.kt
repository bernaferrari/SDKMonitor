package com.bernaferrari.sdkmonitor.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.domain.ThemePalette
import com.bernaferrari.sdkmonitor.domain.UserPreferences
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single(binds = [PreferencesRepository::class])
class PreferencesRepositoryImpl(
        private val dataStore: DataStore<Preferences>,
    ) : PreferencesRepository {
        companion object {
            private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
            private val THEME_PALETTE_KEY = stringPreferencesKey("theme_palette")
            private val APP_FILTER_KEY =
                stringPreferencesKey("app_filter")
            private val BACKGROUND_SYNC_KEY = booleanPreferencesKey("background_sync")
            private val ORDER_BY_SDK_KEY = booleanPreferencesKey("order_by_sdk")
            private val SYNC_INTERVAL_KEY = stringPreferencesKey("sync_interval")
        }

        override fun getUserPreferences(): Flow<UserPreferences> =
            dataStore.data.map { preferences ->
                UserPreferences(
                    themeMode =
                        preferences[THEME_MODE_KEY]
                            ?.let { stored -> ThemeMode.entries.firstOrNull { it.name == stored } }
                            ?: ThemeMode.SYSTEM,
                    themePalette =
                        ThemePalette.fromToken(preferences[THEME_PALETTE_KEY])
                            ?: ThemePalette.DYNAMIC,
                    appFilter =
                        AppFilter.valueOf(
                            preferences[APP_FILTER_KEY] ?: AppFilter.USER_APPS.name,
                        ),
                    backgroundSync = preferences[BACKGROUND_SYNC_KEY] ?: false,
                    orderBySdk = preferences[ORDER_BY_SDK_KEY] ?: false,
                    syncInterval = preferences[SYNC_INTERVAL_KEY] ?: "301",
                )
            }

        override suspend fun updateAppFilter(filter: AppFilter) {
            dataStore.edit { preferences ->
                preferences[APP_FILTER_KEY] = filter.name
            }
        }

        override suspend fun updateBackgroundSync(enabled: Boolean) {
            dataStore.edit { preferences ->
                preferences[BACKGROUND_SYNC_KEY] = enabled
            }
        }

        override suspend fun updateOrderBySdk(enabled: Boolean) {
            dataStore.edit { preferences ->
                preferences[ORDER_BY_SDK_KEY] = enabled
            }
        }

        override suspend fun updateSyncInterval(interval: String) {
            dataStore.edit { preferences ->
                preferences[SYNC_INTERVAL_KEY] = interval
            }
        }

        override suspend fun updateThemeMode(themeMode: ThemeMode) {
            dataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = themeMode.name
            }
        }

        override suspend fun updateThemePalette(themePalette: ThemePalette) {
            dataStore.edit { preferences ->
                preferences[THEME_PALETTE_KEY] = themePalette.token
            }
        }
    }
