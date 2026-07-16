package com.bernaferrari.sdkmonitor.shared.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.ui.platform.AppIconProvider

/** Package-mapped mock icons, following NetGuard's web demo visual approach. */
object DemoAppIconProvider : AppIconProvider {
    @Composable
    override fun AppIcon(packageName: String, size: Dp, modifier: Modifier) {
        val (icon, color) = when (packageName) {
            "com.android.chrome", "org.mozilla.firefox" -> Icons.Default.Language to Color(0xFF4285F4)
            "com.spotify.music" -> Icons.Default.MusicNote to Color(0xFF1DB954)
            "com.whatsapp" -> Icons.AutoMirrored.Filled.Chat to Color(0xFF25D366)
            "com.instagram.android" -> Icons.Default.CameraAlt to Color(0xFFE1306C)
            "com.google.android.gms" -> Icons.Default.Extension to Color(0xFF5F6368)
            "com.android.vending" -> Icons.Default.Shop to Color(0xFF01875F)
            "com.twitter.android" -> Icons.Default.Storefront to Color(0xFF111111)
            "com.discord" -> Icons.Default.Headset to Color(0xFF5865F2)
            "com.bernaferrari.sdkmonitor" -> Icons.Default.MonitorHeart to MaterialTheme.colorScheme.primary
            "com.bank.secure" -> Icons.Default.Security to Color(0xFF1565C0)
            else -> null to Color(0xFF795548)
        }
        Surface(modifier = modifier.size(size), shape = RoundedCornerShape(size / 3), color = color) {
            Box(contentAlignment = Alignment.Center) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(size * 0.55f), tint = Color.White)
                } else {
                    Text(
                        text = packageName.substringAfterLast('.').take(1).uppercase(),
                        color = Color.White,
                        fontSize = (size.value * 0.44f).sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
