package com.bernaferrari.sdkmonitor.settings

import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.fragmentViewModel
import com.bernaferrari.base.mvrx.simpleController
import com.bernaferrari.sdkmonitor.*
import com.bernaferrari.sdkmonitor.core.AboutDialog
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.ui.dagger.DaggerBaseRecyclerFragment
import javax.inject.Inject

class SettingsFragment : DaggerBaseRecyclerFragment() {

    private val viewModel: SettingsViewModel by fragmentViewModel()
    @Inject
    lateinit var settingsViewModelFactory: SettingsViewModel.Factory

    override fun epoxyController(): EpoxyController = simpleController(viewModel) { state ->

        println("state is: ${state.data}")
        if (state.data is Loading) {
            loadingRow { id("loading") }
        }

        if (state.data.complete) {

            marquee {
                id("header")
                title("Settings")
                subtitle("Version ${BuildConfig.VERSION_NAME}")
            }

            val lightMode = state.data()?.lightMode ?: true

            SettingsSwitchBindingModel_()
                .id("light mode")
                .title("Light mode")
                .icon(R.drawable.ic_sunny)
                .switchIsVisible(true)
                .switchIsOn(lightMode)
                .clickListener { v ->
                    Injector.get().isLightTheme().set(!lightMode)
                    activity?.recreate()
                }
                .addTo(this)

            val showSystemApps = state.data()?.showSystemApps ?: true

            SettingsSwitchBindingModel_()
                .id("system apps")
                .title("Show system apps")
                .icon(R.drawable.ic_android)
                .switchIsVisible(true)
                .switchIsOn(showSystemApps)
                .subtitle("Show all installed apps. This might increase loading time.")
                .clickListener { v ->
                    Injector.get().showSystemApps().set(!showSystemApps)
                    AppManager.forceRefresh = true
                }
                .addTo(this)

            val orderBySdk = state.data()?.orderBySdk ?: true

            SettingsSwitchBindingModel_()
                .id("order by")
                .title("Order by targetSDK")
                .icon(R.drawable.ic_sort)
                .subtitle("Change the order of items")
                .switchIsVisible(true)
                .switchIsOn(orderBySdk)
                .clickListener { v ->
                    Injector.get().orderBySdk().set(!orderBySdk)
                }
                .addTo(this)

            val backgroundSync = state.data()?.backgroundSync ?: false

            SettingsSwitchBindingModel_()
                .id("background sync")
                .title("Background Sync")
                .icon(R.drawable.ic_sync)
                .subtitle(if (backgroundSync) "Enabled" else "Disabled")
                .clickListener { v ->
                    DialogBackgroundSync.show(requireActivity())
                }
                .addTo(this)

            SettingsSwitchBindingModel_()
                .id("about")
                .title("About")
                .icon(R.drawable.ic_info)
                .clickListener { v ->
                    AboutDialog.show(requireActivity())
                }
                .addTo(this)
        }
    }
}
