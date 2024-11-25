package com.bernaferrari.sdkmonitor.extensions

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.github.marlonlom.utilities.timeago.TimeAgo
import io.reactivex.Observable
import java.util.Date
import java.util.GregorianCalendar
import kotlin.time.Duration.Companion.days

internal fun Long.convertTimestampToDate(): String = TimeAgo.using(this)

internal operator fun Boolean.inc() = !this

inline val @receiver:ColorInt Int.darken
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.BLACK, 0.2f)

inline val @receiver:ColorInt Int.lighten
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.WHITE, 0.2f)

/**
 * Colors inspired from https://www.vanschneider.com/colors
 *
 * | Final release date | Color      |
 * |--------------------|------------|
 * | < 1 year           | Green      |
 * | < 2 years          | Blue-green |
 * | < 3 years          | Orange     |
 * | < 4 years          | Red-orange |
 * | > 4 years          | Red        |
 */
fun Int.apiToColor(): Int = with (this) {
    when {
        this < 30 -> 0xFFD31B33.toInt() // Lower Android versions -> red
        this.apiToReleaseDate() == null -> 0xFF14B572.toInt() // Not yet released -> green
        this.apiToReleaseDate()!!.time.time < Date().time - 3.times(365).days.inWholeMilliseconds -> 0xFFE54B4B.toInt() // red-orange
        this.apiToReleaseDate()!!.time.time < Date().time - 2.times(365).days.inWholeMilliseconds -> 0xFFE37A46.toInt() // orange
        this.apiToReleaseDate()!!.time.time < Date().time - 1.times(365).days.inWholeMilliseconds -> 0XFF178E96.toInt() // blue-green
        else -> 0xFF14B572.toInt() // less than a year -> green
    }
}

fun Int.apiToVersion() = when (this) {
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

fun Int.apiToReleaseDate() = when (this) {
    30 -> GregorianCalendar(2020, 7, 8)
    31 -> GregorianCalendar(2021, 8, 11)
    32 -> GregorianCalendar(2022, 3, 7)
    33 -> GregorianCalendar(2022, 6, 8)
    34 -> GregorianCalendar(2023, 6, 7)
    35 -> GregorianCalendar(2024, 6, 18)
    36 -> GregorianCalendar(2025, 6, 30) // Planned Q2 2025
    else -> null
}

/**
 * Composes an [rx.Observable] from multiple creation functions chained by [rx.Observable.switchMap].
 *
 * @return composed Observable
 */
fun <A, B, R> doSwitchMap(
        zero: () -> Observable<A>,
        one: (A) -> Observable<B>,
        two: (A, B) -> Observable<R>
): Observable<R> =
        zero.invoke()
                .switchMap { a ->
                    one.invoke(a)
                            .switchMap { b ->
                                two.invoke(a, b)
                            }
                }
