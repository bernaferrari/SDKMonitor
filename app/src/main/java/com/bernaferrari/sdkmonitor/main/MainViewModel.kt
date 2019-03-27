package com.bernaferrari.sdkmonitor.main

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.bernaferrari.base.mvrx.MvRxViewModel
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.extensions.doSwitchMap
import com.bernaferrari.sdkmonitor.extensions.normalizeString
import com.jakewharton.rxrelay2.BehaviorRelay
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class MainViewModel @AssistedInject constructor(
    @Assisted initialState: MainState,
    private val mAppsDao: AppsDao,
    private val mVersionsDao: VersionsDao
) : MvRxViewModel<MainState>(initialState) {

    val itemsList = mutableListOf<AppVersion>()
    var hasLoaded = false
    var maxListSize: BehaviorRelay<Int> = BehaviorRelay.create<Int>()
    val inputRelay: BehaviorRelay<String> = BehaviorRelay.create<String>()
    val showProgressRelay: BehaviorRelay<Boolean> = BehaviorRelay.create<Boolean>()

    init {
        fetchData()
    }

    private fun fetchData() = withState {
        Observables.combineLatest(
            allApps(),
            inputRelay
        ) { list, filter ->
            // get the string without special characters and filter the list.
            // If the filter is not blank, it will filter the list.
            // If it is blank, it will return the original list.
            val pattern = filter.normalizeString()
            list.takeIf { filter.isNotBlank() }
                ?.filter { pattern in it.app.title.normalizeString() }
                    ?: list
        }.doOnNext {
            itemsList.clear()
            itemsList.addAll(it)
        }.execute {
            copy(listOfItems = it)
        }
    }

    private fun allApps() = doSwitchMap(
        { Injector.get().showSystemApps().observe() },
        { Injector.get().orderBySdk().observe() }
        ) { showSystemApps, orderBySdk ->

            if (showSystemApps) {
                // return all apps
                mAppsDao.getAppsListFlowable()
            } else {
                // return only the ones from Play Store or that were installed manually.
                mAppsDao.getAppsListFlowableFiltered(hasKnownOrigin = true)
            }.toObservable()
                .getAppsListObservable(orderBySdk)
    }

    private fun Observable<List<App>>.getAppsListObservable(orderBySdk: Boolean): Observable<List<AppVersion>> =
        this.debounce { list ->
            // debounce with a 200ms delay on all items except the first one
            val flow = Observable.just(list)
            hasLoaded = true
            flow.takeIf { list.isEmpty() }
                ?.let { it } ?: flow.delay(250, TimeUnit.MILLISECONDS)
        }.skipWhile {
            // force the refresh when app is first opened or no known apps are installed (emulator)
            if (it.isEmpty() || AppManager.firstRun) {
                AppManager.firstRun = false
                updateAll()
            }
            it.isEmpty()
        }.map { list ->
            // parse correctly the values
            list.map { app ->
                val (sdkVersion, lastUpdate) = getSdkDate(app)
                AppVersion(app, sdkVersion, lastUpdate)
            }
        }.map { list ->
            // list already comes sorted by name from db, it is faster and avoids sub-querying
            if (orderBySdk) list.sortedBy { it.sdkVersion } else list
        }.doOnNext { maxListSize.accept(it.size) }

    private fun getSdkDate(app: App): Pair<Int, String> {

        // since insertApp is called before insertVersion, mVersionsDao.getValue(...) will
        // return null on app's first run. This will avoid the situation.
        val version = mVersionsDao.getLastValue(app.packageName)

        val sdkVersion =
            version?.targetSdk
                    ?: AppManager.getApplicationInfo(app.packageName)?.targetSdkVersion
                    ?: 0

        val lastUpdate =
            version?.lastUpdateTime
                    ?: AppManager.getPackageInfo(app.packageName)?.lastUpdateTime
                    ?: 0

        return Pair(sdkVersion, lastUpdate.convertTimestampToDate())
    }

    fun updateAll() = runBlocking {
        showProgressRelay.accept(true)

        AppManager.getPackagesWithUserPrefs()
            // this condition will only happen when app there is no app installed
            // which means PROBABLY the app is being ran on emulator.
            .also {
                if (it.isEmpty()) Injector.get().showSystemApps().set(true)
            }
            .forEach { packageInfo ->
                AppManager.insertNewApp(packageInfo)
                AppManager.insertNewVersion(packageInfo)
            }

        showProgressRelay.accept(false)
    }


    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: MainState): MainViewModel
    }

    companion object : MvRxViewModelFactory<MainViewModel, MainState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: MainState
        ): MainViewModel? {
            val fragment: MainFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.mainViewModelFactory.create(state)
        }
    }
}
