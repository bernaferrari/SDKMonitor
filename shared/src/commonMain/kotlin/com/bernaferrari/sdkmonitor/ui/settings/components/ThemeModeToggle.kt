@file:OptIn(
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
    androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class,
)

package com.bernaferrari.sdkmonitor.ui.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.domain.ThemePalette

@Composable
fun ThemeAppearanceSelector(
    selectedMode: ThemeMode,
    selectedPalette: ThemePalette,
    availablePalettes: List<ThemePalette>,
    onModeSelected: (ThemeMode) -> Unit,
    onPaletteSelected: (ThemePalette) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            ThemeMode.entries.forEachIndexed { index, mode ->
                val (label, icon) = mode.labelAndIcon()
                ToggleButton(
                    checked = mode == selectedMode,
                    onCheckedChange = { checked ->
                        if (checked && mode != selectedMode) onModeSelected(mode)
                    },
                    modifier = Modifier.weight(1f),
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            ThemeMode.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                    colors =
                        ToggleButtonDefaults.toggleButtonColors(
                            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(ToggleButtonDefaults.IconSize))
                    Spacer(modifier = Modifier.size(ToggleButtonDefaults.IconSpacing))
                    Text(label, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            availablePalettes.forEach { palette ->
                ThemeColorItem(
                    palette = palette,
                    isSelected = palette == selectedPalette,
                    onClick = { onPaletteSelected(palette) },
                )
            }
        }
    }
}

@Composable
private fun ThemeColorItem(
    palette: ThemePalette,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val displayColor =
        if (palette == ThemePalette.DYNAMIC) {
            MaterialTheme.colorScheme.primary
        } else {
            Color(requireNotNull(palette.seedArgb))
        }
    val swatchBrush =
        remember(displayColor) {
            Brush.linearGradient(
                colors =
                    listOf(
                        lerp(displayColor, Color.White, 0.16f),
                        displayColor,
                        lerp(displayColor, Color.Black, 0.10f),
                    ),
            )
        }
    val checkColor =
        when {
            palette == ThemePalette.DYNAMIC -> MaterialTheme.colorScheme.onPrimary
            displayColor.luminance() > 0.24f -> Color.Black.copy(alpha = 0.78f)
            else -> Color.White
        }
    val cornerRadius by
        animateDpAsState(
            targetValue = if (isSelected) 8.dp else 18.dp,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            label = "${palette.token}CornerRadius",
        )
    val elevation by
        animateDpAsState(
            targetValue = if (isSelected) 6.dp else 1.dp,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            label = "${palette.token}Elevation",
        )
    val borderProgress by
        animateFloatAsState(
            targetValue = if (isSelected) 1f else 0f,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            label = "${palette.token}BorderProgress",
        )
    val borderColor by
        animateColorAsState(
            targetValue =
                if (isSelected) {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                } else {
                    Color.Transparent
                },
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            label = "${palette.token}BorderColor",
        )
    val rotation by
        animateFloatAsState(
            targetValue = if (isSelected) 0f else 45f,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            label = "${palette.token}Rotation",
        )
    val checkProgress by
        animateFloatAsState(
            targetValue = if (isSelected) 1f else 0f,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            label = "${palette.token}CheckProgress",
        )
    val borderWidth = (2.dp * borderProgress).coerceAtLeast(0.dp)
    val safeElevation = elevation.coerceAtLeast(0.dp)
    val innerRadius = (cornerRadius - borderWidth - 2.dp).coerceAtLeast(0.dp)

    Box(
        modifier =
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        if (!isSelected) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClick()
                        }
                    },
                ).semantics {
                    role = Role.RadioButton
                    selected = isSelected
                    contentDescription = palette.displayName()
                },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(36.dp)
                    .rotate(rotation)
                    .shadow(elevation = safeElevation, shape = RoundedCornerShape(cornerRadius), clip = false)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(swatchBrush)
                    .padding(borderWidth)
                    .clip(RoundedCornerShape((cornerRadius - borderWidth).coerceAtLeast(0.dp)))
                    .background(borderColor)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(innerRadius))
                    .background(swatchBrush),
            contentAlignment = Alignment.Center,
        ) {
            if (palette == ThemePalette.DYNAMIC && !isSelected) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).rotate(-rotation),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(18.dp)
                        .rotate(-rotation)
                        .graphicsLayer {
                            val safeProgress = checkProgress.coerceAtLeast(0f)
                            scaleX = safeProgress
                            scaleY = safeProgress
                            alpha = checkProgress.coerceIn(0f, 1f)
                        },
                tint = checkColor,
            )
        }
    }
}

private fun ThemeMode.labelAndIcon(): Pair<String, ImageVector> =
    when (this) {
        ThemeMode.SYSTEM -> "System" to Icons.Default.BrightnessAuto
        ThemeMode.LIGHT -> "Light" to Icons.Default.LightMode
        ThemeMode.DARK -> "Dark" to Icons.Default.DarkMode
    }

private fun ThemePalette.displayName(): String =
    when (this) {
        ThemePalette.DYNAMIC -> "Wallpaper"
        ThemePalette.EMBER -> "Ember"
        ThemePalette.CLAY -> "Clay"
        ThemePalette.SOLAR -> "Solar"
        ThemePalette.CITRINE -> "Citrine"
        ThemePalette.GROVE -> "Grove"
        ThemePalette.LAGOON -> "Lagoon"
        ThemePalette.TIDE -> "Tide"
        ThemePalette.AZURE -> "Azure"
        ThemePalette.ORCHID -> "Orchid"
        ThemePalette.BERRY -> "Berry"
    }
