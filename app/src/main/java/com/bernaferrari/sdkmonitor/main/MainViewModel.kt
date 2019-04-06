package com.bernaferrari.sdkmonitor.main

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.bernaferrari.base.mvrx.MvRxViewModel
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.normalizeString
import com.jakewharton.rxrelay2.BehaviorRelay
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.util.concurrent.TimeUnit

class MainViewModel @AssistedInject constructor(
    @Assisted initialState: MainState,
    private val mainRepository: MainDataSource
) : MvRxViewModel<MainState>(initialState) {

    val itemsList = mutableListOf<AppVersion>()
    var hasLoaded = false
    var maxListSize: BehaviorRelay<Int> = BehaviorRelay.create<Int>()
    val inputRelay: BehaviorRelay<String> = BehaviorRelay.create<String>()

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
            list.takeIf { filter.isNotBlank() }
                ?.filter { filter.normalizeString() in it.app.title.normalizeString() }
                    ?: list
        }.doOnNext {
            itemsList.clear()
            itemsList.addAll(it)
        }.execute {
            copy(listOfItems = it)
        }
    }

    private fun allApps() = mainRepository.shouldOrderBySdk().switchMap { orderBySdk ->
        mainRepository.getAppsList()
            .getAppsListObservable(orderBySdk)
    }

    private fun Observable<List<App>>.getAppsListObservable(orderBySdk: Boolean): Observable<List<AppVersion>> =
        this.debounce { list ->
            // debounce with a 200ms delay on all items except the first one
            val flow = Observable.just(list)
            hasLoaded = true
            if (list.isEmpty()) flow else flow.delay(250, TimeUnit.MILLISECONDS)
        }.skipWhile {
            // force the refresh when app is first opened or no known apps are installed (emulator)
            if (it.isEmpty() || AppManager.firstRun) {
                AppManager.firstRun = false
                refreshAll()
            }
            it.isEmpty()
        }.map { list ->
            // parse correctly the values
            list.map { app -> mainRepository.mapSdkDate(app) }
        }.map { list ->
            // list already comes sorted by name from db, it is faster and avoids sub-querying
            if (orderBySdk) list.sortedBy { it.sdkVersion } else list
        }.doOnNext { maxListSize.accept(it.size) }

    private fun refreshAll() {
        AppManager.getPackagesWithUserPrefs()
            // this condition will only happen when app there is no app installed
            // which means PROBABLY the app is being ran on emulator.
            .also {
                if (it.isEmpty()) mainRepository.setShouldShowSystemApps(true)
            }
            .forEach { packageInfo ->
                AppManager.insertNewApp(packageInfo)
                AppManager.insertNewVersion(packageInfo)
            }
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
