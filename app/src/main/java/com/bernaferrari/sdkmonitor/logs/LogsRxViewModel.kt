package com.bernaferrari.sdkmonitor.logs

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.bernaferrari.base.mvrx.MvRxViewModel
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import com.bernaferrari.sdkmonitor.main.MainState
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * initialState *must* be implemented as a constructor parameter.
 */
class LogsRxViewModel @AssistedInject constructor(
    @Assisted initialState: MainState,
    private val mVersionsDao: VersionsDao,
    private val mAppsDao: AppsDao
) : MvRxViewModel<MainState>(initialState) {

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

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: MainState): LogsRxViewModel
    }

    companion object : MvRxViewModelFactory<LogsRxViewModel, MainState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: MainState
        ): LogsRxViewModel? {
            val fragment: LogsFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.logsViewModelFactory.create(state)
        }
    }
}
