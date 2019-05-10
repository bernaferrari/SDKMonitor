package com.bernaferrari.sdkmonitor.settings

import com.airbnb.mvrx.*
import com.bernaferrari.base.mvrx.MvRxViewModel
import com.bernaferrari.sdkmonitor.Injector
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables

data class SettingsData(
    val lightMode: Boolean,
    val showSystemApps: Boolean,
    val backgroundSync: Boolean,
    val orderBySdk: Boolean
) : MvRxState

data class SettingsState(
    val data: Async<SettingsData> = Loading()
) : MvRxState

class SettingsViewModel @AssistedInject constructor(
    @Assisted initialState: SettingsState,
    @Assisted private val sources: Observable<SettingsData>
) : MvRxViewModel<SettingsState>(initialState) {

    init {
        fetchData()
    }

    private fun fetchData() = withState {
        sources.execute { copy(data = it) }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            initialState: SettingsState,
            sources: Observable<SettingsData>
        ): SettingsViewModel
    }

    companion object : MvRxViewModelFactory<SettingsViewModel, SettingsState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: SettingsState
        ): SettingsViewModel? {

            val source = Observables.combineLatest(
                Injector.get().isLightTheme().observe(),
                Injector.get().showSystemApps().observe(),
                Injector.get().backgroundSync().observe(),
                Injector.get().orderBySdk().observe()
            ) { dark, system, backgroundSync, orderBySdk ->
                SettingsData(dark, system, backgroundSync, orderBySdk)
            }

            val fragment: SettingsFragment =
                (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.settingsViewModelFactory.create(state, source)
        }
    }
}
