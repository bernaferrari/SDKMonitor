package com.bernaferrari.sdkmonitor.domain.logic

import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.domain.TrackedApp
import com.bernaferrari.sdkmonitor.domain.TrackedVersion

object LogEntryLogic {
    fun filterTrackedApps(
        apps: List<TrackedApp>,
        filter: AppFilter,
    ): List<TrackedApp> =
        when (filter) {
            AppFilter.ALL_APPS -> apps
            AppFilter.USER_APPS -> apps.filter { !it.isSystemApp }
            AppFilter.SYSTEM_APPS -> apps.filter { it.isSystemApp }
        }

    /**
     * Build change-log entries from version history (only real version/SDK diffs).
     */
    fun buildChangeLogs(
        versions: List<TrackedVersion>,
        apps: List<TrackedApp>,
        filter: AppFilter,
    ): List<LogEntry> {
        val filteredApps = filterTrackedApps(apps, filter)
        val appMap = apps.associateBy { it.packageName }
        val filteredPackageNames = filteredApps.map { it.packageName }.toSet()

        val versionsByPackage =
            versions
                .filter { it.packageName in filteredPackageNames }
                .groupBy { it.packageName }
                .mapValues { (_, list) -> list.sortedBy { it.lastUpdateTime } }

        val logEntries = mutableListOf<LogEntry>()

        versionsByPackage.forEach { (packageName, packageVersions) ->
            val app = appMap[packageName] ?: return@forEach
            packageVersions.forEachIndexed { index, currentVersion ->
                val previousVersion = if (index > 0) packageVersions[index - 1] else null
                if (previousVersion == null) return@forEachIndexed

                val hasVersionChange = previousVersion.versionName != currentVersion.versionName
                val hasSdkChange = previousVersion.targetSdk != currentVersion.targetSdk
                if (!hasVersionChange && !hasSdkChange) return@forEachIndexed

                logEntries.add(
                    LogEntry(
                        id = currentVersion.versionId.toLong(),
                        packageName = currentVersion.packageName,
                        appName = app.title,
                        oldSdk = previousVersion.targetSdk,
                        newSdk = currentVersion.targetSdk,
                        oldVersion = previousVersion.versionName,
                        newVersion = currentVersion.versionName,
                        timestamp = currentVersion.lastUpdateTime,
                    ),
                )
            }
        }

        return logEntries.sortedByDescending { it.timestamp }
    }
}
