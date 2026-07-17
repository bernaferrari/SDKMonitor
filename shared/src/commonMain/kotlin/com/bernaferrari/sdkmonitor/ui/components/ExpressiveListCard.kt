package com.bernaferrari.sdkmonitor.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val SelectionAnimationDurationMillis = 150

enum class ExpressiveListItemPosition {
    First,
    Middle,
    Last,
    Single,
}

fun expressiveListItemPosition(index: Int, lastIndex: Int): ExpressiveListItemPosition =
    when {
        lastIndex == 0 -> ExpressiveListItemPosition.Single
        index == 0 -> ExpressiveListItemPosition.First
        index == lastIndex -> ExpressiveListItemPosition.Last
        else -> ExpressiveListItemPosition.Middle
    }

@Composable
fun ExpressiveListCard(
    isSelected: Boolean,
    position: ExpressiveListItemPosition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    endPadding: Dp = 16.dp,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val baseCornerRadii = position.cornerRadii()
    val selectionTransition = updateTransition(isSelected, label = "expressiveListCardSelection")
    val topStartRadius by selectionTransition.animatedRadius("topStartRadius", baseCornerRadii.topStart)
    val topEndRadius by selectionTransition.animatedRadius("topEndRadius", baseCornerRadii.topEnd)
    val bottomEndRadius by selectionTransition.animatedRadius("bottomEndRadius", baseCornerRadii.bottomEnd)
    val bottomStartRadius by selectionTransition.animatedRadius("bottomStartRadius", baseCornerRadii.bottomStart)
    val cardColor by
        selectionTransition.animateColor(
            transitionSpec = { tween(durationMillis = SelectionAnimationDurationMillis) },
            label = "containerColor",
        ) { selected ->
            if (selected) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
        }
    val borderColor by
        selectionTransition.animateColor(
            transitionSpec = { tween(durationMillis = SelectionAnimationDurationMillis) },
            label = "borderColor",
        ) { selected ->
            if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
            } else {
                Color.Transparent
            }
        }

    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = endPadding)
                .padding(
                    top =
                        if (position == ExpressiveListItemPosition.First || position == ExpressiveListItemPosition.Single) {
                            0.dp
                        } else {
                            2.dp
                        },
                ).semantics { selected = isSelected },
        shape =
            RoundedCornerShape(
                topStart = topStartRadius,
                topEnd = topEndRadius,
                bottomEnd = bottomEndRadius,
                bottomStart = bottomStartRadius,
            ),
        color = cardColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(2.dp, borderColor),
    ) {
        Row(
            modifier =
                Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                        role = Role.Button,
                        onClick = onClick,
                    ).fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
private fun androidx.compose.animation.core.Transition<Boolean>.animatedRadius(
    label: String,
    restingRadius: Dp,
) = animateDp(
    transitionSpec = { tween(durationMillis = SelectionAnimationDurationMillis) },
    label = label,
) { selected ->
    if (selected) 20.dp else restingRadius
}

private data class CornerRadii(
    val topStart: Dp,
    val topEnd: Dp,
    val bottomEnd: Dp,
    val bottomStart: Dp,
)

private fun ExpressiveListItemPosition.cornerRadii(): CornerRadii =
    when (this) {
        ExpressiveListItemPosition.Single -> CornerRadii(16.dp, 16.dp, 16.dp, 16.dp)
        ExpressiveListItemPosition.First -> CornerRadii(16.dp, 16.dp, 4.dp, 4.dp)
        ExpressiveListItemPosition.Last -> CornerRadii(4.dp, 4.dp, 16.dp, 16.dp)
        ExpressiveListItemPosition.Middle -> CornerRadii(4.dp, 4.dp, 4.dp, 4.dp)
    }
