package com.bernaferrari.sdkmonitor.extensions

import java.text.Normalizer

internal fun String.normalizeString() =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .lowercase()
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")