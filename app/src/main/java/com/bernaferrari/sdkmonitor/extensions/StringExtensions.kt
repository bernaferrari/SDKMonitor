package com.bernaferrari.sdkmonitor.extensions

import com.bernaferrari.sdkmonitor.domain.logic.StringNormalize

internal fun String.normalizeString(): String = StringNormalize.normalize(this)
