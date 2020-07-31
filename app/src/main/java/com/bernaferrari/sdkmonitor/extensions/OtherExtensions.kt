package com.bernaferrari.sdkmonitor.extensions

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.github.marlonlom.utilities.timeago.TimeAgo
import io.reactivex.Observable

internal fun Long.convertTimestampToDate(): String = TimeAgo.using(this)

internal operator fun Boolean.inc() = !this

inline val @receiver:ColorInt Int.darken
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.BLACK, 0.2f)

inline val @receiver:ColorInt Int.lighten
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.WHITE, 0.2f)


// colors inspired from https://www.vanschneider.com/colors
fun Int.apiToColor(): Int = when (this) {
    in 0..22 -> 0xFFD31B33.toInt() // red
    in 23..25 -> 0xFFE54B4B.toInt() // red-orange
    in 26..27 -> 0xFFE37A46.toInt() // orange
    in 28..29 -> 0XFF178E96.toInt() // blue-green
    else -> 0xFF14B572.toInt() // green
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
    else -> "Android ${this - 18}"
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