package com.bernaferrari.sdkmonitor.shared.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.ui.platform.AppIconProvider

/** Distinct, deterministic app identities for the Room-backed desktop and web demo. */
object DemoAppIconProvider : AppIconProvider {
    @Composable
    override fun AppIcon(packageName: String, size: Dp, modifier: Modifier) {
        val (label, color) = when (packageName) {
            "com.android.chrome" -> "C" to Color(0xFF4285F4)
            "com.spotify.music" -> "S" to Color(0xFF1DB954)
            "com.whatsapp" -> "W" to Color(0xFF25D366)
            "com.instagram.android" -> "I" to Color(0xFFE1306C)
            "com.google.android.gms" -> "G" to Color(0xFF34A853)
            "com.android.vending" -> "P" to Color(0xFF01875F)
            "org.mozilla.firefox" -> "F" to Color(0xFFFF7139)
            "com.twitter.android" -> "X" to Color(0xFF111111)
            "com.discord" -> "D" to Color(0xFF5865F2)
            "com.bernaferrari.sdkmonitor" -> "S" to MaterialTheme.colorScheme.primary
            else -> packageName.substringAfterLast('.').take(1).uppercase() to Color(0xFF795548)
        }
        Box(
            modifier = modifier.size(size).clip(RoundedCornerShape(size / 3)).background(color),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = (size.value * 0.44f).sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}
