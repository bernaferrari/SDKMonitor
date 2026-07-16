package com.bernaferrari.sdkmonitor.core

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bernaferrari.sdkmonitor.domain.AppDetails
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.TrackedApp
import com.bernaferrari.sdkmonitor.domain.TrackedVersion
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.extensions.darken
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class AppManager(
    private val context: Context,
    private val appsRepository: AppsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val notificationManager: NotificationManager,
) {
    private val packageManager: PackageManager = context.packageManager

    // In-memory icon cache to avoid repeated OS calls
    private val iconCache = java.util.concurrent.ConcurrentHashMap<String, android.graphics.drawable.Drawable>()

    // Track first sync progress
    private val _isFirstSync = MutableStateFlow(false)
    val isFirstSync: StateFlow<Boolean> = _isFirstSync.asStateFlow()

    private val _syncProgress = MutableStateFlow(SyncProgress())
    val syncProgress: StateFlow<SyncProgress> = _syncProgress.asStateFlow()

    data class SyncProgress(
        val current: Int = 0,
        val total: Int = 0,
        val isActive: Boolean = false,
    )

        private fun isUserApp(ai: ApplicationInfo?): Boolean {
            if (ai == null) return false
            val mask = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
            return ai.flags and mask == 0
        }

        fun getPackages(): List<PackageInfo> = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

        suspend fun removePackageName(packageName: String) =
            withContext(Dispatchers.IO) {
                try {
                    Napier.d("🗑️ Removing all data for package: $packageName")

                    // Remove app and all its versions/logs
                    appsRepository.deleteApp(packageName)
                    appsRepository.deleteAllVersionsForApp(packageName)

                    Napier.d("✅ Successfully removed all data for: $packageName")
                } catch (e: Exception) {
                    Napier.e("❌ Failed to remove package data: $packageName", e)
                    throw e
                }
            }

        suspend fun insertNewVersion(packageInfo: PackageInfo) {
            if (packageInfo.applicationInfo == null) return

            val versionCode = packageInfo.longVersionCode

            val currentTargetSDK = packageInfo.applicationInfo!!.targetSdkVersion
            val lastVersion = appsRepository.getLastVersion(packageInfo.packageName)?.targetSdk

            if (lastVersion != currentTargetSDK) {
                val versionId =
                    "${packageInfo.packageName} $versionCode ${packageInfo.versionName ?: ""} $currentTargetSDK".hashCode()
                val version =
                    TrackedVersion(
                        versionId = versionId,
                        versionCode = versionCode,
                        packageName = packageInfo.packageName,
                        versionName = packageInfo.versionName ?: "",
                        lastUpdateTime = packageInfo.lastUpdateTime,
                        targetSdk = currentTargetSDK,
                    )

                appsRepository.insertVersion(version)

                if (lastVersion != null) {
                    showTargetSDKChangeNotification(packageInfo, lastVersion, currentTargetSDK)
                }
            }
        }

        private suspend fun showTargetSDKChangeNotification(
            packageInfo: PackageInfo,
            oldTargetSDK: Int,
            newTargetSDK: Int,
        ) {
            // Only show notifications for user apps (from Play Store)
            if (!isUserApp(packageInfo.applicationInfo)) {
                Napier.d("🔕 Skipping notification for system app: ${packageInfo.packageName}")
                return
            }

            val appName = getAppLabel(packageInfo)
            notificationManager.showSdkChangeNotification(
                appName = appName,
                packageName = packageInfo.packageName,
                oldSdk = oldTargetSDK,
                newSdk = newTargetSDK,
                appIcon = getAppIcon(packageInfo.applicationInfo),
            )
        }

        private fun getAppLabel(packageInfo: PackageInfo) =
            packageInfo.applicationInfo?.let {
                packageManager.getApplicationLabel(it).toString().trim()
            } ?: ""

        suspend fun insertNewApp(packageInfo: PackageInfo) {
            if (appsRepository.getAppsMap()[packageInfo.packageName] != null) return
            if (packageInfo.applicationInfo == null) return

//            val icon = packageManager.getApplicationIcon(packageInfo.applicationInfo!!).toBitmap()
//            val backgroundColor = getPaletteColor(Palette.from(icon).generate())
            val label = getAppLabel(packageInfo)

            appsRepository.insertApp(
                TrackedApp(
                    packageName = packageInfo.packageName,
                    title = label,
                    backgroundColor = 0,
                    isFromPlayStore = isUserApp(packageInfo.applicationInfo),
                ),
            )
        }

        private fun getPaletteColor(
            palette: Palette?,
            defaultColor: Int = 0,
        ) = when {
            palette?.darkVibrantSwatch != null -> palette.getDarkVibrantColor(defaultColor)
            palette?.vibrantSwatch != null -> palette.getVibrantColor(defaultColor)
            palette?.mutedSwatch != null -> palette.getMutedColor(defaultColor)
            palette?.darkMutedSwatch != null -> palette.getDarkMutedColor(defaultColor)
            palette?.lightMutedSwatch != null -> palette.getMutedColor(defaultColor).darken
            palette?.lightVibrantSwatch != null -> palette.getLightVibrantColor(defaultColor).darken
            else -> defaultColor
        }

        suspend fun getPackagesWithUserPrefs(): List<PackageInfo> {
            val preferences = preferencesRepository.getUserPreferences().first()
            return if (preferences.appFilter === AppFilter.USER_APPS) {
                getPackages()
            } else {
                getPackagesWithOrigin()
            }
        }

        private fun getPackagesWithOrigin(): List<PackageInfo> =
            packageManager
                .getInstalledPackages(PackageManager.GET_META_DATA)
                .filter { isUserApp(it.applicationInfo) }

        fun getPackageInfo(packageName: String): PackageInfo? =
            try {
                packageManager.getPackageInfo(packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }

        private fun getAppTitle(packageName: String): String {
            return try {
                val appInfo = getPackageInfo(packageName)?.applicationInfo ?: return packageName
                packageManager.getApplicationLabel(appInfo).toString().trim()
            } catch (e: Exception) {
                packageName
            }
        }

        fun getAppDetails(packageName: String): AppDetails {
            val packageInfo = getPackageInfo(packageName)
            val appInfo = packageInfo?.applicationInfo

            return AppDetails(
                packageName = packageName,
                title = getAppTitle(packageName),
                versionName = packageInfo?.versionName ?: "Unknown",
                versionCode = packageInfo?.longVersionCode ?: 0,
                targetSdk = appInfo?.targetSdkVersion ?: 0,
                minSdk = appInfo?.minSdkVersion ?: 0,
                size =
                    try {
                        context.packageManager
                            .getApplicationInfo(
                                packageName,
                                0,
                            ).sourceDir
                            ?.let { sourceDir ->
                                java.io.File(sourceDir).length()
                            } ?: 0
                    } catch (e: Exception) {
                        0
                    },
                lastUpdateTime =
                    packageInfo?.lastUpdateTime?.convertTimestampToDate(context)
                        ?: "Unknown",
                isSystemApp = !isUserApp(appInfo),
            )
        }

        /**
         * Get app icon from cache or load from OS if not cached.
         * This is the primary method for getting app icons - use this everywhere.
         */
        fun getAppIconCached(packageName: String): android.graphics.drawable.Drawable? {
            // Return from cache if available
            iconCache[packageName]?.let { return it }

            // Load from OS and cache
            return try {
                val drawable = packageManager.getApplicationIcon(packageName)
                iconCache[packageName] = drawable
                drawable
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }

        /**
         * Clear the icon cache (e.g., when app is uninstalled)
         */
        fun clearIconCache(packageName: String? = null) {
            if (packageName != null) {
                iconCache.remove(packageName)
            } else {
                iconCache.clear()
            }
        }

        /**
         * Get app icon as bitmap for notifications
         */
        private suspend fun getAppIcon(appInfo: ApplicationInfo?): Bitmap? =
            withContext(Dispatchers.IO) {
                if (appInfo == null) return@withContext null

                try {
                    // Use cached version
                    val drawable = getAppIconCached(appInfo.packageName)
                    drawable?.toBitmap()
                } catch (e: Exception) {
                    null
                }
            }

        suspend fun syncAllApps() {
            try {
                Napier.d("🔄 Starting app sync with cleanup")

                // Check if this is first sync (empty database)
                val dbApps = appsRepository.getAllApps()
                val isFirstTime = dbApps.isEmpty()

                if (isFirstTime) {
                    _isFirstSync.value = true
                }

                // Get all installed packages
                val installedPackages =
                    packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
                val installedPackageNames = installedPackages.map { it.packageName }.toSet()
                val dbPackageNames = dbApps.map { it.packageName }.toSet()

                // Find apps in DB but not installed anymore
                val uninstalledPackages = dbPackageNames - installedPackageNames

                // Clean up uninstalled apps
                if (uninstalledPackages.isNotEmpty()) {
                    Napier.d("🧹 Cleaning up ${uninstalledPackages.size} uninstalled apps")
                    uninstalledPackages.forEach { packageName ->
                        removePackageName(packageName)
                        Napier.d("🗑️ Removed uninstalled app: $packageName")
                    }
                }

                // Start progress tracking for first sync
                if (isFirstTime) {
                    _syncProgress.value =
                        SyncProgress(
                            current = 0,
                            total = installedPackages.size,
                            isActive = true,
                        )
                }

                // Sync all currently installed apps
                installedPackages.forEachIndexed { index, packageInfo ->
                    try {
                        insertNewApp(packageInfo)
                        insertNewVersion(packageInfo)

                        // Update progress for first sync
                        if (isFirstTime) {
                            _syncProgress.value =
                                _syncProgress.value.copy(
                                    current = index + 1,
                                )
                        }
                    } catch (e: Exception) {
                        Napier.e("❌ Failed to sync package: ${packageInfo.packageName}", e)
                    }
                }

                // Complete first sync
                if (isFirstTime) {
                    _syncProgress.value = SyncProgress(isActive = false)
                    _isFirstSync.value = false
                }

                Napier.d("✅ App sync completed with cleanup")
            } catch (e: Exception) {
                _syncProgress.value = SyncProgress(isActive = false)
                _isFirstSync.value = false
                Napier.e("❌ Failed to sync apps", e)
                throw e
            }
        }
    }
