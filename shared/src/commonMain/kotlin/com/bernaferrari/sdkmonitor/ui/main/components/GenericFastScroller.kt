package com.bernaferrari.sdkmonitor.ui.main.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * A generic fast scroller that can work with any data type.
 *
 * @param items The list of items to create letters from
 * @param listState The LazyListState to control scrolling
 * @param getIndexKey Function that extracts the index key (e.g., first letter) from an item
 * @param letterToIndexMap Map of letters to their corresponding indices in the LazyColumn
 * @param scrollOffsetPx Additional offset in pixels to apply when scrolling (useful to show context above target)
 * @param modifier Modifier for the component
 * @param onLetterSelected Callback when a letter is selected
 * @param onScrollFinished Callback when scrolling interaction finishes
 * @param onInteractionStart Callback when interaction starts
 */
@Composable
fun <T> GenericFastScroller(
    modifier: Modifier = Modifier,
    items: List<T>,
    listState: LazyListState,
    getIndexKey: (T) -> String,
    letterToIndexMap: Map<String, Int>,
    scrollOffsetPx: Int = 0,
    onLetterSelected: (String) -> Unit = {},
    onScrollFinished: () -> Unit = {},
    onInteractionStart: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current

    // Get unique letters from items
    val letters =
        remember(items.hashCode()) {
            val letterSet = mutableSetOf<String>()
            var hasNonLetters = false

            items.forEach { item ->
                val key = getIndexKey(item)

                // For SDK sorting, treat all numeric strings as SDK versions
                // For NAME sorting, only treat single first characters
                if (key.all { it.isDigit() } && key.length <= 3) { // SDK versions are typically 1-3 digits
                    letterSet.add(key)
                } else {
                    val firstChar = key.firstOrNull()?.uppercaseChar()
                    if (firstChar?.isLetter() == true) {
                        letterSet.add(firstChar.toString())
                    } else {
                        hasNonLetters = true
                    }
                }
            }

            // Sort appropriately based on content type
            val result =
                if (letterSet.any { it.all { char -> char.isDigit() } }) {
                    // If we have numeric values (SDK versions), sort them numerically descending
                    letterSet.sortedByDescending { it.toIntOrNull() ?: 0 }.toMutableList()
                } else {
                    // Regular alphabetical sorting
                    letterSet.sorted().toMutableList()
                }

            // Only add "#" for alphabetical sorting when there are non-letters
            if (hasNonLetters && result.none { it.all { char -> char.isDigit() } }) {
                result.add(0, "#")
            }
            result
        }

    var isInteracting by remember { mutableStateOf(false) }
    var currentDragPosition by remember { mutableFloatStateOf(0f) }
    var scrollerSize by remember { mutableStateOf(IntSize.Zero) }
    var currentSelectedLetter by remember { mutableStateOf("") }
    var previousSelectedLetter by remember { mutableStateOf("") }

    // Auto-reset when items change
    LaunchedEffect(items.hashCode()) {
        isInteracting = false
        currentDragPosition = 0f
    }

    // Function to scroll to letter with offset
    fun scrollToLetter(letter: String) {
        letterToIndexMap[letter]?.let { index ->
            coroutineScope.launch {
                // Use immediate scroll instead of animate for faster response
                if (scrollOffsetPx > 0) {
                    listState.scrollToItem(
                        index = index,
                        scrollOffset = -scrollOffsetPx,
                    )
                } else {
                    listState.scrollToItem(index)
                }
            }
        }
    }

    // Function to handle position and select letter
    fun handlePositionAndSelectLetter(yPosition: Float) {
        if (scrollerSize.height <= 0 || letters.isEmpty()) return

        val verticalPaddingPx = with(density) { 16.dp.toPx() } // Updated to match new padding
        val usableHeight = (scrollerSize.height - (verticalPaddingPx * 2)).coerceAtLeast(1f)
        val adjustedY = (yPosition - verticalPaddingPx).coerceIn(0f, usableHeight)

        val progress = (adjustedY / usableHeight).coerceIn(0f, 1f)
        val letterIndex =
            (progress * (letters.size - 1))
                .toInt()
                .coerceIn(0, letters.size - 1)

        val selectedLetter = letters[letterIndex]

        // Trigger haptic feedback only when letter changes
        if (selectedLetter != previousSelectedLetter) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            previousSelectedLetter = selectedLetter
        }

        currentSelectedLetter = selectedLetter

        onLetterSelected(selectedLetter)
        scrollToLetter(selectedLetter)
    }

    val width = 40.dp
    val verticalInset = 16.dp
    val labelSlotHeight = 22.dp
    val minimumTrackHeight = 72.dp
    val preferredTrackHeight = maxOf(
        minimumTrackHeight,
        labelSlotHeight * letters.size + verticalInset * 2,
    )

    Box(
        modifier =
            modifier
                .height(preferredTrackHeight)
                .width(width)
                .background(
                    color = Color.Transparent, // MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(20.dp),
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(width)
                    .padding(vertical = verticalInset)
                    .onGloballyPositioned { scrollerSize = it.size }
                    .pointerInput(items.hashCode()) {
                        awaitPointerEventScope {
                            while (true) {
                                val down = awaitFirstDown()
                                onInteractionStart()
                                isInteracting = true

                                // Haptic feedback on initial touch
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                                currentDragPosition = down.position.y
                                handlePositionAndSelectLetter(down.position.y)

                                val change =
                                    awaitTouchSlopOrCancellation(down.id) { change, _ ->
                                        change.consume()
                                    }

                                if (change != null) {
                                    drag(change.id) { dragChange ->
                                        currentDragPosition =
                                            dragChange.position.y
                                                .coerceIn(0f, scrollerSize.height.toFloat())
                                        handlePositionAndSelectLetter(currentDragPosition)
                                    }
                                }
                                isInteracting = false
                                previousSelectedLetter = "" // Reset for next interaction
                                onScrollFinished()
                            }
                        }
                    },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Calculate current position once, outside the loop
            val verticalPaddingPx = with(density) { 16.dp.toPx() } // Updated to match new padding
            val usableHeight =
                (scrollerSize.height - (verticalPaddingPx * 2)).coerceAtLeast(1f)
            val adjustedDragPosition =
                (currentDragPosition - verticalPaddingPx).coerceIn(0f, usableHeight)
            val currentPosition =
                if (usableHeight > 0) {
                    (adjustedDragPosition / usableHeight).coerceIn(0f, 1f)
                } else {
                    0f
                }

            letters.forEachIndexed { index, letter ->
                val letterPosition =
                    if (letters.size > 1) {
                        index.toFloat() / (letters.size - 1)
                    } else {
                        0.5f
                    }

                val distance = abs(letterPosition - currentPosition)

                val scale by animateFloatAsState(
                    targetValue =
                        if (isInteracting) {
                            when {
                                distance < 0.05f -> 1.6f
                                distance < 0.1f -> 1.3f
                                distance < 0.15f -> 1.1f
                                else -> 0.9f
                            }
                        } else {
                            1.0f
                        },
                    animationSpec = spring(dampingRatio = 0.8f),
                    label = "letter_scale_$index",
                )

                val alpha by animateFloatAsState(
                    targetValue =
                        if (isInteracting) {
                            when {
                                distance < 0.05f -> 1.0f
                                distance < 0.1f -> 0.9f
                                distance < 0.15f -> 0.7f
                                else -> 0.5f
                            }
                        } else {
                            0.7f
                        },
                    animationSpec = spring(),
                    label = "letter_alpha_$index",
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = letter,
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = if (letter.length > 2) 9.sp else 11.sp, // Smaller font for longer numbers
                            ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier =
                            Modifier
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    alpha = alpha,
                                ),
                    )
                }
            }
        }

        // Floating letter indicator using the new component
        if (isInteracting && currentSelectedLetter.isNotEmpty()) {
            FloatingLetterIndicator(
                letter = currentSelectedLetter,
                yPosition = currentDragPosition,
            )
        }
    }
}

