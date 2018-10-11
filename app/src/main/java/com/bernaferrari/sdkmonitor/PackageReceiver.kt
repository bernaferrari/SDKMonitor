package com.bernaferrari.sdkmonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class PackageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val packageName = intent.data?.encodedSchemeSpecificPart ?: ""

        when {
            intent.action == Intent.ACTION_PACKAGE_ADDED -> {
                // Package installed
                Timber.d("Package installed - %s", packageName)
                PackageService.startActionAddPackage(context, packageName)
            }
            intent.action == Intent.ACTION_PACKAGE_REPLACED -> {
                // Package updated
                Timber.d("Package updated - %s", packageName)
                PackageService.startActionFetchUpdate(context, packageName)
            }
            intent.action == Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                // Package uninstalled
                Timber.d("Package uninstalled - %s", packageName)
                PackageService.startActionRemovePackage(context, packageName)
            }
        }
    }
}
