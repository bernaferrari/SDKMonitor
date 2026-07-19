package com.bernaferrari.sdkmonitor.domain.logic

import com.bernaferrari.sdkmonitor.shared.mock.MockDemoData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ApiLevelTest {
    @Test
    fun latestMinus_returnsSdkRelativeToLatestSupported() {
        assertEquals(ApiLevel.LATEST_SUPPORTED, ApiLevel.latestMinus(0))
        assertEquals(ApiLevel.LATEST_SUPPORTED - 1, ApiLevel.latestMinus(1))
        assertEquals(ApiLevel.LATEST_SUPPORTED - 2, ApiLevel.latestMinus(2))
        assertFailsWith<IllegalArgumentException> { ApiLevel.latestMinus(-1) }
    }

    @Test
    fun colors_followDistanceFromLatestSupportedSdk() {
        assertEquals(0xFF14B572, ApiLevel.colorArgb(ApiLevel.latestMinus(0)))
        assertEquals(0xFF178E96, ApiLevel.colorArgb(ApiLevel.latestMinus(1)))
        assertEquals(0xFFE37A46, ApiLevel.colorArgb(ApiLevel.latestMinus(2)))
        assertEquals(0xFFE54B4B, ApiLevel.colorArgb(ApiLevel.latestMinus(3)))
        assertEquals(0xFFD31B33, ApiLevel.colorArgb(ApiLevel.latestMinus(4)))
    }

    @Test
    fun mockApps_coverEverySdkStatusBand() {
        val sdkLevels = MockDemoData.apps.map { it.sdkVersion }.toSet()

        assertEquals((0..3).map(ApiLevel::latestMinus).toSet(), sdkLevels)
        assertTrue(MockDemoData.apps.count { it.sdkVersion == ApiLevel.latestMinus(3) } >= 3)
    }
}
