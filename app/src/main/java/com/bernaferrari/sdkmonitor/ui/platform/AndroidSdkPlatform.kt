package com.bernaferrari.sdkmonitor.ui.platform

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.core.AppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

/** Localized strings where resources exist; English defaults otherwise. */
@Composable
fun rememberAndroidSdkStrings(): SdkStrings =
    DefaultSdkStrings.copy(
        apps = stringResource(R.string.main_title),
        logs = stringResource(R.string.logs),
        settings = stringResource(R.string.settings),
        about = stringResource(R.string.about),
        searchApps = stringResource(R.string.search_apps_hint),
        clear = stringResource(R.string.clear_search),
        retry = stringResource(R.string.retry),
        loading = stringResource(R.string.loading_apps),
        unknownError = stringResource(R.string.unknown_error),
        appNotFound = stringResource(R.string.app_not_found),
        allApps = stringResource(R.string.all_apps),
        userApps = stringResource(R.string.user_apps),
        systemApps = stringResource(R.string.system_apps),
        sortByName = stringResource(R.string.sort_by_name),
        sortBySdk = stringResource(R.string.sort_by_sdk),
        noAppsFound = stringResource(R.string.no_apps_found),
        noLogsYet = stringResource(R.string.no_logs_yet),
        targetSdk = stringResource(R.string.target_sdk),
        minSdk = stringResource(R.string.min_sdk),
        versionHistory = stringResource(R.string.version_history),
        themeSystem = stringResource(R.string.theme_system),
        themeSystemDescription = stringResource(R.string.theme_system_description),
        themeMaterialYou = stringResource(R.string.theme_material_you),
        themeMaterialYouDescription = stringResource(R.string.theme_material_you_description),
        themeLight = stringResource(R.string.theme_light),
        themeLightDescription = stringResource(R.string.theme_light_description),
        themeDark = stringResource(R.string.theme_dark),
        themeDarkDescription = stringResource(R.string.theme_dark_description),
        backgroundSync = stringResource(R.string.background_sync),
        backgroundSyncDescription = stringResource(R.string.background_sync_description),
        cancel = stringResource(R.string.cancel),
        save = stringResource(R.string.save),
        justNow = stringResource(R.string.just_now),
        exportData = stringResource(R.string.export_data),
        exportDataDescription = stringResource(R.string.export_data_description),
        privacy = stringResource(R.string.privacy_first_title),
        privacyBody = stringResource(R.string.no_data_collection),
        viewOnGitHub = stringResource(R.string.view_on_github),
        getInTouch = stringResource(R.string.get_in_touch),
        contactDescription = stringResource(R.string.send_feedback_or_ask_questions),
        noDataCollection = stringResource(R.string.no_data_collection),
        contact = stringResource(R.string.contact),
        appName = stringResource(R.string.app_name),
        appInformation = stringResource(R.string.app_information),
        playStore = stringResource(R.string.play_store),
        versionLabel = stringResource(R.string.version_label),
        updatedLabel = stringResource(R.string.updated_label),
        sizeLabel = stringResource(R.string.size_label),
        settingsTitle = stringResource(R.string.settings_title),
        failedToLoadLogs = stringResource(R.string.failed_to_load_logs),
    )

/**
 * App icons via [AppManager] cache + Coil — single source for list/details/logs.
 */
class AndroidAppIconProvider : AppIconProvider {
    @Composable
    override fun AppIcon(
        packageName: String,
        size: Dp,
        modifier: Modifier,
    ) {
        val context = LocalContext.current
        val appManager: AppManager = koinInject()
        val icon by produceState<Any?>(initialValue = null, packageName) {
            value =
                withContext(Dispatchers.IO) {
                    appManager.getAppIconCached(packageName) ?: packageName
                }
        }

        AsyncImage(
            model =
                ImageRequest
                    .Builder(context)
                    .data(icon)
                    .crossfade(true)
                    .build(),
            contentDescription = null,
            modifier = modifier.size(size),
        )
    }
}
