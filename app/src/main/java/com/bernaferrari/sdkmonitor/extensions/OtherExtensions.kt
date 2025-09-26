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
            diff < 60_000 -> context.getString(R.string.just_now)
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
        in 0..32 -> 0xFFD31B33.toInt() // red
        33 -> 0xFFE54B4B.toInt() // red-orange
        34 -> 0xFFE37A46.toInt() // orange
        35 -> 0XFF178E96.toInt() // blue-green
        else -> 0xFF14B572.toInt() // green
    }

fun Int.apiToVersion() =
    when (this) {
        0 -> "Error"
        1, 2 -> "Base"
        3 -> "Cupcake"
        4 -> "Donut"
        5 -> "Eclair"
        6 -> "Eclair MR1"
        7 -> "Eclair MR2"
        8 -> "Froyo"
        9 -> "Gingerbread"
        10 -> "Gingerbread MR1"
        11 -> "Honeycomb"
        12 -> "Honeycomb MR1"
        13 -> "Honeycomb MR2"
        14 -> "Ice Cream Sandwich"
        15 -> "Ice Cream Sandwich MR1"
        16 -> "Jelly Bean"
        17 -> "Jelly Bean MR1"
        18 -> "Jelly Bean MR2"
        19 -> "KitKat"
        20 -> "KitKat MR1"
        21 -> "Lollipop"
        22 -> "Lollipop MR1"
        23 -> "Marshmallow"
        24 -> "Nougat"
        25 -> "Nougat MR1"
        26 -> "Oreo"
        27 -> "Oreo MR1"
        28 -> "Pie"
        29 -> "Quince Tart"
        30 -> "Red Velvet Cake"
        31 -> "Snow Cone"
        32 -> "Snow Cone V2"
        33 -> "Tiramisu"
        34 -> "Upside Down Cake"
        35 -> "Vanilla Ice Cream"
        36 -> "Baklava"
        else -> "Android ${this - 20}"
    }
