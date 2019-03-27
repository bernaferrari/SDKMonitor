package com.bernaferrari.sdkmonitor.details

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.bernaferrari.base.mvrx.MvRxViewModel
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import com.bernaferrari.sdkmonitor.main.AppDetails
import com.bernaferrari.sdkmonitor.main.MainState
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailsViewModel @AssistedInject constructor(
    @Assisted initialState: MainState,
    private val mVersionsDao: VersionsDao
) : MvRxViewModel<MainState>(initialState) {

    suspend fun fetchAllVersions(packageName: String): List<Version>? =
        withContext(Dispatchers.IO) { mVersionsDao.getAllValues(packageName) }

    fun fetchAppDetails(packageName: String): MutableList<AppDetails> {

        val packageInfo = AppManager.getPackageInfo(packageName) ?: return mutableListOf()

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
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: MainState): DetailsViewModel
    }

    companion object : MvRxViewModelFactory<DetailsViewModel, MainState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: MainState
        ): DetailsViewModel? {
            val fragment: DetailsDialog = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.detailsViewModelFactory.create(state)
        }
    }
}
