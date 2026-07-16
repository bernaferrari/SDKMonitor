package com.bernaferrari.sdkmonitor.domain.logic

import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.AppListLogic
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.SdkDistribution

object AnalyticsLogic {
    fun sdkDistribution(
        apps: List<AppVersion>,
        filter: AppFilter,
    ): Pair<List<SdkDistribution>, List<AppVersion>> {
        val filtered = AppListLogic.filterByAppFilter(apps, filter)
        val distribution =
            if (filtered.isEmpty()) {
                emptyList()
            } else {
                filtered
                    .groupBy { it.sdkVersion }
                    .map { (sdk, appList) ->
                        SdkDistribution(
                            sdkVersion = sdk,
                            appCount = appList.size,
                            percentage = appList.size.toFloat() / filtered.size,
                        )
                    }.sortedByDescending { it.sdkVersion }
            }
        return distribution to filtered
    }
}
