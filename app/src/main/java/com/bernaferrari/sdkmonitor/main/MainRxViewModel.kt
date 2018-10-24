package com.bernaferrari.sdkmonitor.main

import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.core.MvRxViewModel
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.extensions.normalizeString
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import java.util.concurrent.TimeUnit

/**
 * initialState *must* be implemented as a constructor parameter.
 */
class MainRxViewModel(initialState: MainState) : MvRxViewModel<MainState>(initialState) {

    private val mAppsDao = Injector.get().appsDao()
    private val mVersionsDao = Injector.get().versionsDao()

    val itemsList = mutableListOf<AppVersion>()
    var hasLoaded = false
    var maxListSize = 0
    val inputRelay: BehaviorRelay<String> = BehaviorRelay.create<String>()
    val showProgressRelay: BehaviorRelay<Boolean> = BehaviorRelay.create<Boolean>()

    init {
        fetchData()
    }

    private fun fetchData() = withState { _ ->
        Observables.combineLatest(
            getAppsListObservable(),
            inputRelay
        ) { list, filter ->
            list.takeIf { it.isNotEmpty() && filter.isNotBlank() }
                ?.filter { it.app.title.normalizeString().contains(filter.normalizeString()) }
                    ?: list
        }.doOnNext {
            itemsList.clear()
            itemsList.addAll(it)
        }.execute {
            copy(listOfItems = it)
        }
    }

    suspend fun fetchAllVersions(packageName: String): List<Version>? =
        withContext(Dispatchers.IO) { mVersionsDao.getAllValues(packageName) }

    fun fetchAppDetails(packageName: String): MutableList<AppDetails> {

        val packageInfo = AppManager.getPackageInfo(packageName)

        return mutableListOf<AppDetails>().apply {

            packageInfo.applicationInfo.className?.also {
                this += AppDetails("Class Name", it)
            }

            packageInfo.applicationInfo.sourceDir?.also {
                this += AppDetails("Source Dir", it)
            }

            packageInfo.applicationInfo.dataDir?.also {
                this += AppDetails("Data Dir", it)
            }
        }
    }

    var firstRun = true

    private fun getAppsListObservable(): Observable<List<AppVersion>> =

        mAppsDao.getAppsList().toObservable()
            .debounce { list ->
                // debounce with a 200ms delay all items except the first one
                val flow = Observable.just(list)
                hasLoaded = true
                flow.takeIf { list.isEmpty() }
                    ?.let { it } ?: flow.delay(200, TimeUnit.MILLISECONDS)
            }
            .skipWhile {
                if (it.isEmpty() || firstRun) {
                    firstRun = false
                    updateAll()
                }
                it.isEmpty()
            }
            .map { list ->
                val allItems = mutableListOf<AppVersion>()
                list.asSequence()
                    .sortedBy { it.title.toLowerCase() }
                    .mapTo(allItems) { app ->
                        val (sdkVersion, lastUpdate) = getSdkDate(app)
                        AppVersion(app, sdkVersion, lastUpdate)
                    }.toList()
            }.doOnNext { maxListSize = it.size }

    fun getSdkDate(app: App): Pair<Int, String> {

        // since insertApp is called before insertVersion, mVersionsDao.getValue(...) will
        // return null on app's first run. This will avoid the situation.
        val version = mVersionsDao.getLastValue(app.packageName)

        val sdkVersion =
            version?.targetSdk ?: AppManager.getApplicationInfo(app.packageName).targetSdkVersion

        val lastUpdate =
            version?.lastUpdateTime ?: AppManager.getPackageInfo(
                app.packageName
            ).lastUpdateTime

        return Pair(sdkVersion, lastUpdate.convertTimestampToDate())
    }

    fun updateAll() = GlobalScope.launch(Dispatchers.IO) {
        showProgressRelay.accept(true)

        AppManager.getPlayStorePackages()
            // this condition will only happen when app is run on emulator.
            .let { if (it.isEmpty()) AppManager.getPackages() else it }
            .forEach { packageInfo ->
                AppManager.insertNewApp(packageInfo)
                AppManager.insertNewVersion(packageInfo)
            }

        showProgressRelay.accept(false)
    }
}