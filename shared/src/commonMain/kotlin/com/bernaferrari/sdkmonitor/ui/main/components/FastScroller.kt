package com.bernaferrari.sdkmonitor.ui.main.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.SortOption

@Composable
fun FastScroller(
    modifier: Modifier = Modifier,
    apps: List<AppVersion>,
    listState: LazyListState,
    appFilter: AppFilter,
    sortOption: SortOption, // Add sortOption parameter
    scrollOffsetDp: Int = 60, // Default 60dp offset as requested
) {
    val density = LocalDensity.current
    val scrollOffsetPx =
        remember(scrollOffsetDp) {
            with(density) { scrollOffsetDp.dp.toPx().toInt() }
        }

    // Create mapping based on sort option
    val letterToIndexMap =
        remember(apps, appFilter, sortOption, apps.hashCode()) {
            val mapping = mutableMapOf<String, Int>()

            when (sortOption) {
                SortOption.NAME -> {
                    // Group apps by first letter (existing logic)
                    val groupedApps =
                        apps.groupBy {
                            val firstChar = it.title.firstOrNull()?.uppercaseChar()
                            if (firstChar?.isLetter() == true) {
                                firstChar.toString()
                            } else {
                                "#"
                            }
                        }

                    var currentIndex = 0
                    for (letter in groupedApps.keys.sorted()) {
                        val appsInSection = groupedApps.getValue(letter)
                        mapping[letter] = currentIndex
                        currentIndex += 1 + appsInSection.size // header + apps
                    }
                }

                SortOption.SDK -> {
                    // Group apps by SDK version - use actual SDK numbers, not strings
                    val groupedApps =
                        apps.groupBy { app ->
                            app.sdkVersion.toString()
                        }

                    var currentIndex = 0
                    val sortedSdkKeys =
                        groupedApps.keys.sortedByDescending { it.toIntOrNull() ?: 0 }
                    for (sdkVersion in sortedSdkKeys) {
                        val appsInSection = groupedApps.getValue(sdkVersion)
                        mapping[sdkVersion] = currentIndex
                        currentIndex += 1 + appsInSection.size // header + apps
                    }
                }
            }
            mapping
        }

    GenericFastScroller(
        items = apps,
        listState = listState,
        getIndexKey = { app ->
            when (sortOption) {
                SortOption.NAME -> {
                    // For NAME sorting, return the first character or "#" for non-letters
                    val firstChar = app.title.firstOrNull()?.uppercaseChar()
                    if (firstChar?.isLetter() == true) {
                        firstChar.toString()
                    } else {
                        "#"
                    }
                }

                SortOption.SDK -> {
                    app.sdkVersion.toString()
                }
            }
        },
        letterToIndexMap = letterToIndexMap,
        scrollOffsetPx = scrollOffsetPx,
        modifier = modifier,
    )
}
