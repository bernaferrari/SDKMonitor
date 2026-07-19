package com.bernaferrari.sdkmonitor.extensions

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.bernaferrari.sdkmonitor.R

internal fun Long.convertTimestampToDate(context: Context): String {
    if (this == 0L) return context.getString(R.string.never)

    val elapsed = (System.currentTimeMillis() - this).coerceAtLeast(0L)
    return when {
        elapsed < MinuteMillis -> context.getString(R.string.just_now)
        elapsed < HourMillis -> context.resources.getQuantityString(
            R.plurals.minutes_ago,
            (elapsed / MinuteMillis).toInt(),
            elapsed / MinuteMillis,
        )
        elapsed < DayMillis -> context.resources.getQuantityString(
            R.plurals.hours_ago,
            (elapsed / HourMillis).toInt(),
            elapsed / HourMillis,
        )
        elapsed < WeekMillis -> context.resources.getQuantityString(
            R.plurals.days_ago,
            (elapsed / DayMillis).toInt(),
            elapsed / DayMillis,
        )
        elapsed < MonthMillis -> context.resources.getQuantityString(
            R.plurals.weeks_ago,
            (elapsed / WeekMillis).toInt(),
            elapsed / WeekMillis,
        )
        else -> context.resources.getQuantityString(
            R.plurals.months_ago,
            (elapsed / MonthMillis).toInt(),
            elapsed / MonthMillis,
        )
    }
}

private const val MinuteMillis = 60_000L
private const val HourMillis = 60 * MinuteMillis
private const val DayMillis = 24 * HourMillis
private const val WeekMillis = 7 * DayMillis
private const val MonthMillis = 30 * DayMillis

internal operator fun Boolean.inc() = !this

inline val @receiver:ColorInt Int.darken
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.BLACK, 0.2f)

inline val @receiver:ColorInt Int.lighten
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.WHITE, 0.2f)

// colors inspired from https://www.vanschneider.com/colors — delegate to shared ApiLevel
fun Int.apiToColor(): Int = com.bernaferrari.sdkmonitor.domain.logic.ApiLevel.colorArgb(this).toInt()

fun Int.apiToVersion(): String = com.bernaferrari.sdkmonitor.domain.logic.ApiLevel.versionName(this)
