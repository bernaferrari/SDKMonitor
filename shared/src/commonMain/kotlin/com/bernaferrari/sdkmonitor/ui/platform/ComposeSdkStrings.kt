package com.bernaferrari.sdkmonitor.ui.platform

import androidx.compose.runtime.Composable
import com.bernaferrari.sdkmonitor.shared.resources.Res
import com.bernaferrari.sdkmonitor.shared.resources.about
import com.bernaferrari.sdkmonitor.shared.resources.about_section
import com.bernaferrari.sdkmonitor.shared.resources.all_apps
import com.bernaferrari.sdkmonitor.shared.resources.allow_notifications
import com.bernaferrari.sdkmonitor.shared.resources.analytics
import com.bernaferrari.sdkmonitor.shared.resources.analytics_section
import com.bernaferrari.sdkmonitor.shared.resources.analytics_empty
import com.bernaferrari.sdkmonitor.shared.resources.app_filter
import com.bernaferrari.sdkmonitor.shared.resources.app_information
import com.bernaferrari.sdkmonitor.shared.resources.app_name
import com.bernaferrari.sdkmonitor.shared.resources.app_not_found
import com.bernaferrari.sdkmonitor.shared.resources.appearance
import com.bernaferrari.sdkmonitor.shared.resources.apps
import com.bernaferrari.sdkmonitor.shared.resources.apps_count
import com.bernaferrari.sdkmonitor.shared.resources.background_sync
import com.bernaferrari.sdkmonitor.shared.resources.background_sync_description
import com.bernaferrari.sdkmonitor.shared.resources.cancel
import com.bernaferrari.sdkmonitor.shared.resources.clear
import com.bernaferrari.sdkmonitor.shared.resources.clear_logs
import com.bernaferrari.sdkmonitor.shared.resources.contact
import com.bernaferrari.sdkmonitor.shared.resources.custom
import com.bernaferrari.sdkmonitor.shared.resources.daily
import com.bernaferrari.sdkmonitor.shared.resources.days
import com.bernaferrari.sdkmonitor.shared.resources.enable_sync
import com.bernaferrari.sdkmonitor.shared.resources.enabled_daily
import com.bernaferrari.sdkmonitor.shared.resources.enabled_every
import com.bernaferrari.sdkmonitor.shared.resources.enabled_monthly
import com.bernaferrari.sdkmonitor.shared.resources.enabled_weekly
import com.bernaferrari.sdkmonitor.shared.resources.error
import com.bernaferrari.sdkmonitor.shared.resources.error_loading_settings
import com.bernaferrari.sdkmonitor.shared.resources.export_data
import com.bernaferrari.sdkmonitor.shared.resources.failed_to_load_apps
import com.bernaferrari.sdkmonitor.shared.resources.failed_to_load_logs
import com.bernaferrari.sdkmonitor.shared.resources.hours
import com.bernaferrari.sdkmonitor.shared.resources.interval
import com.bernaferrari.sdkmonitor.shared.resources.just_now
import com.bernaferrari.sdkmonitor.shared.resources.last_update
import com.bernaferrari.sdkmonitor.shared.resources.latest
import com.bernaferrari.sdkmonitor.shared.resources.learn_more_about_app
import com.bernaferrari.sdkmonitor.shared.resources.loading
import com.bernaferrari.sdkmonitor.shared.resources.loading_settings
import com.bernaferrari.sdkmonitor.shared.resources.logs
import com.bernaferrari.sdkmonitor.shared.resources.made_with_love
import com.bernaferrari.sdkmonitor.shared.resources.min_sdk
import com.bernaferrari.sdkmonitor.shared.resources.minutes
import com.bernaferrari.sdkmonitor.shared.resources.monthly
import com.bernaferrari.sdkmonitor.shared.resources.no_analytics_data
import com.bernaferrari.sdkmonitor.shared.resources.no_analytics_subtitle
import com.bernaferrari.sdkmonitor.shared.resources.no_apps_found
import com.bernaferrari.sdkmonitor.shared.resources.no_apps_subtitle
import com.bernaferrari.sdkmonitor.shared.resources.no_logs_subtitle
import com.bernaferrari.sdkmonitor.shared.resources.no_logs_yet
import com.bernaferrari.sdkmonitor.shared.resources.notification_permission_body
import com.bernaferrari.sdkmonitor.shared.resources.notification_permission_title
import com.bernaferrari.sdkmonitor.shared.resources.notification_warning_body
import com.bernaferrari.sdkmonitor.shared.resources.notification_warning_title
import com.bernaferrari.sdkmonitor.shared.resources.notifications_required
import com.bernaferrari.sdkmonitor.shared.resources.open_settings
import com.bernaferrari.sdkmonitor.shared.resources.open_source
import com.bernaferrari.sdkmonitor.shared.resources.package_name
import com.bernaferrari.sdkmonitor.shared.resources.permissions
import com.bernaferrari.sdkmonitor.shared.resources.play_store
import com.bernaferrari.sdkmonitor.shared.resources.plural_days
import com.bernaferrari.sdkmonitor.shared.resources.plural_hours
import com.bernaferrari.sdkmonitor.shared.resources.plural_minutes
import com.bernaferrari.sdkmonitor.shared.resources.privacy
import com.bernaferrari.sdkmonitor.shared.resources.privacy_body
import com.bernaferrari.sdkmonitor.shared.resources.rate_app
import com.bernaferrari.sdkmonitor.shared.resources.refresh
import com.bernaferrari.sdkmonitor.shared.resources.retry
import com.bernaferrari.sdkmonitor.shared.resources.save
import com.bernaferrari.sdkmonitor.shared.resources.search
import com.bernaferrari.sdkmonitor.shared.resources.search_apps
import com.bernaferrari.sdkmonitor.shared.resources.settings
import com.bernaferrari.sdkmonitor.shared.resources.settings_title
import com.bernaferrari.sdkmonitor.shared.resources.singular_day
import com.bernaferrari.sdkmonitor.shared.resources.singular_hour
import com.bernaferrari.sdkmonitor.shared.resources.singular_minute
import com.bernaferrari.sdkmonitor.shared.resources.size
import com.bernaferrari.sdkmonitor.shared.resources.size_label
import com.bernaferrari.sdkmonitor.shared.resources.sort_by_name
import com.bernaferrari.sdkmonitor.shared.resources.sort_by_sdk
import com.bernaferrari.sdkmonitor.shared.resources.sync_dialog_title
import com.bernaferrari.sdkmonitor.shared.resources.sync_interval
import com.bernaferrari.sdkmonitor.shared.resources.syncing
import com.bernaferrari.sdkmonitor.shared.resources.system_app
import com.bernaferrari.sdkmonitor.shared.resources.system_apps
import com.bernaferrari.sdkmonitor.shared.resources.tap_to_configure_sync
import com.bernaferrari.sdkmonitor.shared.resources.target_sdk
import com.bernaferrari.sdkmonitor.shared.resources.theme
import com.bernaferrari.sdkmonitor.shared.resources.theme_dark
import com.bernaferrari.sdkmonitor.shared.resources.theme_dark_description
import com.bernaferrari.sdkmonitor.shared.resources.theme_light
import com.bernaferrari.sdkmonitor.shared.resources.theme_light_description
import com.bernaferrari.sdkmonitor.shared.resources.theme_material_you
import com.bernaferrari.sdkmonitor.shared.resources.theme_material_you_description
import com.bernaferrari.sdkmonitor.shared.resources.theme_system
import com.bernaferrari.sdkmonitor.shared.resources.theme_system_description
import com.bernaferrari.sdkmonitor.shared.resources.unknown_error
import com.bernaferrari.sdkmonitor.shared.resources.updated_label
import com.bernaferrari.sdkmonitor.shared.resources.user_app
import com.bernaferrari.sdkmonitor.shared.resources.user_apps
import com.bernaferrari.sdkmonitor.shared.resources.version
import com.bernaferrari.sdkmonitor.shared.resources.version_history
import com.bernaferrari.sdkmonitor.shared.resources.version_label
import com.bernaferrari.sdkmonitor.shared.resources.weekly
import org.jetbrains.compose.resources.stringResource

