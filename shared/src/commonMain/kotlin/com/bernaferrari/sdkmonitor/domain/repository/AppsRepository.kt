package com.bernaferrari.sdkmonitor.domain.repository

import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.domain.TrackedApp
import com.bernaferrari.sdkmonitor.domain.TrackedVersion
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract shared across platforms.
 * Android implements via Room; other targets may use platform stores.
 */
interface AppsRepository {
    fun getAppsFlow(): Flow<List<TrackedApp>>

    fun getAppsWithVersions(): Flow<List<AppVersion>>

    fun getAppFromDatabase(packageName: String): Flow<TrackedApp?>

    fun getAppVersionHistory(packageName: String): Flow<List<AppVersion>>

    fun getAppChangeLogs(): Flow<List<LogEntry>>

    suspend fun clearAllLogs()

    fun getVersionsForPackage(packageName: String): Flow<List<TrackedVersion>>

    suspend fun insertApp(app: TrackedApp)

    suspend fun insertVersion(version: TrackedVersion)

    suspend fun deleteApp(packageName: String)

    suspend fun getLastVersion(packageName: String): TrackedVersion?

    suspend fun getVersionChangesCount(): Int

    suspend fun getAppsMap(): Map<String, TrackedApp>

    suspend fun getAllVersions(): List<TrackedVersion>

    suspend fun deleteAllVersionsForApp(packageName: String)

    suspend fun getAllApps(): List<TrackedApp>

    suspend fun getAllAppsAsAppVersions(): List<AppVersion>

    suspend fun getApp(packageName: String): TrackedApp?

    suspend fun getAppVersions(packageName: String): List<TrackedVersion>
}
