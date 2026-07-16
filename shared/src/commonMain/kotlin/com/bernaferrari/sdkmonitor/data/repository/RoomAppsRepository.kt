package com.bernaferrari.sdkmonitor.data.repository

import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.domain.TrackedApp
import com.bernaferrari.sdkmonitor.domain.TrackedVersion
import com.bernaferrari.sdkmonitor.domain.logic.LogEntryLogic
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Multiplatform Room-backed [AppsRepository] for Android, desktop, and web (wasmJs).
 */
class RoomAppsRepository(
    private val appsDao: AppsDao,
    private val versionsDao: VersionsDao,
    private val formatTimestamp: (Long) -> String = { ts -> ts.toString() },
) : AppsRepository {
    private fun App.toTracked() =
        TrackedApp(
            packageName = packageName,
            title = title,
            backgroundColor = backgroundColor,
            isFromPlayStore = isFromPlayStore,
        )

    private fun Version.toTracked() =
        TrackedVersion(
            versionId = versionId,
            versionCode = version,
            packageName = packageName,
            versionName = versionName,
            lastUpdateTime = lastUpdateTime,
            targetSdk = targetSdk,
        )

    private fun TrackedApp.toEntity() =
        App(
            packageName = packageName,
            title = title,
            backgroundColor = backgroundColor,
            isFromPlayStore = isFromPlayStore,
        )

    private fun TrackedVersion.toEntity() =
        Version(
            versionId = versionId,
            version = versionCode,
            packageName = packageName,
            versionName = versionName,
            lastUpdateTime = lastUpdateTime,
            targetSdk = targetSdk,
        )

    private fun toAppVersion(
        app: App?,
        version: Version?,
    ): AppVersion {
        val packageName = app?.packageName ?: version?.packageName ?: ""
        val sdkVersion = version?.targetSdk ?: 0
        val lastUpdate = version?.lastUpdateTime ?: 0L
        return AppVersion(
            packageName = packageName,
            title = app?.title ?: packageName,
            sdkVersion = sdkVersion,
            lastUpdateTime = if (lastUpdate == 0L) "" else formatTimestamp(lastUpdate),
            versionName = version?.versionName ?: "",
            versionCode = version?.version ?: 0L,
            backgroundColor = (app?.backgroundColor ?: 0).toLong() and 0xFFFFFFFFL,
            isFromPlayStore = app?.isFromPlayStore ?: false,
            isSystemApp = app?.let { !it.isFromPlayStore } ?: false,
        )
    }

    override fun getAppsFlow(): Flow<List<TrackedApp>> =
        appsDao.getAppsListFlow().map { list -> list.map { it.toTracked() } }

    override fun getAppsWithVersions(): Flow<List<AppVersion>> =
        appsDao.getAppsListFlow().map { apps ->
            apps.map { app ->
                toAppVersion(app, versionsDao.getLastValue(app.packageName))
            }
        }

    override fun getAppFromDatabase(packageName: String): Flow<TrackedApp?> =
        appsDao.getAppFlow(packageName).map { it?.toTracked() }

    override fun getAppVersionHistory(packageName: String): Flow<List<AppVersion>> =
        versionsDao.getAllValuesFlow(packageName).map { versions ->
            versions.map { version ->
                toAppVersion(appsDao.getApp(version.packageName), version)
            }
        }

    override fun getAppChangeLogs(): Flow<List<LogEntry>> =
        versionsDao.getAllChangesFlow().map { versions ->
            val apps = appsDao.getAppsList().associateBy { it.packageName }
            versions.mapNotNull { version ->
                val app = apps[version.packageName] ?: return@mapNotNull null
                LogEntry(
                    id = version.versionId.toLong(),
                    packageName = version.packageName,
                    appName = app.title,
                    oldSdk = null,
                    newSdk = version.targetSdk,
                    oldVersion = null,
                    newVersion = version.versionName,
                    timestamp = version.lastUpdateTime,
                )
            }
        }

    override suspend fun clearAllLogs() {
        versionsDao.deleteAllVersions()
    }

    override fun getVersionsForPackage(packageName: String): Flow<List<TrackedVersion>> =
        versionsDao.getAllValuesFlow(packageName).map { list -> list.map { it.toTracked() } }

    override suspend fun insertApp(app: TrackedApp) {
        appsDao.insertApp(app.toEntity())
    }

    override suspend fun insertVersion(version: TrackedVersion) {
        versionsDao.insertVersion(version.toEntity())
    }

    override suspend fun deleteApp(packageName: String) {
        appsDao.deleteApp(packageName)
    }

    override suspend fun getLastVersion(packageName: String): TrackedVersion? =
        versionsDao.getLastValue(packageName)?.toTracked()

    override suspend fun getVersionChangesCount(): Int = versionsDao.countNumberOfChanges()

    override suspend fun getAppsMap(): Map<String, TrackedApp> =
        appsDao.getAppsList().map { it.toTracked() }.associateBy { it.packageName }

    override suspend fun getAllVersions(): List<TrackedVersion> =
        versionsDao.getAllVersionsSync().map { it.toTracked() }

    override suspend fun deleteAllVersionsForApp(packageName: String) {
        versionsDao.deleteAllVersionsForApp(packageName)
    }

    override suspend fun getAllApps(): List<TrackedApp> = appsDao.getAppsList().map { it.toTracked() }

    override suspend fun getAllAppsAsAppVersions(): List<AppVersion> =
        appsDao.getAppsList().map { app ->
            toAppVersion(app, versionsDao.getLastValue(app.packageName))
        }

    override suspend fun getApp(packageName: String): TrackedApp? = appsDao.getApp(packageName)?.toTracked()

    override suspend fun getAppVersions(packageName: String): List<TrackedVersion> =
        versionsDao.getAllValues(packageName).map { it.toTracked() }

    suspend fun buildChangeLogs(filter: AppFilter = AppFilter.ALL_APPS): List<LogEntry> =
        LogEntryLogic.buildChangeLogs(
            versions = getAllVersions(),
            apps = getAllApps(),
            filter = filter,
        )
}
