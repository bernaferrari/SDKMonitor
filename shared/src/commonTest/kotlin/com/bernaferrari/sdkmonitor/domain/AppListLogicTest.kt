package com.bernaferrari.sdkmonitor.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppListLogicTest {
    private val sample =
        listOf(
            AppVersion("com.a", "Alpha", 34, "", isSystemApp = false),
            AppVersion("com.b", "Beta", 33, "", isSystemApp = true),
            AppVersion("com.c", "Charlie", 35, "", isSystemApp = false),
        )

    @Test
    fun filterByAppFilter_userApps_excludesSystem() {
        val result = AppListLogic.filterByAppFilter(sample, AppFilter.USER_APPS)
        assertEquals(2, result.size)
        assertTrue(result.none { it.isSystemApp })
    }

    @Test
    fun filterByAppFilter_systemApps_onlySystem() {
        val result = AppListLogic.filterByAppFilter(sample, AppFilter.SYSTEM_APPS)
        assertEquals(1, result.size)
        assertEquals("com.b", result.first().packageName)
    }

    @Test
    fun searchApps_matchesTitleAndPackage() {
        assertEquals(1, AppListLogic.searchApps(sample, "alp").size)
        assertEquals(1, AppListLogic.searchApps(sample, "com.c").size)
        assertEquals(sample.size, AppListLogic.searchApps(sample, "  ").size)
    }

    @Test
    fun sortApps_bySdkDescendingThenName() {
        val sorted = AppListLogic.sortApps(sample, SortOption.SDK)
        assertEquals(listOf(35, 34, 33), sorted.map { it.sdkVersion })
    }

    @Test
    fun sortApps_byName() {
        val sorted = AppListLogic.sortApps(sample, SortOption.NAME)
        assertEquals(listOf("Alpha", "Beta", "Charlie"), sorted.map { it.title })
    }

    @Test
    fun applyListPipeline_combinesFilterSortSearch() {
        val result =
            AppListLogic.applyListPipeline(
                apps = sample,
                filter = AppFilter.USER_APPS,
                sortOption = SortOption.NAME,
                orderBySdk = false,
                searchQuery = "a",
            )
        assertEquals(listOf("Alpha", "Charlie"), result.map { it.title })
    }

    @Test
    fun sdkDistribution_countsPerSdk() {
        val dist = AppListLogic.sdkDistribution(sample)
        assertEquals(1, dist[34])
        assertEquals(1, dist[33])
        assertEquals(1, dist[35])
    }
}
