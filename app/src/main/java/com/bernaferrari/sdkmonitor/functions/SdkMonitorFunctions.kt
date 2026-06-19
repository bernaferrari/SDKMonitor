package com.bernaferrari.sdkmonitor.functions

import android.content.Intent
import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.service.AppFunction
import com.bernaferrari.sdkmonitor.MainActivity
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.domain.model.LogEntry
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SdkMonitorFunctions
    @Inject
    constructor(
        private val appManager: AppManager,
        private val appsRepository: AppsRepository,
    ) {
        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class AppTargetSdk(
            /** The package name of the app. */
            val packageName: String,
            /** The display name of the app. */
            val appName: String,
            /** The target SDK API level the app declares. */
            val targetSdk: Int,
            /** The Android version name that corresponds to the target SDK. */
            val androidVersion: String,
            /** The minimum SDK API level the app supports. */
            val minSdk: Int,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class AppTargetSdkSummary(
            /** Installed apps matching the query. */
            val apps: List<AppTargetSdk>,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class LookupAppParams(
            /** Package name or app display name to look up. */
            val query: String,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class SearchAppsParams(
            /** Part of an app name or package name to search for. */
            val query: String,
            /** Maximum number of matching apps to return. */
            val limit: Int = 10,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class ListAppsBelowTargetSdkParams(
            /** Return apps targeting an API level strictly below this value. */
            val targetSdk: Int,
            /** Maximum number of apps to return. */
            val limit: Int = 20,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class RecentSdkChangesParams(
            /** Maximum number of recent SDK changes to return. */
            val limit: Int = 10,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class SdkChange(
            /** The package name of the app that changed. */
            val packageName: String,
            /** The display name of the app that changed. */
            val appName: String,
            /** The previous target SDK, if known. */
            val oldTargetSdk: Int?,
            /** The new target SDK. */
            val newTargetSdk: Int,
            /** When the change was recorded, as a Unix timestamp in milliseconds. */
            val timestamp: Long,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class RecentSdkChangesResult(
            /** Recent target SDK changes across installed apps. */
            val changes: List<SdkChange>,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class SdkDistributionEntry(
            /** The target SDK API level. */
            val targetSdk: Int,
            /** The Android version name for this API level. */
            val androidVersion: String,
            /** How many installed apps target this SDK level. */
            val appCount: Int,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class SdkDistribution(
            /** Total number of tracked installed apps. */
            val totalApps: Int,
            /** App counts grouped by target SDK, highest first. */
            val entries: List<SdkDistributionEntry>,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class OpenAppDetailsParams(
            /** Package name of the app to open in SDK Monitor. */
            val packageName: String,
        )

        @AppFunctionSerializable(isDescribedByKDoc = true)
        data class OpenAppDetailsResult(
            /** The package name that was opened. */
            val packageName: String,
            /** A short confirmation message. */
            val message: String,
        )

        /**
         * Returns the target and minimum SDK for an installed app.
         *
         * @param lookupAppParams Identifies the app by package name or display name.
         */
        @AppFunction(isDescribedByKDoc = true)
        suspend fun getAppTargetSdk(
            appFunctionContext: AppFunctionContext,
            lookupAppParams: LookupAppParams,
        ): AppTargetSdk =
            withContext(Dispatchers.IO) {
                val query = lookupAppParams.query.trim()
                if (query.isEmpty()) {
                    throw AppFunctionInvalidArgumentException("Query must not be empty")
                }

                val appVersion = resolveApp(query)
                    ?: throw AppFunctionElementNotFoundException("No installed app matches \"$query\"")

                toAppTargetSdk(appVersion)
            }

        /**
         * Searches installed apps by name or package name.
         *
         * @param searchAppsParams The search text and result limit.
         */
        @AppFunction(isDescribedByKDoc = true)
        suspend fun searchApps(
            appFunctionContext: AppFunctionContext,
            searchAppsParams: SearchAppsParams,
        ): AppTargetSdkSummary =
            withContext(Dispatchers.IO) {
                val query = searchAppsParams.query.trim()
                if (query.isEmpty()) {
                    throw AppFunctionInvalidArgumentException("Search query must not be empty")
                }

                val limit = searchAppsParams.limit.coerceIn(1, 50)
                val matches =
                    appsRepository
                        .getAllAppsAsAppVersions()
                        .filter { appMatchesQuery(it, query) }
                        .take(limit)
                        .map { toAppTargetSdk(it) }

                AppTargetSdkSummary(apps = matches)
            }

        /**
         * Lists installed apps whose target SDK is below the given API level.
         *
         * @param listAppsBelowTargetSdkParams The SDK threshold and result limit.
         */
        @AppFunction(isDescribedByKDoc = true)
        suspend fun listAppsBelowTargetSdk(
            appFunctionContext: AppFunctionContext,
            listAppsBelowTargetSdkParams: ListAppsBelowTargetSdkParams,
        ): AppTargetSdkSummary =
            withContext(Dispatchers.IO) {
                val threshold = listAppsBelowTargetSdkParams.targetSdk
                if (threshold <= 0) {
                    throw AppFunctionInvalidArgumentException("targetSdk must be a positive API level")
                }

                val limit = listAppsBelowTargetSdkParams.limit.coerceIn(1, 100)
                val apps =
                    appsRepository
                        .getAllAppsAsAppVersions()
                        .filter { it.sdkVersion in 1 until threshold }
                        .sortedBy { it.sdkVersion }
                        .take(limit)
                        .map { toAppTargetSdk(it) }

                AppTargetSdkSummary(apps = apps)
            }

        /**
         * Returns recent target SDK changes tracked by SDK Monitor.
         *
         * @param recentSdkChangesParams How many recent changes to include.
         */
        @AppFunction(isDescribedByKDoc = true)
        suspend fun getRecentSdkChanges(
            appFunctionContext: AppFunctionContext,
            recentSdkChangesParams: RecentSdkChangesParams,
        ): RecentSdkChangesResult =
            withContext(Dispatchers.IO) {
                val limit = recentSdkChangesParams.limit.coerceIn(1, 50)
                val changes =
                    buildSdkChangeLogs()
                        .filter { entry -> entry.oldSdk != null && entry.oldSdk != entry.newSdk }
                        .take(limit)
                        .map { entry ->
                            SdkChange(
                                packageName = entry.packageName,
                                appName = entry.appName,
                                oldTargetSdk = entry.oldSdk,
                                newTargetSdk = entry.newSdk,
                                timestamp = entry.timestamp,
                            )
                        }

                RecentSdkChangesResult(changes = changes)
            }

        /**
         * Summarizes how installed apps are distributed across target SDK levels.
         */
        @AppFunction(isDescribedByKDoc = true)
        suspend fun getSdkDistribution(
            appFunctionContext: AppFunctionContext,
        ): SdkDistribution =
            withContext(Dispatchers.IO) {
                val apps = appsRepository.getAllAppsAsAppVersions()
                val entries =
                    apps
                        .groupBy { it.sdkVersion }
                        .map { (sdk, groupedApps) ->
                            SdkDistributionEntry(
                                targetSdk = sdk,
                                androidVersion = sdk.apiToVersion(),
                                appCount = groupedApps.size,
                            )
                        }.sortedByDescending { it.targetSdk }

                SdkDistribution(
                    totalApps = apps.size,
                    entries = entries,
                )
            }

        /**
         * Opens SDK Monitor on the details screen for the given app.
         *
         * @param openAppDetailsParams The package name to show.
         */
        @AppFunction(isDescribedByKDoc = true)
        suspend fun openAppDetails(
            appFunctionContext: AppFunctionContext,
            openAppDetailsParams: OpenAppDetailsParams,
        ): OpenAppDetailsResult =
            withContext(Dispatchers.IO) {
                val packageName = openAppDetailsParams.packageName.trim()
                if (packageName.isEmpty()) {
                    throw AppFunctionInvalidArgumentException("packageName must not be empty")
                }

                if (appManager.getPackageInfo(packageName) == null) {
                    throw AppFunctionElementNotFoundException("No installed app with package \"$packageName\"")
                }

                val context = appFunctionContext.context
                val intent =
                    Intent(context, MainActivity::class.java).apply {
                        putExtra("package_name", packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                context.startActivity(intent)

                OpenAppDetailsResult(
                    packageName = packageName,
                    message = "Opened SDK Monitor details for $packageName",
                )
            }

        private fun toAppTargetSdk(appVersion: AppVersion): AppTargetSdk {
            val details = appManager.getAppDetails(appVersion.packageName)
            return AppTargetSdk(
                packageName = appVersion.packageName,
                appName = appVersion.title,
                targetSdk = details.targetSdk,
                androidVersion = details.targetSdk.apiToVersion(),
                minSdk = details.minSdk,
            )
        }

        private suspend fun resolveApp(query: String): AppVersion? {
            val apps = appsRepository.getAllAppsAsAppVersions()
            return apps.firstOrNull { it.packageName.equals(query, ignoreCase = true) }
                ?: apps.firstOrNull { it.title.equals(query, ignoreCase = true) }
                ?: apps.firstOrNull { appMatchesQuery(it, query) }
        }

        private fun appMatchesQuery(
            app: AppVersion,
            query: String,
        ): Boolean {
            val normalizedQuery = query.lowercase()
            return app.packageName.lowercase().contains(normalizedQuery) ||
                app.title.lowercase().contains(normalizedQuery)
        }

        private suspend fun buildSdkChangeLogs(): List<LogEntry> {
            val apps = appsRepository.getAllApps()
            val appMap = apps.associateBy { it.packageName }
            val allVersions = appsRepository.getAllVersions()

            val versionsByPackage =
                allVersions
                    .groupBy { it.packageName }
                    .mapValues { (_, versions) -> versions.sortedBy { it.lastUpdateTime } }

            val logEntries = mutableListOf<LogEntry>()

            versionsByPackage.forEach { (packageName, versions) ->
                val app = appMap[packageName] ?: return@forEach

                versions.forEachIndexed { index, currentVersion ->
                    val previousVersion = versions.getOrNull(index - 1) ?: return@forEachIndexed
                    if (previousVersion.targetSdk != currentVersion.targetSdk) {
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
            }

            return logEntries.sortedByDescending { it.timestamp }
        }
    }