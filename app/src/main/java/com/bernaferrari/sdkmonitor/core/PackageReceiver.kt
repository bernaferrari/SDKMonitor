package com.bernaferrari.sdkmonitor.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.orhanobut.logger.Logger

class PackageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Logger.d("Package onReceive")

        val packageName = intent.data?.encodedSchemeSpecificPart ?: ""

        when {
            intent.action == Intent.ACTION_PACKAGE_ADDED -> {
                // Package installed
                Logger.d("Package installed - $packageName")
                PackageService.startActionAddPackage(packageName)
            }
            intent.action == Intent.ACTION_PACKAGE_REPLACED -> {
                // Package updated
                Logger.d("Package updated - $packageName")
                PackageService.startActionFetchUpdate(packageName)
            }
            intent.action == Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                // Package uninstalled
                Logger.d("Package uninstalled - $packageName")
                PackageService.startActionRemovePackage(context, packageName)
            }
        }
    }
}
