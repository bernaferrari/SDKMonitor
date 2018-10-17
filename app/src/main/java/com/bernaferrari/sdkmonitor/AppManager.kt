package com.bernaferrari.sdkmonitor

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

object AppManager {

    private const val PACKAGE_ANDROID_VENDING = "com.android.vending"
    private const val PREF_DISABLED_PACKAGES = "disabled_packages"

    private lateinit var packageManager: PackageManager

    fun init(context: Context) {
        packageManager = context.packageManager
    }

    fun isAppFromGooglePlay(packageName: String): Boolean {
        return try {
            packageManager.getInstallerPackageName(packageName) == PACKAGE_ANDROID_VENDING
        } catch (e: Throwable) {
            false
        }
    }

    fun getPackages(): List<PackageInfo> = packageManager.getInstalledPackages(0)

    fun getPlayStorePackages(): List<PackageInfo> {
        return packageManager.getInstalledPackages(0)
            .filter { isAppFromGooglePlay(it.packageName) }
    }

    fun getPackageInfo(packageName: String): PackageInfo {
        return packageManager.getPackageInfo(packageName, 0)
    }

    fun getApplicationInfo(packageName: String): ApplicationInfo {
        return getPackageInfo(packageName).applicationInfo
    }

    fun getAppLabel(packageInfo: PackageInfo): String {
        return packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
    }

    fun getAppIcon(packageInfo: PackageInfo): Drawable {
        return packageManager.getApplicationIcon(packageInfo.applicationInfo)
    }

    fun getIconFromId(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(getApplicationInfo(packageName))
        } catch (e: Exception) {
            null
        }
    }

}