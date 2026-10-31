package com.bernaferrari.sdkmonitor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.ui.platform.apiContainerColor
import com.bernaferrari.sdkmonitor.ui.platform.apiContentColor
import com.bernaferrari.sdkmonitor.ui.platform.apiToVersionName

/**
 * The target SDK: its level inside the level's shape (a sun when current, down to a triangle when far behind),
 * in the level's color, with the Android version under it. A fixed minimum width keeps a list's badges in one
 * column.
 */
@Composable
fun SdkVersionBadge(
    sdkVersion: Int,
    modifier: Modifier = Modifier,
) {
    val color = sdkVersion.apiContentColor()
    Column(
        modifier = modifier.widthIn(min = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        SdkSeal(
            level = sdkVersion,
            size = 44.dp,
            fill = sdkVersion.apiContainerColor(strong = true),
            textColor = color,
            textStyle = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = sdkVersion.apiToVersionName(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
        )
    }
}
