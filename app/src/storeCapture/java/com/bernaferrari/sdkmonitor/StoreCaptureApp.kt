package com.bernaferrari.sdkmonitor

import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.domain.ThemePalette
import com.bernaferrari.sdkmonitor.domain.TrackedApp
import com.bernaferrari.sdkmonitor.domain.TrackedVersion
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get
import java.io.File

/**
 * The store-capture build: lists only the fictional apps that store-listing/fixture-apps.json installs,
 * and gives each its earlier versions before the first sync, so the change log and dates read like a
 * phone that has been tracking for a year. The installed APK is each app's last version.
 */
class StoreCaptureApp : App() {
    override fun onCreate() {
        super.onCreate()
        AppManager.onlyPackages = history.keys
        val marker = File(filesDir, "storecapture-seeded")
        if (marker.exists()) return
        runBlocking {
            seed(get())
            // A phone that has been tracking for a year checks daily in the background.
            get<PreferencesRepository>().run {
                updateBackgroundSync(true)
                updateSyncInterval("1d")
                // The brand's own color (the icon's red), not whatever the emulator's wallpaper suggests.
                updateThemePalette(ThemePalette.EMBER)
            }
        }
        marker.writeText("ok")
    }

    private suspend fun seed(apps: AppsRepository) {
        val now = System.currentTimeMillis()
        history.forEach { (packageName, versions) ->
            val info = runCatching { packageManager.getApplicationInfo(packageName, 0) }.getOrNull() ?: return@forEach
            apps.insertApp(TrackedApp(packageName, packageManager.getApplicationLabel(info).toString(), 0, true))
            versions.forEach { v ->
                val code = v.name.replace(".", "").toLong()
                apps.insertVersion(
                    TrackedVersion(
                        versionId = "$packageName $code ${v.name} ${v.targetSdk}".hashCode(),
                        versionCode = code,
                        packageName = packageName,
                        versionName = v.name,
                        lastUpdateTime = now - v.daysAgo * DAY - (packageName.hashCode() and 0x3FFFFFF),
                        targetSdk = v.targetSdk,
                    ),
                )
            }
        }
    }

    private class V(val name: String, val targetSdk: Int, val daysAgo: Long)

    private companion object {
        const val DAY = 86_400_000L

        /** Oldest first; the last entry is the installed APK. Mirrors store-listing/fixture-apps.json. */
        val history = mapOf(
            "app.atlasbank" to listOf(V("6.2.0", 34, 240), V("7.0.1", 35, 96), V("7.4.0", 36, 12)),
            "news.brightside" to listOf(V("3.8.0", 34, 180), V("4.1.2", 35, 27)),
            "weather.clearsky" to listOf(V("2.9.0", 35, 210), V("3.0.0", 36, 88), V("3.2.1", 37, 5)),
            "app.cookbook.kitchen" to listOf(V("5.0.3", 33, 300), V("5.4.0", 34, 64)),
            "app.fernway.maps" to listOf(V("11.2.0", 35, 150), V("11.9.4", 36, 21)),
            "chat.harbor" to listOf(V("8.1.0", 35, 190), V("8.6.2", 36, 70), V("9.0.0", 37, 2)),
            "notes.lumen" to listOf(V("1.8.0", 35, 130), V("2.0.0", 36, 33)),
            "app.metrotransit" to listOf(V("3.3.1", 31, 420)),
            "fm.moonlight" to listOf(V("4.4.0", 34, 160), V("4.7.3", 35, 44)),
            "app.parceltrack" to listOf(V("2.1.0", 33, 260)),
            "games.pixelquest" to listOf(V("1.4.2", 29, 610)),
            "app.pocketledger" to listOf(V("3.0.0", 33, 280), V("3.2.5", 34, 58)),
            "mail.quill" to listOf(V("6.0.0", 35, 140), V("6.3.1", 36, 16)),
            "photo.snapframe" to listOf(V("2.6.0", 34, 170), V("2.8.0", 35, 39)),
            "fm.tidalradio" to listOf(V("5.1.0", 33, 330)),
            "app.trailmark" to listOf(V("2.2.0", 31, 350), V("2.5.0", 32, 120)),
            "app.vaultpass" to listOf(V("4.0.0", 35, 200), V("4.3.0", 36, 77), V("4.5.2", 37, 9)),
            "music.wavelength" to listOf(V("7.2.0", 35, 110), V("7.5.1", 36, 7)),
        )
    }
}