// Example data class for preview
data class SampleItem(
    val name: String,
    val category: String,
)


@Composable
fun GenericFastScrollerPreview() {
    MaterialTheme {
        val mockItems =
            remember {
                listOf(
                    SampleItem("Apple", "Fruit"),
                    SampleItem("Banana", "Fruit"),
                    SampleItem("Carrot", "Vegetable"),
                    SampleItem("Dog", "Animal"),
                    SampleItem("Elephant", "Animal"),
                    SampleItem("Fish", "Animal"),
                    SampleItem("Grape", "Fruit"),
                    SampleItem("Horse", "Animal"),
                    SampleItem("Ice", "Other"),
                    SampleItem("Juice", "Drink"),
                    SampleItem("Kiwi", "Fruit"),
                    SampleItem("Lemon", "Fruit"),
                    SampleItem("Mouse", "Animal"),
                    SampleItem("Notebook", "Object"),
                    SampleItem("Orange", "Fruit"),
                    SampleItem("Pen", "Object"),
                    SampleItem("Queen", "Person"),
                    SampleItem("Rabbit", "Animal"),
                    SampleItem("Sun", "Nature"),
                    SampleItem("Tree", "Nature"),
                    SampleItem("Umbrella", "Object"),
                    SampleItem("Violin", "Music"),
                    SampleItem("Water", "Drink"),
                    SampleItem("Xylophone", "Music"),
                    SampleItem("Yoga", "Activity"),
                    SampleItem("Zebra", "Animal"),
                )
            }

        val letterToIndexMap =
            remember(mockItems) {
                val mapping = mutableMapOf<String, Int>()
                val groupedItems =
                    mockItems.groupBy {
                        val firstChar = it.name.firstOrNull()?.uppercaseChar()
                        if (firstChar?.isLetter() == true) {
                            firstChar.toString()
                        } else {
                            "#"
                        }
                    }

                var currentIndex = 0
                for (letter in groupedItems.keys.sorted()) {
                    val itemsInSection = groupedItems.getValue(letter)
                    mapping[letter] = currentIndex
                    currentIndex += 1 + itemsInSection.size
                }
                mapping
            }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            GenericFastScroller(
                items = mockItems,
                listState = rememberLazyListState(),
                getIndexKey = { it.name },
                letterToIndexMap = letterToIndexMap,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}


@Composable
fun SimpleGenericFastScrollerPreview() {
    MaterialTheme {
        val simpleItems =
            remember {
                listOf(
                    "Apple",
                    "Banana",
                    "Cherry",
                    "Date",
                    "Elderberry",
                    "Fig",
                    "Grape",
                    "Honeydew",
                    "Ice cream",
                    "Jackfruit",
                    "Kiwi",
                    "Lemon",
                    "Mango",
                    "Nectarine",
                    "Orange",
                    "Papaya",
                    "Quince",
                    "Raspberry",
                    "Strawberry",
                    "Tangerine",
                    "Ugli fruit",
                    "Vanilla",
                    "Watermelon",
                    "Ximenia",
                    "Yam",
                    "Zucchini",
                )
            }

        val letterToIndexMap =
            remember(simpleItems) {
                val mapping = mutableMapOf<String, Int>()
                val groupedItems =
                    simpleItems.groupBy {
                        it.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
                    }

                var currentIndex = 0
                for (letter in groupedItems.keys.sorted()) {
                    val itemsInSection = groupedItems.getValue(letter)
                    mapping[letter] = currentIndex
                    currentIndex += itemsInSection.size
                }
                mapping
            }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            GenericFastScroller(
                items = simpleItems,
                listState = rememberLazyListState(),
                getIndexKey = { it },
                letterToIndexMap = letterToIndexMap,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}
