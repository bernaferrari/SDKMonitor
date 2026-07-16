package com.bernaferrari.sdkmonitor.ui.details

import android.content.Context
import com.bernaferrari.sdkmonitor.data.Version
import com.bernaferrari.sdkmonitor.domain.AppDetails
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.TrackedVersion
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate

fun Version.toAppVersion(
    appDetails: AppDetails,
    context: Context,
) = AppVersion(
    packageName = this.packageName,
    title = appDetails.title,
    sdkVersion = this.targetSdk,
    versionName = this.versionName,
    versionCode = this.version,
    lastUpdateTime = this.lastUpdateTime.convertTimestampToDate(context),
)

fun TrackedVersion.toAppVersion(
    appDetails: AppDetails,
    context: Context,
) = AppVersion(
    packageName = this.packageName,
    title = appDetails.title,
    sdkVersion = this.targetSdk,
    versionName = this.versionName,
    versionCode = this.versionCode,
    lastUpdateTime = this.lastUpdateTime.convertTimestampToDate(context),
)
