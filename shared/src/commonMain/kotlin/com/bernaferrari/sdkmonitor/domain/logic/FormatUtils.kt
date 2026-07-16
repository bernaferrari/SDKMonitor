package com.bernaferrari.sdkmonitor.domain.logic

/**
 * Human-readable byte size for details UI (multiplatform-safe).
 */
fun formatFileSize(sizeInBytes: Long): String {
    val kb = 1024L
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        sizeInBytes < kb -> "$sizeInBytes B"
        sizeInBytes < mb -> {
            val value = sizeInBytes.toDouble() / kb
            "${round1(value)} KB"
        }
        sizeInBytes < gb -> {
            val value = sizeInBytes.toDouble() / mb
            "${round1(value)} MB"
        }
        else -> {
            val value = sizeInBytes.toDouble() / gb
            "${round1(value)} GB"
        }
    }
}

private fun round1(value: Double): String {
    val scaled = kotlin.math.round(value * 10.0) / 10.0
    val asLong = scaled.toLong()
    return if (scaled == asLong.toDouble()) asLong.toString() else scaled.toString()
}
