package com.bernaferrari.sdkmonitor.extensions

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.app.Fragment
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import com.github.marlonlom.utilities.timeago.TimeAgo

internal typealias ColorGroup = Pair<Int, Int>

internal fun Long.convertTimestampToDate(): String = TimeAgo.using(this)

internal fun View.getText(@StringRes res: Int) = this.resources.getText(res)

internal operator fun Boolean.inc() = !this

internal fun ImageView.setAndStartAnimation(res: Int) {
    this.setImageDrawable(AnimatedVectorDrawableCompat.create(this.context, res))
    (this.drawable as AnimatedVectorDrawableCompat).start()
}

// this will scroll to wanted index + or - one, giving a margin of one and allowing user to
// keep tapping in the same place and RecyclerView keep scrolling.
internal fun RecyclerView.scrollToIndexWithMargins(
    previousIndex: Int,
    index: Int,
    size: Int
) {
    if (previousIndex == -1) {
        // This will run on first iteration
        when (index) {
            in 1 until (size - 1) -> this.scrollToPosition(index - 1)
            (size - 1) -> this.scrollToPosition(index)
            else -> this.scrollToPosition(0)
        }
    } else {
        when (index) {
            in 1 until previousIndex -> this.scrollToPosition(index - 1)
            in previousIndex until (size - 1) -> this.scrollToPosition(index + 1)
            else -> this.scrollToPosition(index)
        }
    }
}

internal fun Int.toDp(resources: Resources): Int {
    return (resources.displayMetrics.density * this).toInt()
}

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

internal fun Context.openInBrowser(url: String?) {
    if (url != null) {
        this.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }
}

internal fun Fragment.getStringFromArguments(key: String, default: String = ""): String =
    arguments?.getString(key) ?: default

/** Convenience for callbacks/listeners whose return value indicates an event was consumed. */
inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

/** Convenience for try/catch where the exception is ignored. */
inline fun trySilently(f: () -> Unit) {
    try {
        f()
    } catch (e: Exception) {

    }
}

internal fun RecyclerView.itemAnimatorWithoutChangeAnimations() =
    this.itemAnimator.apply {
        // From https://stackoverflow.com/a/33302517/4418073
        if (this is SimpleItemAnimator) {
            this.supportsChangeAnimations = false
        }
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
