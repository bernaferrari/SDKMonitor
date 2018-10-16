package com.bernaferrari.sdkmonitor

import android.arch.lifecycle.ViewModel
import android.support.v7.graphics.Palette
import androidx.core.graphics.drawable.toBitmap
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import com.bernaferrari.sdkmonitor.extensions.darken
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Exposes the data to be used in the site diff screen.
 */
class TextViewModel(
    private val mAppsDao: AppsDao,
    private val mVersionsDao: VersionsDao
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    override fun onCleared() {
        coroutineContext.cancel()
        super.onCleared()
    }

    val appsList: Flowable<List<App>> = mAppsDao.concertsByDate()

    fun updateAll() = launch {
        AppManager.getPlayStorePackages()
            // this will only occur if person runs app on emulator.
            .let { if (it.isEmpty()) AppManager.getPackages() else it }
            .forEach {

                // apps like Story Saver have a ' ' before the app name for no reason.
                val icon = AppManager.getAppIcon(it).toBitmap()
                val backgroundColor = getPaletteColor(Palette.from(icon).generate(), 0)

                val app = App(
                    it.packageName,
                    AppManager.getAppLabel(it).trim(),
                    backgroundColor,
                    it.firstInstallTime
                )

                mAppsDao.insertApp(app)

//            try {
//                println("rawrrr -> $it")
////                it.longVersionCode
//            } catch (e: Exception) {
//                println(e.localizedMessage)
//            }

//            mVersionsDao.checkIfExists(it.longVersionCode)
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
