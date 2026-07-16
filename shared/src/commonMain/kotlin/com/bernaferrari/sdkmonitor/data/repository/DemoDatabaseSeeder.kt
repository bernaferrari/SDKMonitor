package com.bernaferrari.sdkmonitor.data.repository

import com.bernaferrari.sdkmonitor.data.source.local.AppDatabase
import com.bernaferrari.sdkmonitor.domain.TrackedApp
import com.bernaferrari.sdkmonitor.domain.TrackedVersion
import com.bernaferrari.sdkmonitor.shared.mock.MockDemoData
import kotlin.time.Clock

/**
 * Replaces disposable web/desktop demo data with the current [MockDemoData] set.
 *
 * The demo database has no user-owned data, so refreshing it on startup keeps screenshots and
 * evaluations deterministic after mock-data improvements.
 */
@Suppress("UnusedReceiverParameter")
suspend fun AppDatabase.resetDemoData() {
    val appsDao = snapsDao()
    val versionsDao = versionsDao()
    val now = Clock.System.now().toEpochMilliseconds()
    versionsDao.deleteAllVersions()
    appsDao.deleteAllApps()

    val repo = RoomAppsRepository(appsDao, versionsDao)
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
                versionName = MockDemoData.previousVersionName(app.packageName),
                lastUpdateTime = demoTimestampFor(app.packageName, now) - 86_400_000L,
                targetSdk = olderSdk,
            ),
        )
        repo.insertVersion(
            TrackedVersion(
                versionId = "${app.packageName}-cur".hashCode(),
                versionCode = app.versionCode,
                packageName = app.packageName,
                versionName = app.versionName,
                lastUpdateTime = demoTimestampFor(app.packageName, now),
                targetSdk = app.sdkVersion,
            ),
        )
    }
}

private fun demoTimestampFor(packageName: String, now: Long): Long =
    when (packageName) {
        "com.bernaferrari.sdkmonitor" -> now - 2 * DemoDayMillis
        "com.google.android.gms" -> now - 1 * DemoDayMillis
        "com.android.vending" -> now - 3 * DemoDayMillis
        "com.whatsapp" -> now - 4 * DemoDayMillis
        "com.android.chrome" -> now - 6 * DemoDayMillis
        "com.bank.secure" -> now - 8 * DemoDayMillis
        "com.spotify.music" -> now - 11 * DemoDayMillis
        "com.discord" -> now - 14 * DemoDayMillis
        "org.mozilla.firefox" -> now - 18 * DemoDayMillis
        "com.instagram.android" -> now - 25 * DemoDayMillis
        "com.twitter.android" -> now - 42 * DemoDayMillis
        else -> now - 180 * DemoDayMillis
    }

private const val DemoDayMillis = 86_400_000L

fun AppDatabase.asRoomAppsRepository(
    formatTimestamp: (Long) -> String = { it.toString() },
): RoomAppsRepository = RoomAppsRepository(snapsDao(), versionsDao(), formatTimestamp)
