package com.bernaferrari.sdkmonitor.ui.platform

import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Platform injects real app icons (Coil + PackageManager on Android).
 * Default is a generic placeholder for previews and non-Android targets.
 */
fun interface AppIconProvider {
    @Composable
    fun AppIcon(
        packageName: String,
        size: Dp,
        modifier: Modifier,
    )
}

object PlaceholderAppIconProvider : AppIconProvider {
    @Composable
    override fun AppIcon(
        packageName: String,
        size: Dp,
        modifier: Modifier,
    ) {
        Box(
            modifier = modifier.size(size),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = MaterialSymbols.Outlined.Apps,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size * 0.6f),
            )
        }
    }
}

val LocalAppIconProvider =
    staticCompositionLocalOf<AppIconProvider> { PlaceholderAppIconProvider }

@Composable
fun PlatformAppIcon(
    packageName: String,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
) {
    LocalAppIconProvider.current.AppIcon(packageName, size, modifier)
}
