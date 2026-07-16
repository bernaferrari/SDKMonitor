package com.bernaferrari.sdkmonitor.shared.mock

import com.bernaferrari.sdkmonitor.domain.AppDetails
import com.bernaferrari.sdkmonitor.domain.AppListLogic
import com.bernaferrari.sdkmonitor.domain.AppVersion

/**
 * Sample data mimicking a real device library so the web demo shows SDK Monitor behavior offline.
 */
object MockDemoData {
    private val seedApps: List<AppVersion> =
        listOf(
            AppVersion("com.android.chrome", "Chrome", 35, "", "1.37.0", 1370, 0xFF4285F4),
            AppVersion("com.google.android.youtube", "YouTube", 35, "", "20.28.36", 202836, 0xFFFF0000),
            AppVersion("com.google.android.gm", "Gmail", 36, "", "2025.06.15", 20250615, 0xFFEA4335),
            AppVersion("com.google.android.apps.maps", "Google Maps", 35, "", "11.137.1", 111371, 0xFF34A853),
            AppVersion("com.spotify.music", "Spotify", 34, "", "9.0.0", 900, 0xFF1DB954),
            AppVersion("com.whatsapp", "WhatsApp", 35, "", "2.3.0", 230, 0xFF25D366),
            AppVersion("com.instagram.android", "Instagram", 34, "", "3.5.8", 358, 0xFFE1306C),
            AppVersion("org.thoughtcrime.securesms", "Signal", 35, "", "7.58.1", 7581, 0xFF3A76F0),
            AppVersion(
                "com.google.android.gms",
                "Google Play services",
                36,
                "",
                "25.2.0",
                2520,
                0xFF34A853,
                isSystemApp = true,
            ),
            AppVersion(
                "com.android.vending",
                "Google Play Store",
                36,
                "",
                "45.2.0",
                4520,
                0xFF01875F,
                isSystemApp = true,
            ),
            AppVersion("org.mozilla.firefox", "Firefox", 35, "", "1.39.0", 1390, 0xFFFF7139),
            AppVersion("com.discord", "Discord", 34, "", "2.5.2", 252, 0xFF5865F2),
            AppVersion("com.bernaferrari.sdkmonitor", "SDK Monitor", 36, "", "2.0.3", 17, 0xFFFF8364),
            AppVersion("com.nu.production", "Nubank", 34, "", "8.143.0", 81430, 0xFF820AD1),
        )

    /** X is deliberately kept on the newest SDK represented by the rest of the mock device. */
    val apps: List<AppVersion> =
        seedApps +
            AppVersion(
                packageName = "com.twitter.android",
                title = "X",
                sdkVersion = seedApps.maxOf(AppVersion::sdkVersion),
                lastUpdateTime = "",
                versionName = "10.5.0",
                versionCode = 1050,
                backgroundColor = 0xFF000000,
            )

    fun previousVersionName(packageName: String): String =
        when (packageName) {
            "com.android.chrome" -> "1.36.0"
            "com.google.android.youtube" -> "20.27.35"
            "com.google.android.gm" -> "2025.06.01"
            "com.google.android.apps.maps" -> "11.136.0"
            "com.spotify.music" -> "8.9.2"
            "com.whatsapp" -> "2.2.0"
            "com.instagram.android" -> "3.5.7"
            "org.thoughtcrime.securesms" -> "7.57.0"
            "com.google.android.gms" -> "25.1.0"
            "com.android.vending" -> "45.1.4"
            "org.mozilla.firefox" -> "1.38.2"
            "com.twitter.android" -> "10.4.1"
            "com.discord" -> "2.5.1"
            "com.bernaferrari.sdkmonitor" -> "2.0.2"
            "com.nu.production" -> "8.142.0"
            else -> "1.0.0"
        }

    fun detailsFor(packageName: String): AppDetails? {
        val app = apps.find { it.packageName == packageName } ?: return null
        return AppDetails(
            packageName = app.packageName,
            title = app.title,
            versionName = app.versionName,
            versionCode = app.versionCode,
            targetSdk = app.sdkVersion,
            minSdk = if (app.isSystemApp) 28 else 24,
            size = 45_000_000L + app.versionCode % 100_000_000,
            lastUpdateTime = app.lastUpdateTime,
            isSystemApp = app.isSystemApp,
        )
    }

    fun sdkDistribution(apps: List<AppVersion>): Map<Int, Int> = AppListLogic.sdkDistribution(apps)
}
