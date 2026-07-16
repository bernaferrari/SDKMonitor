package com.bernaferrari.sdkmonitor.domain.logic

import com.bernaferrari.sdkmonitor.domain.LocalTimeUnit

object SyncInterval {
    fun parse(interval: String): Pair<String, LocalTimeUnit> =
        try {
            when {
                interval.endsWith("m") -> interval.dropLast(1) to LocalTimeUnit.MINUTES
                interval.endsWith("h") -> interval.dropLast(1) to LocalTimeUnit.HOURS
                interval.endsWith("d") -> interval.dropLast(1) to LocalTimeUnit.DAYS
                interval.toIntOrNull() != null -> {
                    val value = interval.toInt()
                    when {
                        value <= 24 -> interval to LocalTimeUnit.HOURS
                        value <= 168 -> (value / 24).toString() to LocalTimeUnit.DAYS
                        else -> "7" to LocalTimeUnit.DAYS
                    }
                }
                else -> "7" to LocalTimeUnit.DAYS
            }
        } catch (_: Exception) {
            "7" to LocalTimeUnit.DAYS
        }

    fun format(
        interval: String,
        unit: LocalTimeUnit,
    ): String =
        when (unit) {
            LocalTimeUnit.MINUTES -> "${interval}m"
            LocalTimeUnit.HOURS -> "${interval}h"
            LocalTimeUnit.DAYS -> "${interval}d"
        }
}
