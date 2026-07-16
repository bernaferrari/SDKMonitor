package com.bernaferrari.sdkmonitor.domain.logic

/**
 * Diacritic-insensitive lowercase normalize for search.
 * Uses a simple NFD-style strip without java.text (commonMain-safe).
 */
object StringNormalize {
    private val combiningMarks = Regex("\\p{Mn}+")

    fun normalize(input: String): String =
        input
            .lowercase()
            .let { stripCombining(it) }

    private fun stripCombining(s: String): String =
        buildString(s.length) {
            for (ch in s) {
                if (!combiningMarks.matches(ch.toString()) && ch.category != CharCategory.NON_SPACING_MARK) {
                    append(ch)
                }
            }
        }.let { result ->
            // Fast path: most Latin diacritics decompose; also map common precomposed leftovers.
            result
                .replace('à', 'a').replace('á', 'a').replace('â', 'a').replace('ã', 'a').replace('ä', 'a').replace('å', 'a')
                .replace('è', 'e').replace('é', 'e').replace('ê', 'e').replace('ë', 'e')
                .replace('ì', 'i').replace('í', 'i').replace('î', 'i').replace('ï', 'i')
                .replace('ò', 'o').replace('ó', 'o').replace('ô', 'o').replace('õ', 'o').replace('ö', 'o')
                .replace('ù', 'u').replace('ú', 'u').replace('û', 'u').replace('ü', 'u')
                .replace('ý', 'y').replace('ÿ', 'y')
                .replace('ñ', 'n').replace('ç', 'c')
        }
}
