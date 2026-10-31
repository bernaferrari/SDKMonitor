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
        aboutSection = stringResource(R.string.about_section),
        allowNotifications = stringResource(R.string.allow_notifications),
        analytics = stringResource(R.string.analytics),
        analyticsEmpty = stringResource(R.string.analytics_empty),
        analyticsSection = stringResource(R.string.analytics_section),
        appFilter = stringResource(R.string.app_filter),
        appearance = stringResource(R.string.appearance),
        // The resource is "%d apps"; the shared UI writes the count itself, so it takes the word alone.
        appsCount = stringResource(R.string.apps_count).replace("%d", "").trim(),
        clearLogs = stringResource(R.string.clear_logs),
        colorStyle = stringResource(R.string.color_style),
        custom = stringResource(R.string.custom),
        daily = stringResource(R.string.daily),
        days = stringResource(R.string.days),
        enableSync = stringResource(R.string.enable_sync),
        enabledDaily = stringResource(R.string.enabled_daily),
        enabledEvery = stringResource(R.string.enabled_every),
        enabledMonthly = stringResource(R.string.enabled_monthly),
        enabledWeekly = stringResource(R.string.enabled_weekly),
        error = stringResource(R.string.error),
        errorLoadingSettings = stringResource(R.string.error_loading_settings),
        failedToLoadApps = stringResource(R.string.failed_to_load_apps),
        hours = stringResource(R.string.hours),
        interval = stringResource(R.string.interval),
        lastUpdate = stringResource(R.string.last_update),
        latest = stringResource(R.string.latest),
        learnMoreAboutApp = stringResource(R.string.learn_more_about_app),
        loadingSettings = stringResource(R.string.loading_settings),
        minutes = stringResource(R.string.minutes),
        monthly = stringResource(R.string.monthly),
        noAnalyticsData = stringResource(R.string.no_analytics_data),
        noAnalyticsSubtitle = stringResource(R.string.no_analytics_subtitle),
        noAppsSubtitle = stringResource(R.string.no_apps_subtitle),
        noLogsSubtitle = stringResource(R.string.no_logs_subtitle),
        notificationPermissionBody = stringResource(R.string.notification_permission_body),
        notificationPermissionTitle = stringResource(R.string.notification_permission_title),
        notificationWarningBody = stringResource(R.string.notification_warning_body),
        notificationWarningTitle = stringResource(R.string.notification_warning_title),
        notificationsRequired = stringResource(R.string.notifications_required),
        openSettings = stringResource(R.string.open_settings),
        openSource = stringResource(R.string.open_source),
        packageName = stringResource(R.string.package_name),
        permissions = stringResource(R.string.permissions),
        pluralDays = stringResource(R.string.plural_days),
        pluralHours = stringResource(R.string.plural_hours),
        pluralMinutes = stringResource(R.string.plural_minutes),
        rateApp = stringResource(R.string.rate_app),
        refresh = stringResource(R.string.refresh),
        search = stringResource(R.string.search),
        singularDay = stringResource(R.string.singular_day),
        singularHour = stringResource(R.string.singular_hour),
        singularMinute = stringResource(R.string.singular_minute),
        size = stringResource(R.string.size),
        styleCmf = stringResource(R.string.style_cmf),
        styleExpressive = stringResource(R.string.style_expressive),
        styleTonal = stringResource(R.string.style_tonal),
        styleVibrant = stringResource(R.string.style_vibrant),
        syncDialogTitle = stringResource(R.string.sync_dialog_title),
        syncInterval = stringResource(R.string.sync_interval),
        syncing = stringResource(R.string.syncing),
        systemApp = stringResource(R.string.system_app),
        tapToConfigureSync = stringResource(R.string.tap_to_configure_sync),
        theme = stringResource(R.string.theme),
        userApp = stringResource(R.string.user_app),
        version = stringResource(R.string.version),
        weekly = stringResource(R.string.weekly),
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
        sortBy = stringResource(R.string.sort_by),
        settingUpYourApps = stringResource(R.string.setting_up_your_apps),
        loadingApps = stringResource(R.string.loading_apps),
        somethingWentWrong = stringResource(R.string.something_went_wrong),
        noAppsFound = stringResource(R.string.no_apps_found),
        noLogsYet = stringResource(R.string.no_logs_yet),
        changeLogs = stringResource(R.string.change_logs),
        loadingChangeHistory = stringResource(R.string.loading_change_history),
        analyzingAppEcosystem = stringResource(R.string.analyzing_app_ecosystem),
        oopsSomethingWentWrong = stringResource(R.string.oops_something_went_wrong),
        changeTimeline = stringResource(R.string.change_timeline),
        showing = stringResource(R.string.showing),
        activityOverview = stringResource(R.string.activity_overview),
        pastWeek = stringResource(R.string.past_week),
        pastMonth = stringResource(R.string.past_month),
        pastSixMonths = stringResource(R.string.past_6_months),
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
        every = stringResource(R.string.every),
        back = stringResource(R.string.back),
        justNow = stringResource(R.string.just_now),
        exportData = stringResource(R.string.export_data),
        installedApps = stringResource(R.string.installed_apps),
        singularApp = stringResource(R.string.singular_app),
        closeSdkDetails = stringResource(R.string.close_sdk_details),
        noInstalledAppsTargetSdk = stringResource(R.string.no_installed_apps_target_sdk),
        exportDataDescription = stringResource(R.string.export_data_description),
        privacy = stringResource(R.string.privacy_first_title),
        privacyBody = stringResource(R.string.no_data_collection),
        viewOnGitHub = stringResource(R.string.view_on_github),
        getInTouch = stringResource(R.string.get_in_touch),
        contactDescription = stringResource(R.string.send_feedback_or_ask_questions),
        noDataCollection = stringResource(R.string.no_data_collection),
        contact = stringResource(R.string.contact),
        project = stringResource(R.string.project),
        dataAndPrivacy = stringResource(R.string.data_and_privacy),
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
