package com.bernaferrari.sdkmonitor.functions

import androidx.appfunctions.AppFunctionSerializable

@AppFunctionSerializable(isDescribedByKDoc = true)
data class AppTargetSdk(
    val packageName: String,
    val appName: String,
    val targetSdk: Int,
    val androidVersion: String,
    val minSdk: Int,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class AppTargetSdkSummary(
    val apps: List<AppTargetSdk>,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class LookupAppParams(
    val query: String,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class SearchAppsParams(
    val query: String,
    val limit: Int = 10,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class ListAppsBelowTargetSdkParams(
    val targetSdk: Int,
    val limit: Int = 20,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class RecentSdkChangesParams(
    val limit: Int = 10,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class SdkChange(
    val packageName: String,
    val appName: String,
    val oldTargetSdk: Int?,
    val newTargetSdk: Int,
    val timestamp: Long,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class RecentSdkChangesResult(
    val changes: List<SdkChange>,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class SdkDistributionEntry(
    val targetSdk: Int,
    val androidVersion: String,
    val appCount: Int,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class SdkDistribution(
    val totalApps: Int,
    val entries: List<SdkDistributionEntry>,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class OpenAppDetailsParams(
    val packageName: String,
)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class OpenAppDetailsResult(
    val packageName: String,
    val message: String,
)