package com.bernaferrari.sdkmonitor.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.AppFilter
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.SortOption
import com.bernaferrari.sdkmonitor.ui.main.components.FastScroller
import com.bernaferrari.sdkmonitor.ui.main.components.FloatingLetterIndicator
import com.bernaferrari.sdkmonitor.ui.main.components.MainAppCard
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings
import com.bernaferrari.sdkmonitor.ui.state.MainUiState

@Composable
fun MainContent(
    uiState: MainUiState,
    searchQuery: String,
    appFilter: AppFilter,
    sortOption: SortOption,
    isFirstSync: Boolean = false,
    syncProgress: Float = 0f,
    selectedPackageName: String? = null,
    onSearchQueryChange: (String) -> Unit,
    onAppFilterChange: (AppFilter) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    onAppClick: (String) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit = {},
    contentModifier: Modifier = Modifier,
) {
    val s = sdkStrings()
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var isScrollerActive by remember { mutableStateOf(false) }
    var currentScrollLetter by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    Column(modifier = contentModifier.fillMaxSize()) {
        if (isFirstSync && syncProgress in 0f..1f) {
            LinearProgressIndicator(
                progress = { syncProgress },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = s.syncing,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(s.appName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (uiState is MainUiState.Success) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        ) {
                            Text(
                                text = "${uiState.filteredApps.size} ${s.appsCount}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(s.searchApps, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = s.search, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = s.clear)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp, topEnd = 10.dp, bottomEnd = 10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )

                Box {
                    IconButton(
                        onClick = { showFilterMenu = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.width(48.dp).height(56.dp),
                    ) {
                        Icon(
                            imageVector =
                                when (appFilter) {
                                    AppFilter.ALL_APPS -> Icons.Default.Apps
                                    AppFilter.USER_APPS -> Icons.Default.Person
                                    AppFilter.SYSTEM_APPS -> Icons.Default.Android
                                },
                            contentDescription = s.appFilter,
                        )
                    }
                    DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }, shape = RoundedCornerShape(12.dp), containerColor = MaterialTheme.colorScheme.surface) {
                        Text(s.appFilter, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                        AppFilter.entries.forEach { filter ->
                            val label = when (filter) { AppFilter.ALL_APPS -> s.allApps; AppFilter.USER_APPS -> s.userApps; AppFilter.SYSTEM_APPS -> s.systemApps }
                            DropdownMenuItem(
                                text = { Text(label, fontWeight = if (appFilter == filter) FontWeight.Bold else FontWeight.Normal, color = if (appFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) },
                                onClick = { onAppFilterChange(filter); showFilterMenu = false },
                            )
                        }
                    }
                }

                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp, topEnd = 20.dp, bottomEnd = 20.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.width(48.dp).height(56.dp),
                    ) {
                        Icon(sortOption.icon(), contentDescription = null)
                    }
                    DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }, shape = RoundedCornerShape(12.dp), containerColor = MaterialTheme.colorScheme.surface) {
                        Text("Sort by", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                        SortOption.entries.forEach { option ->
                            val label = if (option == SortOption.NAME) s.sortByName else s.sortBySdk
                            DropdownMenuItem(
                                text = { Text(label, fontWeight = if (sortOption == option) FontWeight.Bold else FontWeight.Normal, color = if (sortOption == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) },
                                onClick = { onSortOptionChange(option); showSortMenu = false },
                            )
                        }
                    }
                }

            }
            }
        }

        when (uiState) {
            is MainUiState.Loading -> LoadingBody(isFirstSync, syncProgress)
            is MainUiState.Error -> ErrorBody(message = uiState.message, retryLabel = s.retry, onRetry = onRetry)
            is MainUiState.Success -> {
                if (uiState.filteredApps.isEmpty()) {
                    EmptyBody(title = s.noAppsFound, subtitle = s.noAppsSubtitle)
                } else {
                    val groupedByName: List<Pair<String, List<AppVersion>>> = remember(uiState.filteredApps, sortOption, searchQuery) {
                        if (sortOption == SortOption.NAME && searchQuery.isBlank()) {
                            uiState.filteredApps
                                .groupBy { app -> app.title.firstOrNull()?.uppercaseChar()?.let { char -> if (char.isLetter()) char.toString() else "#" } ?: "#" }
                                .entries
                                .sortedBy { entry -> entry.key }
                                .map { entry -> entry.key to entry.value }
                        } else {
                            emptyList()
                        }
                    }
                    val groupedBySdk: List<Pair<String, List<AppVersion>>> = remember(uiState.filteredApps, sortOption, searchQuery) {
                        if (sortOption == SortOption.SDK && searchQuery.isBlank()) {
                            uiState.filteredApps
                                .groupBy { app -> "SDK ${app.sdkVersion}" }
                                .entries
                                .sortedByDescending { entry -> entry.key.removePrefix("SDK ").toIntOrNull() ?: 0 }
                                .map { entry -> entry.key to entry.value }
                        } else {
                            emptyList()
                        }
                    }
                    val showFastScroller = uiState.filteredApps.size > 15 && searchQuery.isBlank()
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                            val groups: List<Pair<String, List<AppVersion>>> = if (groupedByName.isNotEmpty()) groupedByName else groupedBySdk
                            if (groups.isNotEmpty()) {
                                groups.forEach { (header, apps) ->
                                    item(key = "header_$header") {
                                        Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(12.dp), color = if (groupedBySdk.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceContainer) {
                                            Text(header, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                                        }
                                    }
                                    itemsIndexed(apps, key = { _, app -> "${header}_${app.packageName}" }) { index, app ->
                                        MainAppCard(appVersion = app, searchQuery = searchQuery, isSelected = app.packageName == selectedPackageName, isLast = index == apps.lastIndex, onClick = { onAppClick(app.packageName) })
                                    }
                                }
                            } else {
                                itemsIndexed(uiState.filteredApps, key = { _, app -> app.packageName }) { index, app ->
                                    MainAppCard(appVersion = app, searchQuery = searchQuery, isSelected = app.packageName == selectedPackageName, isLast = index == uiState.filteredApps.lastIndex, onClick = { onAppClick(app.packageName) })
                                }
                            }
                        }

                        if (showFastScroller) {
                            FastScroller(
                                modifier = Modifier.align(Alignment.CenterEnd), apps = uiState.filteredApps, listState = listState, appFilter = appFilter, sortOption = sortOption, scrollOffsetDp = 80,
                                onLetterSelected = { currentScrollLetter = it; isScrollerActive = true },
                                onScrollFinished = { isScrollerActive = false; currentScrollLetter = "" },
                                onInteractionStart = { isScrollerActive = true },
                            )
                        }

                        if (isScrollerActive && currentScrollLetter.isNotEmpty()) {
                            FloatingLetterIndicator(
                                letter = currentScrollLetter,
                                yPosition = 0f,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingBody(isFirstSync: Boolean, syncProgress: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp), color = MaterialTheme.colorScheme.primary)
            if (isFirstSync && syncProgress in 0f..1f) {
                Text("Setting up your apps", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Syncing apps", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LinearProgressIndicator(progress = { syncProgress }, modifier = Modifier.fillMaxWidth(0.7f).height(8.dp), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.surfaceVariant)
            } else {
                Text("Loading apps", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ErrorBody(
    message: String,
    retryLabel: String,
    onRetry: () -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.padding(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.Error, "Error", Modifier.size(40.dp), MaterialTheme.colorScheme.onErrorContainer)
                Text("Something went wrong", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                FilledTonalButton(onClick = onRetry) { Icon(Icons.Default.Refresh, retryLabel, Modifier.size(16.dp)); Spacer(Modifier.width(8.dp)); Text(retryLabel) }
            }
        }
    }
}

@Composable
private fun EmptyBody(
    title: String,
    subtitle: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(title, fontWeight = FontWeight.SemiBold)
        Text(
            subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
