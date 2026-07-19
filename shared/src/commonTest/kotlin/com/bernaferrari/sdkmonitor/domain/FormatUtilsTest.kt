package com.bernaferrari.sdkmonitor.domain

import com.bernaferrari.sdkmonitor.domain.logic.formatFileSize
import com.bernaferrari.sdkmonitor.domain.logic.formatRelativeTimestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormatUtilsTest {
    @Test
    fun formatFileSize_scales() {
        assertEquals("512 B", formatFileSize(512))
        assertTrue(formatFileSize(2048).contains("KB"))
        assertTrue(formatFileSize(5L * 1024 * 1024).contains("MB"))
    }

    @Test
    fun formatRelativeTimestamp_usesSingularDay() {
        val day = 24 * 60 * 60 * 1000L

        assertEquals("1 day ago", formatRelativeTimestamp(1L, day + 1L))
        assertEquals("2 days ago", formatRelativeTimestamp(1L, 2 * day + 1L))
    }
}
