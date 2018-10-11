package com.bernaferrari.sdkmonitor.extensions

import android.support.v4.text.PrecomputedTextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.AppCompatTextView
import android.text.SpannableStringBuilder

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