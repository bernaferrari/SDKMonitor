package com.bernaferrari.sdkmonitor.shared.mock

import com.bernaferrari.sdkmonitor.domain.AppDetails
import com.bernaferrari.sdkmonitor.domain.AppListLogic
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.LogEntry

/**
 * Sample data mimicking a real device library so the web demo shows SDK Monitor behavior offline.
 */
object MockDemoData {
    val apps: List<AppVersion> =
        listOf(
            AppVersion("com.android.chrome", "Chrome", 35, "Jun 12, 2026", "1.37.0", 1370, 0xFF4285F4),
            AppVersion("com.spotify.music", "Spotify", 34, "Jun 8, 2026", "9.0.0", 900, 0xFF1DB954),
            AppVersion("com.whatsapp", "WhatsApp", 35, "Jun 15, 2026", "2.3.0", 230, 0xFF25D366),
            AppVersion("com.instagram.android", "Instagram", 34, "May 30, 2026", "3.5.8", 358, 0xFFE1306C),
            AppVersion(
                "com.google.android.gms",
                "Google Play services",
                36,
                "Jun 18, 2026",
                "25.2.0",
                2520,
                0xFF34A853,
                isSystemApp = true,
            ),
            AppVersion(
                "com.android.vending",
                "Google Play Store",
                36,
                "Jun 17, 2026",
                "45.2.0",
                4520,
                0xFF01875F,
                isSystemApp = true,
            ),
            AppVersion("org.mozilla.firefox", "Firefox", 35, "Jun 1, 2026", "1.39.0", 1390, 0xFFFF7139),
            AppVersion("com.twitter.android", "X", 33, "Apr 20, 2026", "10.5.0", 1050, 0xFF000000),
            AppVersion("com.discord", "Discord", 34, "Jun 5, 2026", "2.5.2", 252, 0xFF5865F2),
            AppVersion("com.bernaferrari.sdkmonitor", "SDK Monitor", 36, "Jun 19, 2026", "2.0.3", 17, 0xFFFF8364),
            AppVersion(
                "com.old.legacyapp",
                "Legacy Notes",
                28,
                "Jan 3, 2024",
                "1.2.0",
                12,
                0xFF795548,
                isFromPlayStore = false,
            ),
            AppVersion("com.bank.secure", "SecureBank", 35, "Jun 10, 2026", "4.8.0", 480, 0xFF1565C0),
        )

    val logs: List<LogEntry> =
        listOf(
            LogEntry(1, "com.whatsapp", "WhatsApp", 34, 35, "2.2.0", "2.3.0", 1_748_400_000_000L),
            LogEntry(2, "com.spotify.music", "Spotify", 33, 34, "8.0.0", "9.0.0", 1_747_900_000_000L),
            LogEntry(3, "com.android.chrome", "Chrome", 34, 35, "1.36.0", "1.37.0", 1_747_500_000_000L),
            LogEntry(4, "com.old.legacyapp", "Legacy Notes", 26, 28, "1.1.0", "1.2.0", 1_704_200_000_000L),
            LogEntry(5, "com.google.android.gms", "Google Play services", 35, 36, "25.1.0", "25.2.0", 1_748_500_000_000L),
        )

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
