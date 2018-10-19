package com.bernaferrari.sdkmonitor.main

import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.core.MvRxViewModel
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.extensions.normalizeString
import com.bernaferrari.sdkmonitor.extensions.toDpF
import com.bernaferrari.sdkmonitor.util.AppManager
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

/**
 * initialState *must* be implemented as a constructor parameter.
 */
class MainRxViewModel(initialState: MainState) : MvRxViewModel<MainState>(initialState) {

    val mAppsDao = Injector.get().appsDao()
    val mVersionsDao = Injector.get().versionsDao()

    var cornerRadius: Float = 8.toDpF(Injector.get().appContext().resources)

    init {
        fetchNextPage()
    }

    fun fetchNextPage() = withState { state ->

        Observables.combineLatest(
            getObservableList(),
            relay.doOnEach {
                println("relayoneach: ${it.value}")
            }
        ) { list, filter ->
            list.takeIf { it.isNotEmpty() && filter.isNotBlank() }
                ?.filter { it.app.title.normalizeString().contains(filter.normalizeString()) }
                    ?: list
        }.doOnNext {
            itemsList.clear()
            itemsList.addAll(it)
        }.execute { copy(listOfItems = it.invoke() ?: emptyList()) }
    }

    val itemsList = mutableListOf<AppVersion>()
    val allItems = mutableListOf<AppVersion>()
    val relay = BehaviorRelay.create<String>()

    private fun getObservableList(): Observable<List<AppVersion>> =
        mAppsDao.getAppsList().toObservable()
            .doOnNext { if (it.isEmpty()) updateAll() }
//            .skipWhile { it.isEmpty() }
            .debounce { list ->
                // debounce with a 200ms delay all items except the first one
                val flow = Observable.just(list)

                flow.takeIf { allItems.isEmpty() && list.isEmpty() }
                    ?.let { it } ?: flow.delay(200, TimeUnit.MILLISECONDS)
            }
            .map { list ->
                list.also { allItems.clear() }
                    .asSequence()
                    .sortedBy { it.title.toLowerCase() }
                    .mapTo(allItems) { app ->
                        val (sdkVersion, lastUpdate) = getSdkDate(app)
                        AppVersion(
                            app,
                            sdkVersion,
                            lastUpdate,
                            cornerRadius
                        )
                    }.toList()
            }


    fun getSdkDate(app: App): Pair<Int, String> {

        // since insertApp is called before insertVersion, mVersionsDao.getValue(...) will
        // return null on app's first run. This will avoid the situation.
        val version = mVersionsDao.getValue(app.packageName)

        val sdkVersion =
            version?.targetSdk ?: AppManager.getApplicationInfo(app.packageName).targetSdkVersion

        val lastUpdate =
            version?.lastUpdateTime ?: AppManager.getPackageInfo(
                app.packageName
            ).lastUpdateTime

        return Pair(sdkVersion, lastUpdate.convertTimestampToDate())
    }

    private fun updateAll() =
        GlobalScope.launch(Dispatchers.Default) {
            AppManager.getPlayStorePackages()
                // this will only occur if person runs app on emulator.
                .let { if (it.isEmpty()) AppManager.getPackages() else it }
                .forEach {

                    // apps like Story Saver have a ' ' before the app name for no reason.
                    val icon = AppManager.getAppIcon(it).toBitmap()
                    val backgroundColor =
                        getPaletteColor(Palette.from(icon).generate(), 0)
                    val label = AppManager.getAppLabel(it).trim()

                    val app = App(
                        packageName = it.packageName,
                        title = label,
                        backgroundColor = backgroundColor,
                        firstInstallTime = it.firstInstallTime,
                        isFromPlayStore = AppManager.isAppFromGooglePlay(
                            it.packageName
                        )
                    )

                    mAppsDao.insertApp(app)

                    val versionCode =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            it.longVersionCode
                        } else {
                            it.versionCode.toLong()
                        }

                    println("checkIfExists ${label}: " + mVersionsDao.checkIfExists(versionCode))

                    mVersionsDao.insertVersion(
                        Version(
                            version = versionCode,
                            packageName = it.packageName,
                            versionName = it.versionName,
                            lastUpdateTime = it.lastUpdateTime,
                            targetSdk = it.applicationInfo.targetSdkVersion//,
                            //                       className = it.applicationInfo.className ?: "",
                            //                       sourceDir = it.applicationInfo.sourceDir ?: "",
                            //                       dataDir = it.applicationInfo.dataDir ?: ""
                        )
                    )
                }
        }

    private fun getPaletteColor(palette: Palette?, defaultColor: Int) = when {
        palette?.darkVibrantSwatch != null -> palette.getDarkVibrantColor(defaultColor)
        palette?.vibrantSwatch != null -> palette.getVibrantColor(defaultColor)
        palette?.mutedSwatch != null -> palette.getMutedColor(defaultColor)
        palette?.darkMutedSwatch != null -> palette.getDarkMutedColor(defaultColor)
        palette?.lightMutedSwatch != null -> palette.getMutedColor(defaultColor).darken
        palette?.lightVibrantSwatch != null -> palette.getLightVibrantColor(defaultColor).darken
        else -> defaultColor
    }

}