package com.bernaferrari.sdkmonitor.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.marlonlom.utilities.timeago.TimeAgo

internal fun Long.convertTimestampToDate(): String = TimeAgo.using(this)

internal operator fun Boolean.inc() = !this

internal fun Int.toDpF(resources: Resources): Float = resources.displayMetrics.density * this

/**
 * For Fragments, allows declarations like
 * ```
 * val myViewModel = viewModelProvider(myViewModelFactory)
 * ```
 */
inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
    provider: ViewModelProvider.Factory
) = ViewModelProviders.of(this, provider).get(VM::class.java)

/** Convenience for callbacks/listeners whose return value indicates an event was consumed. */
inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

internal fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

/**
 * Allows calls like
 *
 * `viewGroup.inflate(R.layout.foo)`
 */
fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layout, this, attachToRoot)
}

inline val @receiver:ColorInt Int.darken
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.BLACK, 0.2f)

inline val @receiver:ColorInt Int.lighten
    @ColorInt
    get() = ColorUtils.blendARGB(this, Color.WHITE, 0.2f)


// colors inspired from https://www.vanschneider.com/colors
fun Int.apiToColor(): Int = when (this) {
    in 0..20 -> 0xFFD31B33.toInt() // red
    in 21..23 -> 0xFFE54B4B.toInt() // red-orange
    in 24..25 -> 0xFFE37A46.toInt() // orange
    in 26..27 -> 0XFF178E96.toInt() // blue-green
    else -> 0xFF14B572.toInt() // green
}

fun Int.apiToVersion() = when (this) {
    2 -> "Petit Four"
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
    else -> ""
}
