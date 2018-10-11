package com.bernaferrari.sdkmonitor

import android.support.multidex.MultiDexApplication
import timber.log.Timber

class MainApplication : MultiDexApplication() {

    lateinit var component: SingletonComponent

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        component = DaggerSingletonComponent.builder()
            .contextModule(ContextModule(this))
            .appModule(AppModule(this))
            .build()

        Timber.plant(Timber.DebugTree())
        AppManager.init(this)
    }

    companion object {
        private var INSTANCE: MainApplication? = null

        @JvmStatic
        fun get(): MainApplication =
            INSTANCE ?: throw NullPointerException("MainApplication INSTANCE must not be null")
    }
}