package com.bernaferrari.sdkmonitor.ui.main

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.AppFilter
import com.bernaferrari.sdkmonitor.domain.model.SortOption
import com.bernaferrari.sdkmonitor.ui.main.components.FastScroller
import com.bernaferrari.sdkmonitor.ui.main.components.MainAppCard

@Composable
fun MainScreen(
    onNavigateToAppDetails: (String) -> Unit,
    selectedPackageName: String? = null,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val appFilter by viewModel.appFilter.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val isFirstSync by viewModel.isFirstSync.collectAsStateWithLifecycle()
    val syncProgress by viewModel.syncProgress.collectAsStateWithLifecycle()

    // Add focus manager for keyboard dismissal
    val focusManager = LocalFocusManager.current

    // Launch effect to load apps when screen first loads
    LaunchedEffect(Unit) {
        viewModel.loadApps()
    }

    // Add state for sort menu and fast scroller
    var showSortMenu by remember { mutableStateOf(false) }
    var isScrollerActive by remember { mutableStateOf(false) }
    var currentScrollLetter by remember { mutableStateOf("") }

    // Add state for filter menu
    var showFilterMenu by remember { mutableStateOf(false) }

    // State for LazyColumn with persistent scroll state
    val listState = rememberLazyListState()

    // Track if list is being scrolled
    val isScrolling by remember {
        derivedStateOf {
            listState.isScrollInProgress
        }
    }

    // Dismiss keyboard when scrolling
    LaunchedEffect(isScrolling) {
        if (isScrolling) {
            focusManager.clearFocus()
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        // Compact header card with title, search, and dropdowns
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Title and app count row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style =
                            MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    // App count
                    when (val state = uiState) {
                        is MainUiState.Success -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier.padding(0.dp),
                            ) {
                                Text(
                                    text =
                                        stringResource(
                                            R.string.apps_count,
                                            state.filteredApps.size,
                                        ),
                                    style =
                                        MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier =
                                        Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp,
                                        ),
                                )
                            }
                        }

                        else -> {
                            Spacer(modifier = Modifier.width(1.dp))
                        }
                    }
                }

                // Search and dropdown buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Search field takes most space
                    TextField(
                        value = searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                stringResource(R.string.search_apps_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_apps_hint),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        trailingIcon =
                            if (searchQuery.isNotEmpty()) {
                                {
                                    IconButton(
                                        onClick = { viewModel.updateSearchQuery("") },
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = stringResource(R.string.clear_search),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }
                            } else {
                                null
                            },
                        singleLine = true,
                        shape =
                            RoundedCornerShape(
                                topStart = 20.dp,
                                bottomStart = 20.dp,
                                topEnd = 10.dp,
                                bottomEnd = 10.dp,
                            ),
                        colors =
                            TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        textStyle =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                            ),
                    )

                    // Filter dropdown button
                    Box {
                        IconButton(
                            onClick = { showFilterMenu = true },
                            shape = RoundedCornerShape(10.dp),
                            colors =
                                IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                ),
                            modifier =
                                Modifier
                                    .width(48.dp)
                                    .height(56.dp),
                        ) {
                            Icon(
                                imageVector =
                                    when (appFilter) {
                                        AppFilter.ALL_APPS -> Icons.Default.Apps
                                        AppFilter.USER_APPS -> Icons.Default.Person
                                        AppFilter.SYSTEM_APPS -> Icons.Default.Android
                                    },
                                contentDescription = stringResource(R.string.filter_apps),
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = MaterialTheme.colorScheme.surface,
                        ) {
                            // Filter menu title
                            Text(
                                text = stringResource(R.string.filter_apps),
                                style =
                                    MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )

                            // Calculate counts for each filter when success state
                            val filterCounts =
                                when (val currentState = uiState) {
                                    is MainUiState.Success -> {
                                        remember(currentState.apps) {
                                            mapOf(
                                                AppFilter.ALL_APPS to currentState.apps.size,
                                                AppFilter.USER_APPS to currentState.apps.count { it.isFromPlayStore },
                                                AppFilter.SYSTEM_APPS to currentState.apps.count { !it.isFromPlayStore },
                                            )
                                        }
                                    }

                                    else -> {
                                        mapOf(
                                            AppFilter.ALL_APPS to 0,
                                            AppFilter.USER_APPS to 0,
                                            AppFilter.SYSTEM_APPS to 0,
                                        )
                                    }
                                }

                            AppFilter.entries.forEach { filter ->
                                DropdownMenuItem(text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            Icon(
                                                imageVector =
                                                    when (filter) {
                                                        AppFilter.ALL_APPS -> Icons.Default.Apps
                                                        AppFilter.USER_APPS -> Icons.Default.Person
                                                        AppFilter.SYSTEM_APPS -> Icons.Default.Android
                                                    },
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint =
                                                    if (appFilter == filter) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    },
                                            )
                                            Text(
                                                text =
                                                    when (filter) {
                                                        AppFilter.ALL_APPS -> stringResource(R.string.all_apps)
                                                        AppFilter.USER_APPS -> stringResource(R.string.user_apps)
                                                        AppFilter.SYSTEM_APPS -> stringResource(R.string.system_apps)
                                                    },
                                                style =
                                                    MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (appFilter == filter) FontWeight.Bold else FontWeight.Normal,
                                                    ),
                                                color =
                                                    if (appFilter == filter) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    },
                                            )
                                        }

                                        // Count badge
                                        Surface(
                                            modifier = Modifier.padding(start = 12.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            color =
                                                if (appFilter == filter) {
                                                    MaterialTheme.colorScheme.primaryContainer
                                                } else {
                                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                                },
                                        ) {
                                            Text(
                                                text = filterCounts[filter]?.toString() ?: "0",
                                                modifier =
                                                    Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 4.dp,
                                                    ),
                                                style =
                                                    MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                    ),
                                                color =
                                                    if (appFilter == filter) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    },
                                            )
                                        }
                                    }
                                }, onClick = {
                                    viewModel.updateAppFilter(filter)
                                    showFilterMenu = false
                                })
                            }
                        }
                    }

                    // Sort dropdown button
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            shape =
                                RoundedCornerShape(
                                    topStart = 10.dp,
                                    bottomStart = 10.dp,
                                    topEnd = 20.dp,
                                    bottomEnd = 20.dp,
                                ),
                            colors =
                                IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                ),
                            modifier =
                                Modifier
                                    .width(48.dp)
                                    .height(56.dp),
                        ) {
                            Icon(
                                imageVector = sortOption.icon,
                                contentDescription = stringResource(R.string.sort_by),
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = MaterialTheme.colorScheme.surface,
                        ) {
                            // Sort menu title
                            Text(
                                text = stringResource(R.string.sort_by),
                                style =
                                    MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )

                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Icon(
                                            imageVector = option.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint =
                                                if (sortOption == option) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                },
                                        )
                                        Text(
                                            text =
                                                when (option) {
                                                    SortOption.NAME -> stringResource(R.string.sort_by_name)
                                                    SortOption.SDK -> stringResource(R.string.sort_by_sdk)
                                                },
                                            style =
                                                MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (sortOption == option) FontWeight.Bold else FontWeight.Normal,
                                                ),
                                            color =
                                                if (sortOption == option) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurface
                                                },
                                        )
                                    }
                                }, onClick = {
                                    viewModel.updateSortOption(option)
                                    showSortMenu = false
                                })
                            }
                        }
                    }
                }
            }
        }

        // Content states with functional fast scroller
        when (val state = uiState) {
            is MainUiState.Loading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(end = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )

                        if (isFirstSync && syncProgress.isActive) {
                            // First sync with progress
                            Text(
                                text = stringResource(R.string.setting_up_your_apps),
                                style =
                                    MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Text(
                                text =
                                    stringResource(
                                        R.string.apps_sync_progress,
                                        syncProgress.current,
                                        syncProgress.total,
                                    ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            LinearProgressIndicator(
                                progress = {
                                    if (syncProgress.total > 0) {
                                        syncProgress.current.toFloat() / syncProgress.total.toFloat()
                                    } else {
                                        0f
                                    }
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        } else {
                            // Regular loading
                            Text(
                                text = stringResource(R.string.loading_apps),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            is MainUiState.Error -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(end = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Card(
                        modifier = Modifier.padding(24.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            Text(
                                text = stringResource(R.string.something_went_wrong),
                                style =
                                    MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                            )
                            FilledTonalButton(
                                onClick = { viewModel.retryLoadApps() },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(R.string.retry),
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }

            is MainUiState.Success -> {
                when {
                    state.filteredApps.isEmpty() && searchQuery.isNotBlank() -> {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(end = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = stringResource(R.string.no_apps_found),
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = stringResource(R.string.no_apps_found),
                                    style =
                                        MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                        ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = stringResource(R.string.try_searching_different_terms),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    state.filteredApps.isEmpty() -> {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(end = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = stringResource(R.string.no_apps_found),
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text =
                                        stringResource(
                                            R.string.no_filtered_apps_found,
                                            when (appFilter) {
                                                AppFilter.ALL_APPS -> stringResource(R.string.all_apps)
                                                AppFilter.USER_APPS -> stringResource(R.string.user_apps)
                                                AppFilter.SYSTEM_APPS -> stringResource(R.string.system_apps)
                                            }.lowercase(),
                                        ),
                                    style =
                                        MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                        ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = stringResource(R.string.try_changing_filter),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    else -> {
                        // Only show fast scroller when sorting by name or SDK and has enough apps
                        val showFastScroller =
                            (sortOption == SortOption.NAME || sortOption == SortOption.SDK) &&
                                state.filteredApps.size > 15 &&
                                searchQuery.isBlank()

                        // Group apps by first letter when sorting alphabetically
                        val groupedApps =
                            remember(state.filteredApps, sortOption) {
                                if (sortOption == SortOption.NAME && searchQuery.isBlank()) {
                                    state.filteredApps
                                        .groupBy {
                                            val firstChar = it.title.firstOrNull()?.uppercaseChar()
                                            if (firstChar?.isLetter() == true) {
                                                firstChar.toString()
                                            } else {
                                                "#" // Group all non-letters (numbers, symbols) under "#"
                                            }
                                        }.toSortedMap()
                                } else {
                                    emptyMap()
                                }
                            }

                        // Group apps by SDK version when sorting by SDK
                        val groupedAppsBySdk =
                            remember(state.filteredApps, sortOption) {
                                if (sortOption == SortOption.SDK && searchQuery.isBlank()) {
                                    state.filteredApps
                                        .groupBy { app ->
                                            "SDK ${app.sdkVersion}"
                                        }.toSortedMap(
                                            compareByDescending { key ->
                                                // Extract SDK number for proper sorting
                                                key.removePrefix("SDK ").toIntOrNull() ?: 0
                                            },
                                        )
                                } else {
                                    emptyMap()
                                }
                            }

                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                state = listState,
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .pointerInput(Unit) {
                                            detectDragGestures(
                                                onDragStart = {
                                                    focusManager.clearFocus()
                                                },
                                            ) { _, _ -> }
                                        }.padding(end = if (showFastScroller) 32.dp else 0.dp),
                            ) {
                                when {
                                    groupedApps.isNotEmpty() -> {
                                        // Show apps with alphabet section headers
                                        groupedApps.forEach { (letter, appsInSection) ->
                                            // Section header
                                            item(key = "header_$letter") {
                                                Surface(
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                start = 16.dp,
                                                                end = 16.dp,
                                                                top = 8.dp,
                                                                bottom = 4.dp,
//                                                            horizontal = 16.dp, vertical = 8.dp
                                                            ),
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                                ) {
                                                    Text(
                                                        text = letter,
                                                        style =
                                                            MaterialTheme.typography.titleMedium.copy(
                                                                fontWeight = FontWeight.Bold,
                                                            ),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier =
                                                            Modifier.padding(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp,
                                                            ),
                                                    )
                                                }
                                            }

                                            // Apps in this section
                                            itemsIndexed(
                                                items = appsInSection,
                                                key = { _, app -> "${letter}_${app.packageName}" },
                                            ) { index, appVersion ->
                                                MainAppCard(
                                                    appVersion = appVersion,
                                                    searchQuery = searchQuery,
                                                    isLast = index == appsInSection.lastIndex,
                                                    isSelected = selectedPackageName == appVersion.packageName,
                                                    onClick = {
                                                        onNavigateToAppDetails(appVersion.packageName)
                                                    },
                                                )
                                            }
                                        }
                                    }

                                    groupedAppsBySdk.isNotEmpty() -> {
                                        // Show apps with SDK version section headers
                                        groupedAppsBySdk.forEach { (sdkHeader, appsInSection) ->
                                            // SDK Section header
                                            item(key = "sdk_header_$sdkHeader") {
                                                Surface(
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp,
                                                            ),
                                                    shape = RoundedCornerShape(12.dp),
                                                    color =
                                                        MaterialTheme.colorScheme.primaryContainer.copy(
                                                            alpha = 0.3f,
                                                        ),
                                                ) {
                                                    Text(
                                                        text = sdkHeader,
                                                        style =
                                                            MaterialTheme.typography.titleMedium.copy(
                                                                fontWeight = FontWeight.Bold,
                                                            ),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier =
                                                            Modifier.padding(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp,
                                                            ),
                                                    )
                                                }
                                            }

                                            // Apps in this SDK section
                                            itemsIndexed(
                                                items = appsInSection,
                                                key = { _, app -> "${sdkHeader}_${app.packageName}" },
                                            ) { index, appVersion ->
                                                MainAppCard(
                                                    appVersion = appVersion,
                                                    searchQuery = searchQuery,
                                                    isLast = index == appsInSection.lastIndex,
                                                    isSelected = selectedPackageName == appVersion.packageName,
                                                    onClick = { onNavigateToAppDetails(appVersion.packageName) },
                                                )
                                            }
                                        }
                                    }

                                    else -> {
                                        // Show apps without headers (when searching)
                                        itemsIndexed(
                                            items = state.filteredApps,
                                            key = { _, app -> app.packageName },
                                        ) { index, appVersion ->
                                            MainAppCard(
                                                appVersion = appVersion,
                                                searchQuery = searchQuery,
                                                isLast = index == state.filteredApps.lastIndex,
                                                isSelected = selectedPackageName == appVersion.packageName,
                                                onClick = { onNavigateToAppDetails(appVersion.packageName) },
                                            )
                                        }
                                    }
                                }
                            }

                            // Fast Scroller - positioned as overlay
                            if (showFastScroller) {
                                FastScroller(
                                    modifier =
                                        Modifier
                                            .align(Alignment.CenterEnd)
                                            .windowInsetsPadding(WindowInsets.navigationBars),
                                    apps = state.filteredApps,
                                    listState = listState,
                                    appFilter = appFilter,
                                    sortOption = sortOption,
                                    scrollOffsetDp = 80,
                                    onLetterSelected = { letter ->
                                        currentScrollLetter = letter
                                        isScrollerActive = true
                                    },
                                    onScrollFinished = {
                                        isScrollerActive = false
                                        currentScrollLetter = ""
                                    },
                                    onInteractionStart = {
                                        focusManager.clearFocus()
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
