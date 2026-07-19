package com.bernaferrari.sdkmonitor.data.repository

import com.bernaferrari.sdkmonitor.data.source.local.AppDatabase
import com.bernaferrari.sdkmonitor.domain.TrackedApp
import com.bernaferrari.sdkmonitor.domain.TrackedVersion
import com.bernaferrari.sdkmonitor.domain.logic.ApiLevel
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
        historicalVersionFor(app.packageName)?.let { (versionName, targetSdk, ageInDays) ->
            repo.insertVersion(
                TrackedVersion(
                    versionId = "${app.packageName}-history".hashCode(),
                    versionCode = (app.versionCode - 2).coerceAtLeast(1),
                    packageName = app.packageName,
                    versionName = versionName,
                    lastUpdateTime = now - ageInDays * DemoDayMillis,
                    targetSdk = targetSdk,
                ),
            )
        }
        // Prior version (for change-log / analytics story)
        val olderSdk = (app.sdkVersion - 1).coerceAtLeast(21)
        repo.insertVersion(
            TrackedVersion(
                versionId = "${app.packageName}-old".hashCode(),
                versionCode = (app.versionCode - 1).coerceAtLeast(1),
                packageName = app.packageName,
                versionName = MockDemoData.previousVersionName(app.packageName),
                // A release history should never show an SDK change on the same day.
                lastUpdateTime = demoTimestampFor(app.packageName, now) - DemoDayMillis,
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

private fun historicalVersionFor(packageName: String): Triple<String, Int, Long>? =
    when (packageName) {
        // Every historical transition must also change target SDK, not just version name.
        "com.android.chrome" -> Triple("1.35.0", ApiLevel.latestMinus(3), 30L)
        "com.google.android.youtube" -> Triple("20.25.30", ApiLevel.latestMinus(3), 28L)
        "com.google.android.gm" -> Triple("2025.05.01", ApiLevel.latestMinus(2), 35L)
        "com.google.android.apps.maps" -> Triple("11.134.0", ApiLevel.latestMinus(3), 24L)
        "com.spotify.music" -> Triple("8.8.0", ApiLevel.latestMinus(4), 32L)
        "com.whatsapp" -> Triple("2.1.0", ApiLevel.latestMinus(3), 26L)
        "com.bernaferrari.sdkmonitor" -> Triple("2.0.0", ApiLevel.latestMinus(3), 20L)
        else -> null
    }

private fun demoTimestampFor(packageName: String, now: Long): Long =
    when (packageName) {
        "com.bernaferrari.sdkmonitor" -> now - 2 * DemoDayMillis
        "com.google.android.gms" -> now - 1 * DemoDayMillis
        "com.android.vending" -> now - 1 * DemoDayMillis
        "com.whatsapp" -> now - 4 * DemoDayMillis
        "com.android.chrome" -> now - 5 * DemoDayMillis
        "com.google.android.youtube" -> now - 7 * DemoDayMillis
        "com.google.android.gm" -> now - 9 * DemoDayMillis
        "com.google.android.apps.maps" -> now - 10 * DemoDayMillis
        "com.spotify.music" -> now - 11 * DemoDayMillis
        "com.discord" -> now - 14 * DemoDayMillis
        "org.thoughtcrime.securesms" -> now - 16 * DemoDayMillis
        "org.mozilla.firefox" -> now - 18 * DemoDayMillis
        "com.instagram.android" -> now - 25 * DemoDayMillis
        "com.twitter.android" -> now - 42 * DemoDayMillis
        "com.nu.production" -> now - 48 * DemoDayMillis
        "com.google.android.apps.photos" -> now - 3 * DemoDayMillis
        "com.reddit.frontpage" -> now - 8 * DemoDayMillis
        "com.linkedin.android" -> now - 12 * DemoDayMillis
        else -> {
            val staggeredDays = 5L + packageName.sumOf(Char::code).toLong() % 80L
            now - staggeredDays * DemoDayMillis
        }
    }

private const val DemoDayMillis = 86_400_000L

fun AppDatabase.asRoomAppsRepository(
    formatTimestamp: (Long) -> String = { it.toString() },
): RoomAppsRepository = RoomAppsRepository(snapsDao(), versionsDao(), formatTimestamp)
