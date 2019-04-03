package com.bernaferrari.sdkmonitor.main

import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import io.reactivex.Observable


interface MainDataSource {

    fun setShouldShowSystemApps(value: Boolean)

    fun shouldShowSystemApps(): Observable<Boolean>

    fun shouldOrderBySdk(): Observable<Boolean>

    fun getAppsList(): Observable<List<App>>

    fun getLastItem(packageName: String): Version?

    fun mapSdkDate(app: App): AppVersion
}
