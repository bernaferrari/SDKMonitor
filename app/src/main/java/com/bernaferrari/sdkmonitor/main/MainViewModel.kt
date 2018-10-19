package com.bernaferrari.sdkmonitor.main

import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.util.AppManager
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Exposes the data to be used in the site diff screen.
 */
class MainViewModel(
    private val mAppsDao: AppsDao,
    private val mVersionsDao: VersionsDao
) : ViewModel(), CoroutineScope {

    val itemsList = mutableListOf<AppVersion>()
    val allItems = mutableListOf<AppVersion>()

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    override fun onCleared() {
        coroutineContext.cancel()
        super.onCleared()
    }

    val appsList: Flowable<List<App>> = mAppsDao.getAppsList()

    fun getSdkDate(app: App): Pair<Int, String> {

        // since insertApp is called before insertVersion, mVersionsDao.getValue(...) will
        // return null on app's first run. This will avoid the situation.
        val version = mVersionsDao.getValue(app.packageName)

        val sdkVersion =
            version?.targetSdk ?: AppManager.getApplicationInfo(app.packageName).targetSdkVersion

        val lastUpdate =
            version?.lastUpdateTime ?: AppManager.getPackageInfo(app.packageName).lastUpdateTime

        return Pair(sdkVersion, lastUpdate.convertTimestampToDate())
    }

    fun updateAll() = launch {
        AppManager.getPlayStorePackages()
            // this will only occur if person runs app on emulator.
            .let { if (it.isEmpty()) AppManager.getPackages() else it }
            .forEach {

                // apps like Story Saver have a ' ' before the app name for no reason.
                val icon = AppManager.getAppIcon(it).toBitmap()
                val backgroundColor = getPaletteColor(Palette.from(icon).generate(), 0)
                val label = AppManager.getAppLabel(it).trim()

                val app = App(
                    packageName = it.packageName,
                    title = label,
                    backgroundColor = backgroundColor,
                    firstInstallTime = it.firstInstallTime,
                    isFromPlayStore = AppManager.isAppFromGooglePlay(it.packageName)
                )

                mAppsDao.insertApp(app)

                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}
