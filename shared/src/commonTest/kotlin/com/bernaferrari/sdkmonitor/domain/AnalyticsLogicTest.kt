package com.bernaferrari.sdkmonitor.domain

import com.bernaferrari.sdkmonitor.domain.logic.AnalyticsLogic
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnalyticsLogicTest {
    private val sample =
        listOf(
            AppVersion("com.a", "A", 34, "", isSystemApp = false),
            AppVersion("com.b", "B", 34, "", isSystemApp = false),
            AppVersion("com.c", "C", 33, "", isSystemApp = true),
        )

    @Test
    fun sdkDistribution_percentagesAndSort() {
        val (dist, filtered) = AnalyticsLogic.sdkDistribution(sample, AppFilter.ALL_APPS)
        assertEquals(3, filtered.size)
        assertEquals(2, dist.size)
        assertEquals(34, dist.first().sdkVersion)
        assertEquals(2, dist.first().appCount)
        assertEquals(2f / 3f, dist.first().percentage)
    }

    @Test
    fun sdkDistribution_emptyWhenNoAppsMatchFilter() {
        val (dist, filtered) = AnalyticsLogic.sdkDistribution(sample, AppFilter.SYSTEM_APPS)
        assertEquals(1, filtered.size)
        assertEquals(1, dist.size)
        assertEquals(1f, dist.first().percentage)
    }

    @Test
    fun sdkDistribution_emptyInput() {
        val (dist, filtered) = AnalyticsLogic.sdkDistribution(emptyList(), AppFilter.ALL_APPS)
        assertTrue(dist.isEmpty())
        assertTrue(filtered.isEmpty())
    }
}
