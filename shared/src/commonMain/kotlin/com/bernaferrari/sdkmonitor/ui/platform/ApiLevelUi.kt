package com.bernaferrari.sdkmonitor.ui.platform

import androidx.compose.ui.graphics.Color
import com.bernaferrari.sdkmonitor.domain.logic.ApiLevel

fun Int.apiToComposeColor(): Color = Color(ApiLevel.colorArgb(this))

fun Int.apiToVersionName(): String = ApiLevel.versionName(this)
