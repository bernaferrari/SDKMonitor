package com.bernaferrari.sdkmonitor.domain

/**
 * Shared domain models used by the Android app and the web/desktop demo.
 */
data class AppDetails(
    val packageName: String,
    val title: String,
    val versionName: String,
    val versionCode: Long,
    val targetSdk: Int,
    val minSdk: Int,
    val size: Long,
    val lastUpdateTime: String,
    val isSystemApp: Boolean = false,
)

data class AppVersion(
    val packageName: String,
    val title: String,
    val sdkVersion: Int,
    val lastUpdateTime: String,
    val versionName: String = "",
    val versionCode: Long = 0L,
    /** ARGB color; stored as [Long] for multiplatform (demo uses hex literals). */
    val backgroundColor: Long = 0L,
    /** User-installed / non-system app on Android; demo maps from [isSystemApp]. */
    val isFromPlayStore: Boolean = false,
    val isSystemApp: Boolean = false,
)

/**
 * Platform-agnostic app row (maps from Room [App] entity — Room lives in commonMain).
 * [isFromPlayStore] means user-installed / non-system on Android.
 */
data class TrackedApp(
    val packageName: String,
    val title: String,
    val backgroundColor: Int,
    val isFromPlayStore: Boolean,
) {
    val isSystemApp: Boolean get() = !isFromPlayStore
}

/**
 * Platform-agnostic version row (maps from Room [Version] entity in commonMain).
 */
data class TrackedVersion(
    val versionId: Int,
    val versionCode: Long,
    val packageName: String,
    val versionName: String,
    val lastUpdateTime: Long,
    val targetSdk: Int,
)

data class UserPreferences(
    val lightMode: Boolean = true,
    val appFilter: AppFilter = AppFilter.USER_APPS,
    val backgroundSync: Boolean = false,
    val orderBySdk: Boolean = false,
    val syncInterval: String = "30m",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themePalette: ThemePalette = ThemePalette.DYNAMIC,
)

data class LogEntry(
    val id: Long,
    val packageName: String,
    val appName: String,
    val oldSdk: Int?,
    val newSdk: Int,
    val oldVersion: String?,
    val newVersion: String,
    val timestamp: Long,
)

enum class AppFilter {
    ALL_APPS,
    USER_APPS,
    SYSTEM_APPS,
}

enum class SortOption {
    NAME,
    SDK,
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

enum class ThemePalette(
    val token: String,
    val seedArgb: Long?,
) {
    DYNAMIC("dynamic", null),
    EMBER("ember", 0xFFFF2D1F),
    SOLAR("solar", 0xFFFF8A00),
    CITRINE("citrine", 0xFFC5C500),
    GROVE("grove", 0xFF00C96B),
    LAGOON("lagoon", 0xFF00CDB0),
    TIDE("tide", 0xFF00B8D4),
    AZURE("azure", 0xFF1677FF),
    ORCHID("orchid", 0xFF9B51FF),
    BERRY("berry", 0xFFF2387A),
    ;

    companion object {
        fun fromToken(token: String?): ThemePalette? =
            entries.firstOrNull { it.token == token?.lowercase() }
    }
}

enum class LocalTimeUnit(
    val code: Int,
) {
    MINUTES(0),
    HOURS(1),
    DAYS(2),
}

data class SdkDistribution(
    val sdkVersion: Int,
    val appCount: Int,
    val percentage: Float,
)

data class SettingsPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themePalette: ThemePalette = ThemePalette.DYNAMIC,
    val appFilter: AppFilter = AppFilter.ALL_APPS,
    val backgroundSync: Boolean = false,
    val orderBySdk: Boolean = false,
    val syncInterval: String = "30",
    val syncLocalTimeUnit: LocalTimeUnit = LocalTimeUnit.MINUTES,
)
