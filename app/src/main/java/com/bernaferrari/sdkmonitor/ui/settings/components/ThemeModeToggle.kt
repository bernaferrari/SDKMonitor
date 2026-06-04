package com.bernaferrari.sdkmonitor.ui.settings.components

import android.graphics.Color
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.model.ThemeMode
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme

@Composable
fun ThemeModeToggle(
    themeMode: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Animate corner radius: 16.dp (rounded) -> 32.dp (circular)
    val cornerRadius by animateDpAsState(
        targetValue = if (isSelected) 32.dp else 16.dp,
        animationSpec = tween(durationMillis = 300),
        label = "cornerRadius",
    )

    // Optionally animate border width for extra feedback
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 300),
        label = "borderWidth",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedCard(
            onClick = onClick,
            modifier = Modifier.height(64.dp),
            shape = RoundedCornerShape(cornerRadius),
            colors =
                CardDefaults.outlinedCardColors(
                    containerColor =
                        if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                ),
            border =
                BorderStroke(
                    width = borderWidth,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.inversePrimary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isSelected) themeMode.selectedIcon else themeMode.icon,
                    contentDescription = stringResource(themeMode.displayNameRes),
                    modifier = Modifier.size(24.dp),
                    tint =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(themeMode.displayNameRes),
            style = MaterialTheme.typography.labelMedium,
            color =
                if (isSelected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Preview(showBackground = true, backgroundColor = Color.WHITE.toLong())
@Composable
private fun ThemeOptionPreview() {
    SDKMonitorTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ThemeModeToggle(
                themeMode = ThemeMode.MATERIAL_YOU,
                isSelected = true,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            ThemeModeToggle(
                themeMode = ThemeMode.LIGHT,
                isSelected = false,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemeOptionDarkPreview() {
    SDKMonitorTheme(darkTheme = true) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ThemeModeToggle(
                themeMode = ThemeMode.DARK,
                isSelected = true,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            ThemeModeToggle(
                themeMode = ThemeMode.SYSTEM,
                isSelected = false,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
}
