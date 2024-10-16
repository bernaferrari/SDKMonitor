package com.bernaferrari.sdkmonitor.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.MainActivity
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.extensions.darken
import io.karn.notify.Notify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppManager {

    private const val PACKAGE_ANDROID_VENDING = "com.android.vending"
    private const val OUTSIDE_STORE = "com.google.android.packageinstaller"
    private const val PREF_DISABLED_PACKAGES = "disabled_packages"

    private lateinit var packageManager: PackageManager
    var forceRefresh = true

    fun init(context: Context) {
        packageManager = context.packageManager
    }

    private fun isUserApp(ai: ApplicationInfo?): Boolean {
        // https://stackoverflow.com/a/14665381/4418073
        if (ai == null) return false
        val mask = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        return ai.flags and mask == 0
    }

    // verifies if app came from Play Store or was installed manually
    fun doesAppHasOrigin(packageName: String): Boolean {
        return isUserApp(AppManager.getApplicationInfo(packageName))
    }

    fun getPackages(): List<PackageInfo> =
        packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

    suspend fun removePackageName(packageName: String) = withContext(Dispatchers.IO) {
        Injector.get().appsDao().deleteApp(packageName)
    }

    fun insertNewVersion(packageInfo: PackageInfo) {

        if (packageInfo.applicationInfo == null) return

        val versionCode =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }

        val currentTargetSDK = packageInfo.applicationInfo!!.targetSdkVersion

        val lastVersion = Injector.get().versionsDao().getLastTargetSDK(packageInfo.packageName)

        if (lastVersion != currentTargetSDK) {

            val version = Version(
                version = versionCode,
                packageName = packageInfo.packageName,
                versionName = packageInfo.versionName ?: "",
                lastUpdateTime = packageInfo.lastUpdateTime,
                targetSdk = currentTargetSDK
            )

            Injector.get().versionsDao().insertVersion(version)

            if (lastVersion != null) {
                val appContext = Injector.get().appContext()
                Notify.with(appContext)
                    .header { this.icon = R.drawable.ic_target }
                    .meta {
                        this.clickIntent = PendingIntent.getActivity(
                            appContext, 0,
                            Intent(appContext, MainActivity::class.java), 0
                        )
                    }.content {
                        title = "TargetSDK changed for ${getAppLabel(packageInfo)}!"
                        text = "Went from $lastVersion to $currentTargetSDK"
                    }
                    .show()
            }
        }
    }

    // there are apps with extra space on the name
    private fun getAppLabel(packageInfo: PackageInfo) =
        packageManager.getApplicationLabel(packageInfo.applicationInfo!!).toString().trim()

    fun insertNewApp(packageInfo: PackageInfo) {

        if (Injector.get().appsDao().getAppString(packageInfo.packageName) != null) return

        if (packageInfo.applicationInfo == null) return

        val icon = packageManager.getApplicationIcon(packageInfo.applicationInfo!!).toBitmap()
        val backgroundColor = getPaletteColor(Palette.from(icon).generate())
        val label = getAppLabel(packageInfo)

        Injector.get().appsDao().insertApp(
            App(
                packageName = packageInfo.packageName,
                title = label,
                backgroundColor = backgroundColor,
                isFromPlayStore = isUserApp(packageInfo.applicationInfo)
            )
        )
    }

    private fun getPaletteColor(palette: Palette?, defaultColor: Int = 0) = when {
        palette?.darkVibrantSwatch != null -> palette.getDarkVibrantColor(defaultColor)
        palette?.vibrantSwatch != null -> palette.getVibrantColor(defaultColor)
        palette?.mutedSwatch != null -> palette.getMutedColor(defaultColor)
        palette?.darkMutedSwatch != null -> palette.getDarkMutedColor(defaultColor)
        palette?.lightMutedSwatch != null -> palette.getMutedColor(defaultColor).darken
        palette?.lightVibrantSwatch != null -> palette.getLightVibrantColor(defaultColor).darken
        else -> defaultColor
    }

    fun getPackagesWithUserPrefs(): List<PackageInfo> {
        return if (Injector.get().showSystemApps().get()) {
            getPackages()
        } else {
            getPackagesWithOrigin()
        }
    }

    private fun getPackagesWithOrigin(): List<PackageInfo> {
        return packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            .filter { isUserApp(it.applicationInfo) }
    }

    fun getPackageInfo(packageName: String): PackageInfo? {
        return try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun getApplicationInfo(packageName: String): ApplicationInfo? {
        return getPackageInfo(packageName)?.applicationInfo
    }

    fun getIconFromId(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(getApplicationInfo(packageName)!!)
        } catch (e: Exception) {
            null
        }
    }

}