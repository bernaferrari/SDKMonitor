package com.bernaferrari.sdkmonitor

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.bernaferrari.sdkmonitor.functions.SdkMonitorFunctions
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App :
    Application(),
    Configuration.Provider,
    AppFunctionConfiguration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var sdkMonitorFunctions: SdkMonitorFunctions

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build()

    override val appFunctionConfiguration: AppFunctionConfiguration
        get() =
            AppFunctionConfiguration
                .Builder()
                .addEnclosingClassFactory(SdkMonitorFunctions::class.java) { sdkMonitorFunctions }
                .build()
}
