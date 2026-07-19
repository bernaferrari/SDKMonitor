package com.bernaferrari.sdkmonitor.domain.logic

/**
 * Human-readable byte size for details UI (multiplatform-safe).
 */
fun formatFileSize(sizeInBytes: Long): String {
    val kb = 1024L
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        sizeInBytes < kb -> "$sizeInBytes B"
        sizeInBytes < mb -> {
            val value = sizeInBytes.toDouble() / kb
            "${round1(value)} KB"
        }
        sizeInBytes < gb -> {
            val value = sizeInBytes.toDouble() / mb
            "${round1(value)} MB"
        }
        else -> {
            val value = sizeInBytes.toDouble() / gb
            "${round1(value)} GB"
        }
    }
}

/**
 * Shared relative-time presentation for every target. The mock only supplies timestamps; this
 * function decides how both Android and web describe them.
 */
fun formatRelativeTimestamp(timestamp: Long, now: Long): String {
    if (timestamp == 0L) return "Never"
    val elapsed = (now - timestamp).coerceAtLeast(0L)
    return when {
        elapsed < MinuteMillis -> "Just now"
        elapsed < HourMillis -> "${elapsed / MinuteMillis} min ago"
        elapsed < DayMillis -> "${elapsed / HourMillis} hr ago"
        elapsed < WeekMillis -> {
            val days = elapsed / DayMillis
            if (days == 1L) "1 day ago" else "$days days ago"
        }
        elapsed < MonthMillis -> {
            val weeks = elapsed / WeekMillis
            if (weeks == 1L) "1 week ago" else "$weeks weeks ago"
        }
        else -> {
            val months = elapsed / MonthMillis
            if (months == 1L) "1 month ago" else "$months months ago"
        }
    }
}

private const val MinuteMillis = 60_000L
private const val HourMillis = 60 * MinuteMillis
private const val DayMillis = 24 * HourMillis
private const val WeekMillis = 7 * DayMillis
private const val MonthMillis = 30 * DayMillis

private fun round1(value: Double): String {
    val scaled = kotlin.math.round(value * 10.0) / 10.0
    val asLong = scaled.toLong()
    return if (scaled == asLong.toDouble()) asLong.toString() else scaled.toString()
}
