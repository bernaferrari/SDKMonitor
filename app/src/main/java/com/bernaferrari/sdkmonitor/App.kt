package com.bernaferrari.sdkmonitor

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import androidx.work.Configuration
import com.bernaferrari.sdkmonitor.di.SdkMonitorKoinApp
import com.bernaferrari.sdkmonitor.functions.SdkMonitorFunctions
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.plugin.module.dsl.startKoin

class App :
    Application(),
    Configuration.Provider,
    AppFunctionConfiguration.Provider {
    private val sdkMonitorFunctions: SdkMonitorFunctions by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin<SdkMonitorKoinApp> {
            androidContext(this@App)
            workManagerFactory()
        }
    }

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(KoinWorkerFactory())
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build()

    override val appFunctionConfiguration: AppFunctionConfiguration
        get() =
            AppFunctionConfiguration
                .Builder()
                .addEnclosingClassFactory(SdkMonitorFunctions::class.java) { sdkMonitorFunctions }
                .build()
}