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
        val timeline = demoTimelineFor(app.packageName)
        repo.insertApp(
            TrackedApp(
                packageName = app.packageName,
                title = app.title,
                backgroundColor = app.backgroundColor.toInt(),
                isFromPlayStore = !app.isSystemApp,
            ),
        )
        historicalVersionFor(app.packageName)?.let { historicalVersion ->
            repo.insertVersion(
                TrackedVersion(
                    versionId = "${app.packageName}-history".hashCode(),
                    versionCode = (app.versionCode - 2).coerceAtLeast(1),
                    packageName = app.packageName,
                    versionName = historicalVersion.versionName,
                    lastUpdateTime = now - timeline.historicalAgeDays * DemoDayMillis,
                    targetSdk = historicalVersion.targetSdk,
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
                lastUpdateTime = now - timeline.previousAgeDays * DemoDayMillis,
                targetSdk = olderSdk,
            ),
        )
        repo.insertVersion(
            TrackedVersion(
                versionId = "${app.packageName}-cur".hashCode(),
                versionCode = app.versionCode,
                packageName = app.packageName,
                versionName = app.versionName,
                lastUpdateTime = now - timeline.currentAgeDays * DemoDayMillis,
                targetSdk = app.sdkVersion,
            ),
        )
    }
}

private data class DemoHistoricalVersion(
    val versionName: String,
    val targetSdk: Int,
)

private data class DemoTimeline(
    val currentAgeDays: Long,
    val previousAgeDays: Long,
    val historicalAgeDays: Long,
)

private fun historicalVersionFor(packageName: String): DemoHistoricalVersion? =
    when (packageName) {
        // Every historical transition must also change target SDK, not just version name.
        "com.android.chrome" -> DemoHistoricalVersion("1.35.0", ApiLevel.latestMinus(3))
        "com.google.android.youtube" -> DemoHistoricalVersion("20.25.30", ApiLevel.latestMinus(3))
        "com.google.android.gm" -> DemoHistoricalVersion("2025.05.01", ApiLevel.latestMinus(2))
        "com.google.android.apps.maps" -> DemoHistoricalVersion("11.134.0", ApiLevel.latestMinus(3))
        "com.spotify.music" -> DemoHistoricalVersion("8.8.0", ApiLevel.latestMinus(4))
        "com.whatsapp" -> DemoHistoricalVersion("2.1.0", ApiLevel.latestMinus(3))
        "com.bernaferrari.sdkmonitor" -> DemoHistoricalVersion("2.0.0", ApiLevel.latestMinus(3))
        else -> null
    }

/**
 * App releases follow different cadences. Keeping these deliberately irregular prevents the demo
 * change log from looking like every app updated together, while preserving version chronology.
 */
private fun demoTimelineFor(packageName: String): DemoTimeline =
    when (packageName) {
        "com.bernaferrari.sdkmonitor" -> DemoTimeline(2, 61, 156)
        "com.google.android.gms" -> DemoTimeline(1, 32, 120)
        "com.android.vending" -> DemoTimeline(6, 43, 150)
        "com.whatsapp" -> DemoTimeline(3, 38, 124)
        "com.android.chrome" -> DemoTimeline(11, 52, 166)
        "com.google.android.youtube" -> DemoTimeline(19, 78, 204)
        "com.google.android.gm" -> DemoTimeline(33, 103, 258)
        "com.google.android.apps.maps" -> DemoTimeline(48, 136, 312)
        "com.spotify.music" -> DemoTimeline(73, 188, 410)
        "com.discord" -> DemoTimeline(8, 55, 170)
        "org.thoughtcrime.securesms" -> DemoTimeline(25, 89, 210)
        "org.mozilla.firefox" -> DemoTimeline(41, 126, 270)
        "com.instagram.android" -> DemoTimeline(67, 180, 360)
        "com.twitter.android" -> DemoTimeline(112, 270, 480)
        "com.nu.production" -> DemoTimeline(154, 365, 620)
        "com.google.android.apps.photos" -> DemoTimeline(5, 46, 145)
        "com.reddit.frontpage" -> DemoTimeline(14, 64, 185)
        "com.linkedin.android" -> DemoTimeline(91, 230, 430)
        else -> {
            val packageSeed = packageName.sumOf(Char::code).toLong()
            val currentAgeDays = 5L + packageSeed % 140L
            val previousAgeDays = currentAgeDays + 30L + packageSeed % 60L
            DemoTimeline(
                currentAgeDays = currentAgeDays,
                previousAgeDays = previousAgeDays,
                historicalAgeDays = previousAgeDays + 75L + packageSeed % 120L,
            )
        }
    }

private const val DemoDayMillis = 86_400_000L

fun AppDatabase.asRoomAppsRepository(
    formatTimestamp: (Long) -> String = { it.toString() },
): RoomAppsRepository = RoomAppsRepository(snapsDao(), versionsDao(), formatTimestamp)
