package com.bernaferrari.sdkmonitor.settings

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.core.MvRxViewModel
import io.reactivex.rxkotlin.Observables

data class SettingsData(
    val lightMode: Boolean,
    val colorBySdk: Boolean,
    val showSystemApps: Boolean,
    val backgroundSync: Boolean
) : MvRxState

data class SettingsState(val data: Async<SettingsData> = Loading()) : MvRxState

class SettingsRxViewModel(initialState: SettingsState) :
    MvRxViewModel<SettingsState>(initialState) {

    init {
        fetchData()
    }

    private fun fetchData() = withState {

        Observables.combineLatest(
            Injector.get().isLightTheme().observe(),
            Injector.get().isColorBySdk().observe(),
            Injector.get().observeShowSystemApps(),
            Injector.get().backgroundSync().observe()
        ) { dark, color, system, backgroundSync ->
            SettingsData(dark, color, system, backgroundSync)
        }.execute {
            copy(data = it)
        }
    }
}
