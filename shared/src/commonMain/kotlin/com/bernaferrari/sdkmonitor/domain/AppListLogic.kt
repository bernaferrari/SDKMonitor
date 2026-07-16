package com.bernaferrari.sdkmonitor.domain

/**
 * Pure list filtering, search, and sort shared between the Android app and the web/desktop demo.
 */
object AppListLogic {
    fun filterByAppFilter(
        apps: List<AppVersion>,
        filter: AppFilter,
    ): List<AppVersion> =
        when (filter) {
            AppFilter.ALL_APPS -> apps
            AppFilter.USER_APPS -> apps.filter { !it.isSystemApp }
            AppFilter.SYSTEM_APPS -> apps.filter { it.isSystemApp }
        }

    fun filterLogsByAppFilter(
        logs: List<LogEntry>,
        apps: List<AppVersion>,
        filter: AppFilter,
    ): List<LogEntry> {
        if (filter == AppFilter.ALL_APPS) return logs
        val systemPackages = apps.filter { it.isSystemApp }.map { it.packageName }.toSet()
        return when (filter) {
            AppFilter.ALL_APPS -> logs
            AppFilter.USER_APPS -> logs.filter { it.packageName !in systemPackages }
            AppFilter.SYSTEM_APPS -> logs.filter { it.packageName in systemPackages }
        }
    }

    fun searchApps(
        apps: List<AppVersion>,
        query: String,
        normalize: (String) -> String = { it.lowercase() },
    ): List<AppVersion> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return apps
        val needle = normalize(trimmed)
        return apps.filter { app ->
            normalize(app.title).contains(needle) ||
                normalize(app.packageName).contains(needle)
        }
    }

    fun sortApps(
        apps: List<AppVersion>,
        sortOption: SortOption,
        orderBySdkPreference: Boolean = false,
    ): List<AppVersion> {
        val bySdk = orderBySdkPreference || sortOption == SortOption.SDK
        return if (bySdk) {
            apps.sortedWith(
                compareByDescending<AppVersion> { it.sdkVersion }
                    .thenBy { it.title.lowercase() },
            )
        } else {
            apps.sortedBy { it.title.lowercase() }
        }
    }

    fun sdkDistribution(apps: List<AppVersion>): Map<Int, Int> = apps.groupingBy { it.sdkVersion }.eachCount()

    fun applyListPipeline(
        apps: List<AppVersion>,
        filter: AppFilter,
        sortOption: SortOption,
        orderBySdk: Boolean,
        searchQuery: String,
        normalize: (String) -> String = { it.lowercase() },
    ): List<AppVersion> {
        val filtered = filterByAppFilter(apps, filter)
        val sorted = sortApps(filtered, sortOption, orderBySdk)
        return searchApps(sorted, searchQuery, normalize)
    }
}