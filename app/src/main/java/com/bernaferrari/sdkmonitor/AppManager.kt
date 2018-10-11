package com.bernaferrari.sdkmonitor

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

object AppManager {

    private const val PACKAGE_ANDROID_VENDING = "com.android.vending"
    private const val PREF_DISABLED_PACKAGES = "disabled_packages"

    private lateinit var packageManager: PackageManager
//    private lateinit var ignoredPackagesPref: Preference<Set<String>>

    fun init(context: Context) {
        packageManager = context.packageManager
//        packageManager = context.packageManager
//        val prefs = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext())
//        val rxPrefs = RxSharedPreferences.create(prefs)
//        ignoredPackagesPref = rxPrefs.getStringSet(PREF_DISABLED_PACKAGES)
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

    fun getPackageInfo(packageName: String): PackageInfo? {
        return try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (nnfe: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun getAppLabel(packageInfo: PackageInfo): String {
        return packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
    }

    fun getAppIcon(packageInfo: PackageInfo): Drawable {
        return packageManager.getApplicationIcon(packageInfo.applicationInfo)
    }

    fun getIconFromId(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(getPackageInfo(packageName)?.applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

//    fun getIgnoredAppsObservable(): Observable<Set<String>> {
//        return ignoredPackagesPref.asObservable()
//    }

    fun setAppIgnored(packageName: String, ignore: Boolean) {
//        val set = ignoredPackagesPref.get() ?: emptySet()
//        if (ignore) {
//            ignoredPackagesPref.set(set.plus(packageName))
//        } else {
//            ignoredPackagesPref.set(set.minus(packageName))
//        }
    }

    fun toggleAppIgnored(packageName: String) {
        setAppIgnored(packageName, !isAppIgnored(packageName))
    }

    fun isAppIgnored(packageName: String): Boolean {
//        return ignoredPackagesPref.get()?.contains(packageName) ?: false
        return true
    }
}