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
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.bernaferrari.sdkmonitor.domain.ColorStyle
import com.bernaferrari.sdkmonitor.domain.ThemeMode
import com.bernaferrari.sdkmonitor.domain.ThemePalette
import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols

@Composable
fun ThemeAppearanceSelector(
    selectedMode: ThemeMode,
    selectedPalette: ThemePalette,
    availablePalettes: List<ThemePalette>,
    onModeSelected: (ThemeMode) -> Unit,
    onPaletteSelected: (ThemePalette) -> Unit,
    modifier: Modifier = Modifier,
    selectedStyle: ColorStyle = ColorStyle.TONAL,
    onStyleSelected: (ColorStyle) -> Unit = {},
) {
    // Connected items, like the app's lists: theme mode, color, then (for the app's own colors) their style.
    // Round outside, tight where they meet. Wallpaper colors come as Android gives them, so the style row
    // slides away while they are chosen, the same rule as every app of the family.
    val item = MaterialTheme.colorScheme.surfaceContainerLow
    val seed = selectedPalette.seedArgb?.let { Color(it) }
    val bottom by animateDpAsState(if (seed != null) 6.dp else 24.dp, label = "colorsBottomCorner")
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(item, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 6.dp, bottomEnd = 6.dp))
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            ThemeMode.entries.forEachIndexed { index, mode ->
                val (label, icon) = mode.labelAndIcon()
                ToggleButton(
                    checked = mode == selectedMode,
                    onCheckedChange = { checked ->
                        if (checked && mode != selectedMode) onModeSelected(mode)
                    },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            ThemeMode.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                    // The family standard (Calcula, WiFi Weakspot, QuietGuard, Wordkeep, Keepout): the chosen
                    // mode solid in primary with a check, the others raised off the card.
                    colors =
                        ToggleButtonDefaults.colors(
                            checkedContainerColor = MaterialTheme.colorScheme.primary,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                ) {
                    // Like a segmented button: the chosen mode trades its icon for a check.
                    Icon(
                        if (mode == selectedMode) MaterialSymbols.Filled.Check else icon,
                        contentDescription = null,
                        modifier = Modifier.size(ToggleButtonDefaults.IconSize),
                    )
                    Spacer(modifier = Modifier.size(ToggleButtonDefaults.IconSpacing))
                    Text(label, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        FlowRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(item, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = bottom, bottomEnd = bottom))
                    .padding(12.dp),
            // Wallpaper and six colors in one row on any phone: the 48dp touch targets sit side by side, and their
            // 40dp swatches leave the gap between them.
            horizontalArrangement = Arrangement.Center,
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
        // The style row unfolds downward from the colors above it, across its full width, and folds back up.
        androidx.compose.animation.AnimatedVisibility(
            visible = seed != null,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            ColorStylePicker(
                selected = selectedStyle,
                seedColor = seed ?: MaterialTheme.colorScheme.primary,
                dark = dark,
                onSelect = onStyleSelected,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(item, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
                        .padding(12.dp),
            )
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
    val innerRadius = (cornerRadius - borderWidth - 2.dp).coerceAtLeast(0.dp)

    Box(
        modifier =
            Modifier
                .size(48.dp)
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
                    .size(40.dp)
                    .rotate(rotation)
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
                    imageVector = MaterialSymbols.Filled.Palette,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).rotate(-rotation),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Icon(
                imageVector = MaterialSymbols.Filled.Check,
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
        ThemeMode.SYSTEM -> "System" to MaterialSymbols.Filled.BrightnessAuto
        ThemeMode.LIGHT -> "Light" to MaterialSymbols.Filled.LightMode
        ThemeMode.DARK -> "Dark" to MaterialSymbols.Filled.DarkMode
    }

private fun ThemePalette.displayName(): String =
    when (this) {
        ThemePalette.DYNAMIC -> "Wallpaper"
        ThemePalette.EMBER -> "Ember"
        ThemePalette.SOLAR -> "Solar"
        ThemePalette.GROVE -> "Grove"
        ThemePalette.AZURE -> "Azure"
        ThemePalette.ORCHID -> "Orchid"
        ThemePalette.BERRY -> "Berry"
    }
