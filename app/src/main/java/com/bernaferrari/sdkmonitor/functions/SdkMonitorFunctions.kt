package com.bernaferrari.sdkmonitor.functions

import android.content.Intent
import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.service.AppFunction
import com.bernaferrari.sdkmonitor.MainActivity
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.extensions.apiToVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class SdkMonitorFunctions(
    private val appManager: AppManager,
    private val appsRepository: AppsRepository,
) {
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

            val appVersion =
                resolveApp(query)
                    ?: throw AppFunctionElementNotFoundException("No installed app matches \"$query\"")

            toAppTargetSdk(appVersion)
        }

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