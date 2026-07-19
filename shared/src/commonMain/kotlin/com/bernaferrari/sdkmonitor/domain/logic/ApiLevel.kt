package com.bernaferrari.sdkmonitor.domain.logic

/**
 * Pure helpers for Android API levels (shared with demos and App Functions).
 */
object ApiLevel {
    /** Newest API level represented by the app's status colors and demo data. */
    const val LATEST_SUPPORTED = 37

    fun latestMinus(versionsBehind: Int): Int {
        require(versionsBehind >= 0) { "versionsBehind must not be negative" }
        return (LATEST_SUPPORTED - versionsBehind).coerceAtLeast(1)
    }

    /** ARGB without alpha channel bits; callers add alpha if needed. */
    fun colorArgb(api: Int): Long =
        when {
            api <= latestMinus(4) -> 0xFFD31B33
            api == latestMinus(3) -> 0xFFE54B4B
            api == latestMinus(2) -> 0xFFE37A46
            api == latestMinus(1) -> 0xFF178E96
            else -> 0xFF14B572
        }

    fun versionName(api: Int): String =
        when (api) {
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
            29, 30, 31 -> "Android ${api - 19}"
            32 -> "Android 12L"
            else -> "Android ${api - 20}"
        }
}
