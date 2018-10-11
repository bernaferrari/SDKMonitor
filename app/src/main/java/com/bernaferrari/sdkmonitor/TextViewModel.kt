package com.bernaferrari.sdkmonitor

import android.arch.lifecycle.ViewModel
import android.support.v7.graphics.Palette
import androidx.core.graphics.drawable.toBitmap
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.extensions.darken
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Exposes the data to be used in the site diff screen.
 */
class TextViewModel(private val mSnapsDao: AppsDao) : ViewModel(), CoroutineScope {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    val concertList: Flowable<List<App>> = mSnapsDao.concertsByDate()

    fun updateAll() = launch {
        AppManager.getPlayStorePackages().forEach {

            val icon = AppManager.getAppIcon(it).toBitmap()
            val backgroundColor = getPaletteColor(Palette.from(icon).generate(), 0)

            mSnapsDao.insertApp(App(it.packageName, AppManager.getAppLabel(it), backgroundColor))
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
