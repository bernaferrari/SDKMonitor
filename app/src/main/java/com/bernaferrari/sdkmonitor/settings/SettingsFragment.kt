package com.bernaferrari.sdkmonitor.settings

import android.os.Bundle
import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.activityViewModel
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.SettingsSwitchBindingModel_
import com.bernaferrari.sdkmonitor.core.simpleController
import com.bernaferrari.sdkmonitor.logs.RecyclerBaseFragment
import com.bernaferrari.sdkmonitor.marquee
import com.bernaferrari.sdkmonitor.views.loadingRow
import kotlinx.android.synthetic.main.recyclerview.*

class SettingsFragment : RecyclerBaseFragment() {

    private val viewModel: SettingsRxViewModel by activityViewModel()

    override fun epoxyController(): EpoxyController = simpleController(viewModel) { state ->

        marquee {
            id("header")
            title("Settings")
        }

        if (state.data is Loading) {
            loadingRow { id("loading") }
        }

        if (state.data.complete) {

            val lightMode = state.data()?.lightMode ?: true

            SettingsSwitchBindingModel_()
                .id("light mode")
                .title("Light mode")
                .icon(R.drawable.ic_sunny)
                .subtitle("Change how the app looks")
                .switchIsVisible(true)
                .switchIsOn(lightMode)
                .clickListener { v ->
                    viewModel.lightMode.set(!lightMode)
                    activity?.recreate()
                }
                .addTo(this)

            val colorBySdk = state.data()?.colorBySdk ?: true

            SettingsSwitchBindingModel_()
                .id("color mode")
                .title("Color by targetSDK")
                .icon(R.drawable.ic_color)
                .subtitle(if (colorBySdk) "Color will range from green (recent sdk) to red (old)." else "Color will match the icon's palette.")
                .switchIsVisible(true)
                .switchIsOn(colorBySdk)
                .clickListener { v ->
                    viewModel.colorBySdk.set(!colorBySdk)
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
                    viewModel.showSystemApps.set(!showSystemApps)
                }
                .addTo(this)

//            SettingsSwitchBindingModel_()
//                .id("background sync")
//                .title("Background Sync")
//                .icon(R.drawable.ic_sync)
//                .subtitle("Disabled")
//                .clickListener { v ->
//                    viewModel.showSystemApps.set(!showSystemApps)
//                }
//                .addTo(this)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decoration = InsetDecoration(1, 0, 0x40FFFFFF)
        recycler.addItemDecoration(decoration)

//        updating += DialogItemSwitch(
//            "Debug mode",
//            IconicsDrawable(context, CommunityMaterial.Icon.cmd_bug).color(color),
//            sharedPrefs.getBoolean("debug", true)
//        ) {
//            sharedPrefs.edit { putBoolean("debug", it.isSwitchOn) }
//        }
//
//        updating += com.bernaferrari.sdkmonitor.settings.DialogItemSwitch(
//            getString(R.string.background_sync),
//            IconicsDrawable(context, GoogleMaterial.Icon.gmd_sync).color(color),
//            sharedPrefs.getBoolean("backgroundSync", false)
//        ) {
//            sharedPrefs.edit { putBoolean("backgroundSync", it.isSwitchOn) }
//
//            if (it.isSwitchOn) {
//                syncSection.update(syncSettings)
//                WorkerHelper.updateWorkerWithConstraints(sharedPrefs)
//            } else {
//                syncSection.update(mutableListOf())
//                WorkerHelper.cancelWork()
//            }
//        }
//
//        syncSettings += com.bernaferrari.sdkmonitor.settings.DialogItemInterval(
//            getString(R.string.sync_interval),
//            sharedPrefs.getLong(WorkerHelper.DELAY, 1440).toInt()
//        ) {
//            sharedPrefs.edit { putLong(WorkerHelper.DELAY, it) }
//            WorkerHelper.updateWorkerWithConstraints(sharedPrefs)
//            Logger.d("Reloaded! $it min")
//        }
//
//        syncSettings += com.bernaferrari.sdkmonitor.settings.DialogItemSeparator(getString(R.string.constraints))
//
//        syncSettings += com.bernaferrari.sdkmonitor.settings.DialogItemSwitch(
//            getString(R.string.charging),
//            IconicsDrawable(context, CommunityMaterial.Icon.cmd_battery_charging)
//                .color(color),
//            sharedPrefs.getBoolean(WorkerHelper.CHARGING, false)
//        ) {
//            updateSharedPreferences(WorkerHelper.CHARGING, it.isSwitchOn)
//        }
//
//        syncSettings += com.bernaferrari.sdkmonitor.settings.DialogItemSwitch(
//            getString(R.string.batter_not_low),
//            IconicsDrawable(context, CommunityMaterial.Icon.cmd_battery_20).color(color),
//            sharedPrefs.getBoolean(WorkerHelper.BATTERYNOTLOW, false)
//        ) {
//            updateSharedPreferences(WorkerHelper.BATTERYNOTLOW, it.isSwitchOn)
//        }
//
//        if (sharedPrefs.getBoolean("backgroundSync", false)) {
//            syncSection.update(syncSettings)
//        }
    }
}
