package com.bernaferrari.sdkmonitor.ui.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel
    @Inject
    constructor(
        private val appsRepository: AppsRepository,
        private val appManager: AppManager,
        @param:ApplicationContext private val context: Context,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
        val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

        /**
         * Loads details for a specific app by its package name
         */
        fun loadAppDetails(packageName: String) {
            viewModelScope.launch {
                try {
                    // Set loading state
                    _uiState.value = DetailsUiState.Loading

                    // Get app from repository
                    val app = appsRepository.getApp(packageName)

                    // If app exists, fetch its versions and create AppDetails
                    if (app != null) {
                        val versions = appsRepository.getAppVersions(packageName)

                        // Use appManager to get complete AppDetails
                        val appDetails = appManager.getAppDetails(packageName)
                        val appVersionList =
                            versions.map {
                                it.toAppVersion(
                                    appDetails,
                                    context,
                                )
                            } // Changed from toVersionInfo to toAppVersion

                        // Update UI state with success
                        _uiState.value =
                            DetailsUiState.Success(
                                appDetails = appDetails,
                                versions = appVersionList, // Changed from versionInfoList to appVersionList
                            )
                    } else {
                        // If app doesn't exist in database, try to get it from package manager
                        val packageInfo = appManager.getPackageInfo(packageName)
                        if (packageInfo != null) {
                            val appDetails = appManager.getAppDetails(packageName)

                            // Update UI state with success but empty version history
                            _uiState.value =
                                DetailsUiState.Success(
                                    appDetails = appDetails,
                                    versions = emptyList(),
                                )
                        } else {
                            // App not found anywhere
                            _uiState.value =
                                DetailsUiState.Error(context.getString(R.string.app_not_found))
                        }
                    }
                } catch (e: Exception) {
                    // Handle any errors
                    _uiState.value =
                        DetailsUiState.Error(e.message ?: context.getString(R.string.unknown_error))
                }
            }
        }

        /**
         * Refreshes the app details
         */
        fun refreshDetails(packageName: String) {
            loadAppDetails(packageName)
        }
    }
