package com.bernaferrari.sdkmonitor.extensions

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.logic.formatRelativeTimestamp

internal fun Long.convertTimestampToDate(context: Context): String =
    formatRelativeTimestamp(this, System.currentTimeMillis())

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
