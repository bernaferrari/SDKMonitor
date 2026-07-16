package com.bernaferrari.sdkmonitor.domain

import com.bernaferrari.sdkmonitor.domain.logic.LogEntryLogic
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogEntryLogicTest {
    private val apps =
        listOf(
            TrackedApp("com.a", "Alpha", 0, isFromPlayStore = true),
            TrackedApp("com.sys", "System", 0, isFromPlayStore = false),
        )

    @Test
    fun buildChangeLogs_emitsSdkAndVersionChanges() {
        val versions =
            listOf(
                TrackedVersion(1, 1, "com.a", "1.0", 1000L, 33),
                TrackedVersion(2, 2, "com.a", "1.1", 2000L, 34),
                TrackedVersion(3, 3, "com.a", "1.1", 3000L, 34), // no change — skipped
            )
        val logs = LogEntryLogic.buildChangeLogs(versions, apps, AppFilter.ALL_APPS)
        assertEquals(1, logs.size)
        assertEquals(33, logs.first().oldSdk)
        assertEquals(34, logs.first().newSdk)
        assertEquals("1.0", logs.first().oldVersion)
        assertEquals("1.1", logs.first().newVersion)
    }

    @Test
    fun buildChangeLogs_respectsUserFilter() {
        val versions =
            listOf(
                TrackedVersion(1, 1, "com.sys", "1.0", 1000L, 30),
                TrackedVersion(2, 2, "com.sys", "2.0", 2000L, 31),
            )
        val logs = LogEntryLogic.buildChangeLogs(versions, apps, AppFilter.USER_APPS)
        assertTrue(logs.isEmpty())
        val systemLogs = LogEntryLogic.buildChangeLogs(versions, apps, AppFilter.SYSTEM_APPS)
        assertEquals(1, systemLogs.size)
    }

    @Test
    fun buildChangeLogs_sortedNewestFirst() {
        val versions =
            listOf(
                TrackedVersion(1, 1, "com.a", "1.0", 1000L, 33),
                TrackedVersion(2, 2, "com.a", "1.1", 2000L, 34),
                TrackedVersion(3, 3, "com.a", "1.2", 3000L, 35),
            )
        val logs = LogEntryLogic.buildChangeLogs(versions, apps, AppFilter.ALL_APPS)
        assertEquals(2, logs.size)
        assertTrue(logs[0].timestamp >= logs[1].timestamp)
    }
}
