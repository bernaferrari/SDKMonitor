package com.bernaferrari.sdkmonitor

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.bernaferrari.sdkmonitor.data.App

class PackageService : IntentService("PackageService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)

            when (action) {
                ACTION_FETCH_INSERT -> handleActionInsert(packageName)
                ACTION_FETCH_UPDATE -> handleActionFetchUpdate(packageName)
                ACTION_REMOVE_PACKAGE -> handleActionRemovePackage(packageName)
            }
        }
    }

    private fun handleActionRemovePackage(packageName: String) {
        Injector.get().appsDao().deleteApp(packageName)
    }

    private fun handleActionFetchUpdate(packageName: String) {
        val packageInfo = AppManager.getPackageInfo(packageName) ?: return

//        Injector.get().versionsDao().insertVersion()
    }

    private fun handleActionInsert(packageName: String) {
        val packageInfo = AppManager.getPackageInfo(packageName) ?: return

        Injector.get().appsDao().insertApp(App(packageName, AppManager.getAppLabel(packageInfo), 0))
//        Injector.get().versionsDao().insertVersion(
//            Version(
//                packageInfo
//            )
//        )

    }

    companion object {

        private const val ACTION_REMOVE_PACKAGE = "REMOVE_PACKAGE"
        private const val ACTION_FETCH_UPDATE = "FETCH_UPDATE"
        private const val ACTION_FETCH_INSERT = "FETCH_INSERT"
        private const val EXTRA_PACKAGE_NAME = "PACKAGE_NAME"

        fun startActionRemovePackage(context: Context, packageName: String) {
            val intent = Intent(context, PackageService::class.java)
            intent.action = ACTION_REMOVE_PACKAGE
            intent.putExtra(EXTRA_PACKAGE_NAME, packageName)
            context.startService(intent)
        }

        fun startActionFetchUpdate(context: Context, packageName: String) {
            val intent = Intent(context, PackageService::class.java)
            intent.action = ACTION_FETCH_UPDATE
            intent.putExtra(EXTRA_PACKAGE_NAME, packageName)
            context.startService(intent)
        }

        fun startActionAddPackage(context: Context, packageName: String) {
            val intent = Intent(context, PackageService::class.java)
            intent.action = ACTION_FETCH_INSERT
            intent.putExtra(EXTRA_PACKAGE_NAME, packageName)
            context.startService(intent)
        }
    }

}
