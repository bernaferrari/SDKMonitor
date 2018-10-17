package com.bernaferrari.sdkmonitor.extensions

import android.text.SpannableStringBuilder
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat

internal fun AppCompatTextView.setTextAsync(text: String) =
    this.setTextFuture(
        PrecomputedTextCompat.getTextFuture(
            text,
            TextViewCompat.getTextMetricsParams(this),
            null
        )
    )

internal fun AppCompatTextView.setTextAsync(text: SpannableStringBuilder) =
    this.setTextFuture(
        PrecomputedTextCompat.getTextFuture(
            text,
            TextViewCompat.getTextMetricsParams(this),
            null
        )
    )