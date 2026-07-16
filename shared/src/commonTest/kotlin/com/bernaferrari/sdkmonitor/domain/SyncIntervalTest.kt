package com.bernaferrari.sdkmonitor.domain

import com.bernaferrari.sdkmonitor.domain.logic.SyncInterval
import kotlin.test.Test
import kotlin.test.assertEquals

class SyncIntervalTest {
    @Test
    fun parse_suffixUnits() {
        assertEquals("30" to LocalTimeUnit.MINUTES, SyncInterval.parse("30m"))
        assertEquals("2" to LocalTimeUnit.HOURS, SyncInterval.parse("2h"))
        assertEquals("7" to LocalTimeUnit.DAYS, SyncInterval.parse("7d"))
    }

    @Test
    fun format_roundTrip() {
        assertEquals("30m", SyncInterval.format("30", LocalTimeUnit.MINUTES))
        assertEquals("1h", SyncInterval.format("1", LocalTimeUnit.HOURS))
        assertEquals("7d", SyncInterval.format("7", LocalTimeUnit.DAYS))
    }

    @Test
    fun parse_legacyNumeric() {
        assertEquals("12" to LocalTimeUnit.HOURS, SyncInterval.parse("12"))
        assertEquals("7" to LocalTimeUnit.DAYS, SyncInterval.parse("168"))
    }
}
