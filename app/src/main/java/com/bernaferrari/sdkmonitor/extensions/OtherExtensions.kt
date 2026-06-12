package com.bernaferrari.sdkmonitor.extensions

import android.content.Context
import android.graphics.Color
import android.text.format.DateUtils
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.bernaferrari.sdkmonitor.R
import java.util.Calendar
import java.util.Date

internal fun Long.convertTimestampToDate(context: Context): String =
    if (this == 0L) {
        "Never" // You might want to add this to strings.xml too
    } else {
        val now = System.currentTimeMillis()
        val diff = now - this

        when {
            diff < 60_000 -> {
                context.getString(R.string.just_now)
            }

            diff < 3_600_000 -> {
                val minutes = (diff / 60_000).toInt()
                context.resources.getQuantityString(R.plurals.minutes_ago, minutes, minutes)
            }

            diff < 86_400_000 -> {
                val hours = (diff / 3_600_000).toInt()
                context.resources.getQuantityString(R.plurals.hours_ago, hours, hours)
            }

            diff < 604_800_000 -> {
                val days = (diff / 86_400_000).toInt()
                context.resources.getQuantityString(R.plurals.days_ago, days, days)
            }

            else -> {
                val date = Date(this)
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val timestampYear =
                    Calendar
                        .getInstance()
                        .apply { timeInMillis = this@convertTimestampToDate }
                        .get(Calendar.YEAR)

                // Use Android's built-in localized date formatting
                DateUtils.formatDateTime(
                    context,
                    this@convertTimestampToDate,
                    if (timestampYear == currentYear) {
                        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_YEAR
                    } else {
                        DateUtils.FORMAT_SHOW_DATE
                    },
                )
            }
        }
    }

internal operator fun Boolean.inc() = !this

inline val @receiver:ColorInt Int.darken
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.BLACK, 0.2f)

inline val @receiver:ColorInt Int.lighten
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.WHITE, 0.2f)

// colors inspired from https://www.vanschneider.com/colors
fun Int.apiToColor(): Int =
    when (this) {
        in 0..33 -> 0xFFD31B33.toInt()

        // red
        34 -> 0xFFE54B4B.toInt()

        // red-orange
        35 -> 0xFFE37A46.toInt()

        // orange
        36 -> 0XFF178E96.toInt()

        // blue-green
        else -> 0xFF14B572.toInt() // green
    }

fun Int.apiToVersion() =
    when (this) {
        3 -> "Cupcake"
        4 -> "Donut"
        5, 6, 7 -> "Eclair"
        8 -> "Froyo"
        9, 10 -> "Gingerbread"
        11, 12, 13 -> "Honeycomb"
        14, 15 -> "Ice Cream Sandwich"
        16, 17, 18 -> "Jelly Bean"
        19, 20 -> "KitKat"
        21, 22 -> "Lollipop"
        23 -> "Marshmallow"
        24, 25 -> "Nougat"
        26, 27 -> "Oreo"
        28 -> "Pie"
        29, 30, 31 -> "Android ${this - 19}"
        32 -> "Android 12L"
        else -> "Android ${this - 20}"
    }
