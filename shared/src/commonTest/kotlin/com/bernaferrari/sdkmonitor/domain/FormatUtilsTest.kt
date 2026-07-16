package com.bernaferrari.sdkmonitor.domain

import com.bernaferrari.sdkmonitor.domain.logic.formatFileSize
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
}
