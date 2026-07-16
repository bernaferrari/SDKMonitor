package com.bernaferrari.sdkmonitor.data.repository

import com.bernaferrari.sdkmonitor.data.source.local.AppDatabase
import com.bernaferrari.sdkmonitor.domain.TrackedApp
import com.bernaferrari.sdkmonitor.domain.TrackedVersion
import com.bernaferrari.sdkmonitor.shared.mock.MockDemoData

/**
 * Seeds [AppDatabase] with [MockDemoData] once (demo / first web or desktop run).
 */
suspend fun AppDatabase.seedDemoDataIfEmpty() {
    val appsDao = snapsDao()
    if (appsDao.getAppsCount() > 0) return

    val repo = RoomAppsRepository(appsDao, versionsDao())
    MockDemoData.apps.forEach { app ->
        repo.insertApp(
            TrackedApp(
                packageName = app.packageName,
                title = app.title,
                backgroundColor = app.backgroundColor.toInt(),
                isFromPlayStore = !app.isSystemApp,
            ),
        )
        // Prior version (for change-log / analytics story)
        val olderSdk = (app.sdkVersion - 1).coerceAtLeast(21)
        repo.insertVersion(
            TrackedVersion(
                versionId = "${app.packageName}-old".hashCode(),
                versionCode = (app.versionCode - 1).coerceAtLeast(1),
                packageName = app.packageName,
                versionName = "prev",
                lastUpdateTime = 1_700_000_000_000L,
                targetSdk = olderSdk,
            ),
        )
        repo.insertVersion(
            TrackedVersion(
                versionId = "${app.packageName}-cur".hashCode(),
                versionCode = app.versionCode,
                packageName = app.packageName,
                versionName = app.versionName,
                lastUpdateTime = 1_748_000_000_000L,
                targetSdk = app.sdkVersion,
            ),
        )
    }
}

fun AppDatabase.asRoomAppsRepository(
    formatTimestamp: (Long) -> String = { it.toString() },
): RoomAppsRepository = RoomAppsRepository(snapsDao(), versionsDao(), formatTimestamp)
