package com.bernaferrari.sdkmonitor.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector
import com.bernaferrari.sdkmonitor.domain.SortOption

fun SortOption.icon(): ImageVector =
    when (this) {
        SortOption.NAME -> Icons.Default.SortByAlpha
        SortOption.SDK -> Icons.Default.Android
    }