package com.bernaferrari.sdkmonitor.ui.main

import androidx.compose.ui.graphics.vector.ImageVector

import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols

import com.bernaferrari.sdkmonitor.domain.SortOption

fun SortOption.icon(): ImageVector =
    when (this) {
        SortOption.NAME -> MaterialSymbols.Filled.SortByAlpha
        SortOption.SDK -> MaterialSymbols.Filled.Android
    }