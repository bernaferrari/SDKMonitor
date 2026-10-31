package com.bernaferrari.sdkmonitor.ui.theme

import com.bernaferrari.sdkmonitor.domain.ColorStyle
import com.materialkolor.PaletteStyle

/** The Material palette style behind each [ColorStyle]. */
fun ColorStyle.paletteStyle(): PaletteStyle =
    when (this) {
        ColorStyle.EXPRESSIVE -> PaletteStyle.Expressive
        ColorStyle.VIBRANT -> PaletteStyle.Vibrant
        ColorStyle.TONAL -> PaletteStyle.TonalSpot
        ColorStyle.CMF -> PaletteStyle.Cmf()
    }
