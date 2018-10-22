package com.bernaferrari.sdkmonitor.main

import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.core.MvRxViewModel
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.extensions.consume
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.extensions.normalizeString
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

    private val mAppsDao = Injector.get().appsDao()
    private val mVersionsDao = Injector.get().versionsDao()

    val itemsList = mutableListOf<AppVersion>()
    var hasLoaded = false
    var maxListSize = 0
    val relay: BehaviorRelay<String> = BehaviorRelay.create<String>()

    init {
        fetchData()
    }

    private fun fetchData() = withState { _ ->
        Observables.combineLatest(
            getAppsListObservable(),
            relay
        ) { list, filter ->
            list.takeIf { it.isNotEmpty() && filter.isNotBlank() }
                ?.filter { it.app.title.normalizeString().contains(filter.normalizeString()) }
                    ?: list
        }.doOnNext {
            itemsList.clear()
            itemsList.addAll(it)
        }.execute {
            copy(
                state = if (it.complete) State.SUCCESS else State.LOADING,
                listOfItems = it.invoke() ?: emptyList()
            )
        }
    }

    fun fetchData2(packageName: String): MutableList<AppDetails> {

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

            this += AppDetails(
                "First Install",
                packageInfo.firstInstallTime.convertTimestampToDate()
            )
        }
    }

    private fun getAppsListObservable(): Observable<List<AppVersion>> =

        mAppsDao.getAppsList().toObservable()
            .skipWhile { if (it.isEmpty()) consume { updateAll() } else false }
            .debounce { list ->
                // debounce with a 200ms delay all items except the first one
                val flow = Observable.just(list)
                hasLoaded = true
                flow.takeIf { list.isEmpty() }
                    ?.let { it } ?: flow.delay(200, TimeUnit.MILLISECONDS)
            }
            .map { list ->
                val allItems = mutableListOf<AppVersion>()
                list.asSequence()
                    .sortedBy { it.title.toLowerCase() }
                    .mapTo(allItems) { app ->
                        val (sdkVersion, lastUpdate) = getSdkDate(app)
                        AppVersion(
                            app,
                            sdkVersion,
                            lastUpdate
                        )
                    }.toList()
            }.doOnNext { maxListSize = it.size }

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

    private fun updateAll() = GlobalScope.launch(Dispatchers.Default) {
            AppManager.getPlayStorePackages()
                // this condition will only happen when app is run on emulator.
                .let { if (it.isEmpty()) AppManager.getPackages() else it }
                .forEach {

                    val icon = AppManager.getAppIcon(it).toBitmap()
                    val backgroundColor =
                        getPaletteColor(Palette.from(icon).generate(), 0)

                    // apps like Story Saver have a ' ' before the app name for no reason.
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

                    println("checkIfExists $label: " + mVersionsDao.checkIfExists(versionCode))

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