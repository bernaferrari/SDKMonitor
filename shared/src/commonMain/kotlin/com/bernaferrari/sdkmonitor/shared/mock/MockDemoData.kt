package com.bernaferrari.sdkmonitor.shared.mock

import com.bernaferrari.sdkmonitor.domain.AppDetails
import com.bernaferrari.sdkmonitor.domain.AppListLogic
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.logic.ApiLevel

/**
 * Sample data mimicking a real device library so the web demo shows SDK Monitor behavior offline.
 */
object MockDemoData {
    private val latestSdk = ApiLevel.latestMinus(0)
    private val previousSdk = ApiLevel.latestMinus(1)
    private val twoVersionsBehindSdk = ApiLevel.latestMinus(2)
    private val threeVersionsBehindSdk = ApiLevel.latestMinus(3)

    private val seedApps: List<AppVersion> =
        listOf(
            AppVersion("com.android.chrome", "Chrome", previousSdk, "", "1.37.0", 1370, 0xFF4285F4),
            AppVersion("com.google.android.youtube", "YouTube", previousSdk, "", "20.28.36", 202836, 0xFFFF0000),
            AppVersion("com.google.android.gm", "Gmail", latestSdk, "", "2025.06.15", 20250615, 0xFFEA4335),
            AppVersion("com.google.android.apps.maps", "Google Maps", previousSdk, "", "11.137.1", 111371, 0xFF18884B),
            AppVersion("com.spotify.music", "Spotify", twoVersionsBehindSdk, "", "9.0.0", 900, 0xFF1ED760),
            AppVersion("com.whatsapp", "WhatsApp", previousSdk, "", "2.3.0", 230, 0xFF128C4A),
            AppVersion("com.instagram.android", "Instagram", threeVersionsBehindSdk, "", "3.5.8", 358, 0xFFC72C69),
            AppVersion("org.thoughtcrime.securesms", "Signal", previousSdk, "", "7.58.1", 7581, 0xFF3A76F0),
            AppVersion(
                "com.google.android.gms",
                "Google Play services",
                latestSdk,
                "",
                "25.2.0",
                2520,
                0xFF5F6368,
                isSystemApp = true,
            ),
            AppVersion(
                "com.android.vending",
                "Google Play Store",
                latestSdk,
                "",
                "45.2.0",
                4520,
                0xFF01875F,
                isSystemApp = true,
            ),
            AppVersion("org.mozilla.firefox", "Firefox", previousSdk, "", "1.39.0", 1390, 0xFFFF7139),
            AppVersion("com.discord", "Discord", threeVersionsBehindSdk, "", "2.5.2", 252, 0xFF5865F2),
            AppVersion("com.bernaferrari.sdkmonitor", "SDK Monitor", previousSdk, "", "2.0.3", 17, 0xFFFF8364),
            AppVersion("com.nu.production", "Nubank", threeVersionsBehindSdk, "", "8.143.0", 81430, 0xFF820AD1),
            AppVersion("com.google.android.apps.photos", "Google Photos", previousSdk, "", "7.85.0", 785000, 0xFF1769E0),
            AppVersion("com.reddit.frontpage", "Reddit", twoVersionsBehindSdk, "", "2026.28.0", 2026280, 0xFFD93A00),
            AppVersion("com.linkedin.android", "LinkedIn", previousSdk, "", "9.1.0", 9100, 0xFF0A66C2),
        )

    /** X is deliberately kept on the newest stable SDK represented by the mock device. */
    val apps: List<AppVersion> =
        seedApps +
            AppVersion(
                packageName = "com.twitter.android",
                title = "X",
                sdkVersion = latestSdk,
                lastUpdateTime = "",
                versionName = "10.5.0",
                versionCode = 1050,
                backgroundColor = 0xFF111111,
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
            "com.google.android.apps.photos" -> "7.84.0"
            "com.reddit.frontpage" -> "2026.27.0"
            "com.linkedin.android" -> "9.0.2"
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
