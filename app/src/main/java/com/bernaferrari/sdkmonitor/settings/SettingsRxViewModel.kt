package com.bernaferrari.sdkmonitor.settings

import com.afollestad.rxkprefs.rxkPrefs
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.core.MvRxViewModel
import io.reactivex.rxkotlin.Observables

data class SettingsData(
    val lightMode: Boolean,
    val colorBySdk: Boolean,
    val showSystemApps: Boolean
) : MvRxState

data class SettingsState(val data: Async<SettingsData> = Loading()) : MvRxState

class SettingsRxViewModel(initialState: SettingsState) :
    MvRxViewModel<SettingsState>(initialState) {

    private val myPrefs = rxkPrefs(Injector.get().sharedPrefs())

    val lightMode = myPrefs.boolean("light mode", true)

    val colorBySdk = myPrefs.boolean("colorBySdk", true)

    val showSystemApps = myPrefs.boolean("system apps", false)

    init {
        fetchData()
    }

    private fun fetchData() = withState {

        Observables.combineLatest(
            lightMode.observe(),
            colorBySdk.observe(),
            showSystemApps.observe()
        ) { dark, color, system ->
            SettingsData(dark, color, system)
        }.execute {
            copy(data = it)
        }
    }

}
