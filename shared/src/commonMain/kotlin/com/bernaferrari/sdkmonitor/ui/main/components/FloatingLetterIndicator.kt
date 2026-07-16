package com.bernaferrari.sdkmonitor.ui.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun FloatingLetterIndicator(
    letter: String,
    yPosition: Float,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val indicatorSize = 80.dp

    Surface(
        modifier =
            modifier
                .size(indicatorSize)
                .aspectRatio(1f)
                .offset {
                    IntOffset(
                        x = with(density) { (-60).dp.roundToPx() }, // Position to the left of the scroller
                        y = (yPosition - with(density) { (indicatorSize / 2).toPx() }).toInt(), // Center vertically on touch point
                    )
                },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary,
    ) {
        Text(
            text = letter,
            style =
                MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    lineHeightStyle =
                        LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Proportional,
                            trim = LineHeightStyle.Trim.Both,
                        ),
                ),
            modifier =
                Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
        )
    }
}


@Composable
private fun FloatingLetterIndicatorPreview() {
    MaterialTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            FloatingLetterIndicator(
                letter = "M",
                yPosition = 100f,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}


@Composable
private fun FloatingLetterIndicatorVariationsPreview() {
    MaterialTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            // Different letters at different positions
            FloatingLetterIndicator(
                letter = "A",
                yPosition = 50f,
                modifier = Modifier.align(Alignment.TopCenter),
            )

            FloatingLetterIndicator(
                letter = "#",
                yPosition = 100f,
                modifier = Modifier.align(Alignment.Center),
            )

            FloatingLetterIndicator(
                letter = "Z",
                yPosition = 150f,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
