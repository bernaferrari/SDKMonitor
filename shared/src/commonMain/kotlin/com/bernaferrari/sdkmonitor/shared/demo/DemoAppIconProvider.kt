package com.bernaferrari.sdkmonitor.shared.demo

import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        val style = when (packageName) {
            "com.android.chrome" -> DemoIconStyle(MaterialSymbols.Filled.Language, background = Color(0xFF4285F4))
            "org.mozilla.firefox" -> DemoIconStyle(MaterialSymbols.Filled.Language, background = Color(0xFFFF7139))
            "com.google.android.youtube" -> DemoIconStyle(MaterialSymbols.Filled.PlayCircle, background = Color(0xFFFF0000))
            "com.google.android.gm" -> DemoIconStyle(MaterialSymbols.Filled.Email, background = Color(0xFFEA4335))
            "com.google.android.apps.maps" -> DemoIconStyle(MaterialSymbols.Filled.Place, background = Color(0xFF18884B))
            "com.spotify.music" ->
                DemoIconStyle(
                    icon = MaterialSymbols.Filled.MusicNote,
                    background = Color(0xFF1ED760),
                    foreground = Color(0xFF08150D),
                )
            "com.whatsapp" -> DemoIconStyle(MaterialSymbols.Filled.Chat, background = Color(0xFF128C4A))
            "com.instagram.android" -> DemoIconStyle(MaterialSymbols.Filled.CameraAlt, background = Color(0xFFC72C69))
            "com.google.android.gms" -> DemoIconStyle(MaterialSymbols.Filled.Extension, background = Color(0xFF5F6368))
            "com.android.vending" -> DemoIconStyle(MaterialSymbols.Filled.Shop, background = Color(0xFF01875F))
            "com.twitter.android" -> DemoIconStyle(label = "X", background = Color(0xFF111111))
            "com.discord" -> DemoIconStyle(MaterialSymbols.Filled.Headset, background = Color(0xFF5865F2))
            "com.bernaferrari.sdkmonitor" ->
                DemoIconStyle(MaterialSymbols.Filled.MonitorHeart, background = MaterialTheme.colorScheme.primary)
            "org.thoughtcrime.securesms" -> DemoIconStyle(MaterialSymbols.Filled.Chat, background = Color(0xFF3A76F0))
            "com.nu.production" -> DemoIconStyle(label = "Nu", background = Color(0xFF820AD1))
            "com.google.android.apps.photos" ->
                DemoIconStyle(MaterialSymbols.Filled.CameraAlt, background = Color(0xFF1769E0))
            "com.reddit.frontpage" -> DemoIconStyle(MaterialSymbols.Filled.Chat, background = Color(0xFFD93A00))
            "com.linkedin.android" -> DemoIconStyle(label = "in", background = Color(0xFF0A66C2))
            else -> DemoIconStyle(label = packageName.substringAfterLast('.').take(1).uppercase(), background = Color(0xFF546E7A))
        }
        Surface(modifier = modifier.size(size), shape = RoundedCornerShape(size / 3), color = style.background) {
            Box(contentAlignment = Alignment.Center) {
                if (style.icon != null) {
                    Icon(
                        style.icon,
                        contentDescription = null,
                        modifier = Modifier.size(size * 0.55f),
                        tint = style.foreground,
                    )
                } else {
                    Text(
                        text = style.label.orEmpty(),
                        color = style.foreground,
                        fontSize = (size.value * if (style.label?.length == 1) 0.44f else 0.34f).sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private data class DemoIconStyle(
    val icon: ImageVector? = null,
    val label: String? = null,
    val background: Color,
    val foreground: Color = Color.White,
)
