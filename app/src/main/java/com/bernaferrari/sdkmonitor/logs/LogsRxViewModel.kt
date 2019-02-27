package com.bernaferrari.sdkmonitor.logs

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.core.MvRxViewModel
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.main.MainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * initialState *must* be implemented as a constructor parameter.
 */
class LogsRxViewModel(initialState: MainState) : MvRxViewModel<MainState>(initialState) {

    private val mAppsDao = Injector.get().appsDao()
    private val mVersionsDao = Injector.get().versionsDao()

    suspend fun getAppList(): Map<String, App> = withContext(Dispatchers.IO) {
        mutableMapOf<String, App>().apply {
            mAppsDao.getAppsList().forEach {
                this[it.packageName] = it
            }
        }
    }

    suspend fun getVersionCount(): Int = withContext(Dispatchers.IO) {
        mVersionsDao.countNumberOfChanges()
    }

    fun pagedVersion(): LiveData<PagedList<Version>> {

        val myPagingConfig = PagedList.Config.Builder()
            .setPageSize(20)
            .setPrefetchDistance(60)
            .setEnablePlaceholders(true)
            .build()

        return LivePagedListBuilder<Int, Version>(
            mVersionsDao.getVersionsPaged(),
            myPagingConfig
        ).build()
    }
}
