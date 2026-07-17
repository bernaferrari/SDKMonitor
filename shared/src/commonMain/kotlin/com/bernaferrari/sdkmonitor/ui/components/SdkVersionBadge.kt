package com.bernaferrari.sdkmonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.ui.platform.apiToComposeColor
import com.bernaferrari.sdkmonitor.ui.platform.apiToVersionName

@Composable
fun SdkVersionBadge(
    sdkVersion: Int,
    modifier: Modifier = Modifier,
) {
    val color = sdkVersion.apiToComposeColor()
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier =
            modifier
                .clip(shape)
                .background(color.copy(alpha = 0.07f))
                .border(1.dp, color, shape)
                .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = sdkVersion.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = color,
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
}