/**
 * [SdkStrings] backed by composeResources — use on desktop/web/demo hosts.
 * Android can keep [rememberAndroidSdkStrings] for app-module R.string localization.
 */
@Composable
fun rememberComposeSdkStrings(): SdkStrings =
    SdkStrings(
        apps = stringResource(Res.string.apps),
        logs = stringResource(Res.string.logs),
        settings = stringResource(Res.string.settings),
        about = stringResource(Res.string.about),
        search = stringResource(Res.string.search),
        searchApps = stringResource(Res.string.search_apps),
        clear = stringResource(Res.string.clear),
        retry = stringResource(Res.string.retry),
        refresh = stringResource(Res.string.refresh),
        loading = stringResource(Res.string.loading),
        error = stringResource(Res.string.error),
        unknownError = stringResource(Res.string.unknown_error),
        appNotFound = stringResource(Res.string.app_not_found),
        allApps = stringResource(Res.string.all_apps),
        userApps = stringResource(Res.string.user_apps),
        systemApps = stringResource(Res.string.system_apps),
        sortByName = stringResource(Res.string.sort_by_name),
        sortBySdk = stringResource(Res.string.sort_by_sdk),
        noAppsFound = stringResource(Res.string.no_apps_found),
        noAppsSubtitle = stringResource(Res.string.no_apps_subtitle),
        noLogsYet = stringResource(Res.string.no_logs_yet),
        noLogsSubtitle = stringResource(Res.string.no_logs_subtitle),
        targetSdk = stringResource(Res.string.target_sdk),
        minSdk = stringResource(Res.string.min_sdk),
        version = stringResource(Res.string.version),
        packageName = stringResource(Res.string.package_name),
        lastUpdate = stringResource(Res.string.last_update),
        size = stringResource(Res.string.size),
        permissions = stringResource(Res.string.permissions),
        versionHistory = stringResource(Res.string.version_history),
        theme = stringResource(Res.string.theme),
        themeSystem = stringResource(Res.string.theme_system),
        themeSystemDescription = stringResource(Res.string.theme_system_description),
        themeMaterialYou = stringResource(Res.string.theme_material_you),
        themeMaterialYouDescription = stringResource(Res.string.theme_material_you_description),
        themeLight = stringResource(Res.string.theme_light),
        themeLightDescription = stringResource(Res.string.theme_light_description),
        themeDark = stringResource(Res.string.theme_dark),
        themeDarkDescription = stringResource(Res.string.theme_dark_description),
        backgroundSync = stringResource(Res.string.background_sync),
        backgroundSyncDescription = stringResource(Res.string.background_sync_description),
        syncInterval = stringResource(Res.string.sync_interval),
        appFilter = stringResource(Res.string.app_filter),
        analytics = stringResource(Res.string.analytics),
        analyticsSection = stringResource(Res.string.analytics_section),
        analyticsEmpty = stringResource(Res.string.analytics_empty),
        clearLogs = stringResource(Res.string.clear_logs),
        exportData = stringResource(Res.string.export_data),
        minutes = stringResource(Res.string.minutes),
        hours = stringResource(Res.string.hours),
        days = stringResource(Res.string.days),
        cancel = stringResource(Res.string.cancel),
        save = stringResource(Res.string.save),
        justNow = stringResource(Res.string.just_now),
        systemApp = stringResource(Res.string.system_app),
        userApp = stringResource(Res.string.user_app),
        syncing = stringResource(Res.string.syncing),
        failedToLoadApps = stringResource(Res.string.failed_to_load_apps),
        failedToLoadLogs = stringResource(Res.string.failed_to_load_logs),
        settingsTitle = stringResource(Res.string.settings_title),
        loadingSettings = stringResource(Res.string.loading_settings),
        errorLoadingSettings = stringResource(Res.string.error_loading_settings),
        appearance = stringResource(Res.string.appearance),
        aboutSection = stringResource(Res.string.about_section),
        learnMoreAboutApp = stringResource(Res.string.learn_more_about_app),
        tapToConfigureSync = stringResource(Res.string.tap_to_configure_sync),
        notificationsRequired = stringResource(Res.string.notifications_required),
        enabledDaily = stringResource(Res.string.enabled_daily),
        enabledWeekly = stringResource(Res.string.enabled_weekly),
        enabledMonthly = stringResource(Res.string.enabled_monthly),
        enabledEvery = stringResource(Res.string.enabled_every),
        notificationPermissionTitle = stringResource(Res.string.notification_permission_title),
        notificationPermissionBody = stringResource(Res.string.notification_permission_body),
        allowNotifications = stringResource(Res.string.allow_notifications),
        notificationWarningTitle = stringResource(Res.string.notification_warning_title),
        notificationWarningBody = stringResource(Res.string.notification_warning_body),
        openSettings = stringResource(Res.string.open_settings),
        daily = stringResource(Res.string.daily),
        weekly = stringResource(Res.string.weekly),
        monthly = stringResource(Res.string.monthly),
        custom = stringResource(Res.string.custom),
        syncDialogTitle = stringResource(Res.string.sync_dialog_title),
        interval = stringResource(Res.string.interval),
        enableSync = stringResource(Res.string.enable_sync),
        singularMinute = stringResource(Res.string.singular_minute),
        singularHour = stringResource(Res.string.singular_hour),
        singularDay = stringResource(Res.string.singular_day),
        pluralMinutes = stringResource(Res.string.plural_minutes),
        pluralHours = stringResource(Res.string.plural_hours),
        pluralDays = stringResource(Res.string.plural_days),
        latest = stringResource(Res.string.latest),
        appsCount = stringResource(Res.string.apps_count),
        noAnalyticsData = stringResource(Res.string.no_analytics_data),
        noAnalyticsSubtitle = stringResource(Res.string.no_analytics_subtitle),
        privacy = stringResource(Res.string.privacy),
        privacyBody = stringResource(Res.string.privacy_body),
        openSource = stringResource(Res.string.open_source),
        rateApp = stringResource(Res.string.rate_app),
        contact = stringResource(Res.string.contact),
        madeWithLove = stringResource(Res.string.made_with_love),
        versionLabel = stringResource(Res.string.version_label),
        updatedLabel = stringResource(Res.string.updated_label),
        sizeLabel = stringResource(Res.string.size_label),
        appInformation = stringResource(Res.string.app_information),
        playStore = stringResource(Res.string.play_store),
        appName = stringResource(Res.string.app_name),
    )
