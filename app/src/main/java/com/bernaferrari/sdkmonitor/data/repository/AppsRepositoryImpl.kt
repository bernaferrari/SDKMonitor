package com.bernaferrari.sdkmonitor.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppsRepositoryImpl
    @Inject
    constructor(
        private val appsDao: AppsDao,
        private val versionsDao: VersionsDao,
        @param:ApplicationContext private val context: Context,
    ) : AppsRepository {
        override fun getAppsFlow(): Flow<List<App>> = appsDao.getAppsListFlow()

        override fun getAppsWithVersions(): Flow<List<AppVersion>> =
            getAppsFlow().map { apps ->
                apps.map { app ->
                    val version = versionsDao.getLastValue(app.packageName)
                    val sdkVersion = version?.targetSdk ?: 0
                    val lastUpdate = version?.lastUpdateTime ?: 0

                    AppVersion(
                        packageName = app.packageName,
                        title = app.title,
                        sdkVersion = sdkVersion,
                        lastUpdateTime = lastUpdate.convertTimestampToDate(context),
                        versionName = version?.versionName ?: "",
                        versionCode = version?.version ?: 0L,
                        backgroundColor = app.backgroundColor,
                        isFromPlayStore = app.isFromPlayStore,
                    )
                }
            }

        override fun getAppFromDatabase(packageName: String): Flow<App?> = appsDao.getAppFlow(packageName)

        override fun getAppVersionHistory(packageName: String): Flow<List<AppVersion>> =
            versionsDao.getAllValuesFlow(packageName).map { versions ->
                versions.map { version ->
                    val app = appsDao.getApp(version.packageName)
                    AppVersion(
                        packageName = version.packageName,
                        title = app?.title ?: version.packageName,
                        sdkVersion = version.targetSdk,
                        lastUpdateTime = version.lastUpdateTime.convertTimestampToDate(context),
                        versionName = version.versionName,
                        versionCode = version.version,
                        backgroundColor = app?.backgroundColor ?: 0,
                        isFromPlayStore = app?.isFromPlayStore ?: false,
                    )
                }
            }

        override fun getAppChangeLogs(): Flow<List<LogEntry>> {
            return versionsDao.getAllChangesFlow().map { versions ->
                versions.mapNotNull { version ->
                    val app = appsDao.getApp(version.packageName) ?: return@mapNotNull null
                    LogEntry(
                        id = version.versionId.toLong(),
                        packageName = version.packageName,
                        appName = app.title,
                        oldSdk = null, // This would need to be tracked separately
                        newSdk = version.targetSdk,
                        oldVersion = null, // This would need to be tracked separately
                        newVersion = version.versionName,
                        timestamp = version.lastUpdateTime,
                    )
                }
            }
        }

        override suspend fun clearAllLogs() {
            versionsDao.deleteAllVersions()
        }

        override fun getVersionsForPackage(packageName: String): Flow<List<Version>> = versionsDao.getAllValuesFlow(packageName)

        override fun getVersionsPaged(): Flow<PagingData<Version>> =
            Pager(
                config =
                    PagingConfig(
                        pageSize = 20,
                        prefetchDistance = 60,
                        enablePlaceholders = true,
                    ),
                pagingSourceFactory = { versionsDao.getVersionsPaged() },
            ).flow

        override suspend fun insertApp(app: App) {
            appsDao.insertApp(app)
        }

        override suspend fun insertVersion(version: Version) {
            versionsDao.insertVersion(version)
        }

        override suspend fun deleteApp(packageName: String) {
            appsDao.deleteApp(packageName)
        }

        override suspend fun getLastVersion(packageName: String): Version? = versionsDao.getLastValue(packageName)

        override suspend fun getVersionChangesCount(): Int = versionsDao.countNumberOfChanges()

        override suspend fun getAppsMap(): Map<String, App> = appsDao.getAppsList().associateBy { it.packageName }

        override suspend fun getAllVersions(): List<Version> = versionsDao.getAllVersionsSync()

        override suspend fun deleteAllVersionsForApp(packageName: String): Unit = versionsDao.deleteAllVersionsForApp(packageName)

        override suspend fun getAllApps(): List<App> = appsDao.getAppsList()

        override suspend fun getAllAppsAsAppVersions(): List<AppVersion> =
            withContext(Dispatchers.IO) {
                val apps = appsDao.getAppsList()
                apps.map { app ->
                    val version = versionsDao.getLastValue(app.packageName)
                    val sdkVersion = version?.targetSdk ?: 0
                    val lastUpdate = version?.lastUpdateTime ?: 0

                    AppVersion(
                        packageName = app.packageName,
                        title = app.title,
                        sdkVersion = sdkVersion,
                        lastUpdateTime = lastUpdate.convertTimestampToDate(context),
                        versionName = version?.versionName ?: "",
                        versionCode = version?.version ?: 0L,
                        backgroundColor = app.backgroundColor,
                        isFromPlayStore = app.isFromPlayStore,
                    )
                }
            }

        override suspend fun getApp(packageName: String): App? = appsDao.getApp(packageName)

        override suspend fun getAppVersions(packageName: String): List<Version> = versionsDao.getAllValues(packageName)
    }
