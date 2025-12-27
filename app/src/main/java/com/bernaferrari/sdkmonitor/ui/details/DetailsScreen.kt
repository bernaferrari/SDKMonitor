package com.bernaferrari.sdkmonitor.ui.details

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.domain.model.AppVersion
import com.bernaferrari.sdkmonitor.ui.details.components.AppDetailsCard
import com.bernaferrari.sdkmonitor.ui.details.components.VersionTimeline
import kotlinx.coroutines.delay

/**
 * App Details Screen with Material Design 3 and animations
 */
@Composable
fun DetailsScreen(
    packageName: String,
    onNavigateBack: () -> Unit,
    isTabletSize: Boolean = false,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showLoading by remember { mutableStateOf(false) }

    // Action handlers
    val handleAppInfoClick =
        remember {
            {
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                context.startActivity(intent)
            }
        }

    val handlePlayStoreClick =
        remember {
            {
                val intent =
                    Intent(Intent.ACTION_VIEW).apply {
                        data = "market://details?id=$packageName".toUri()
                    }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Fallback to web browser
                    val webIntent =
                        Intent(Intent.ACTION_VIEW).apply {
                            data =
                                "https://play.google.com/store/apps/details?id=$packageName".toUri()
                        }
                    context.startActivity(webIntent)
                }
            }
        }

    // Only load app details if we have a valid package name (and not in tablet mode with empty selection)
    LaunchedEffect(packageName) {
        if (packageName.isNotBlank()) {
            viewModel.loadAppDetails(packageName)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is DetailsUiState.Loading) {
            delay(300)
            showLoading = true
        } else {
            showLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        // Main content
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Handle empty package name case (for tablet mode) - don't try to load anything
            if (packageName.isBlank()) {
                // Return early - this should be handled by EmptyDetailState in navigation
                return@Box
            }

            when (val state = uiState) {
                is DetailsUiState.Loading -> {
                    if (showLoading) {
                        LoadingState(
                            modifier =
                                Modifier
                                    .fillMaxSize(),
                        )
                    }
                }

                is DetailsUiState.Error -> {
                    // Only show error state if we're not in tablet mode or if we have a valid package name
                    if (packageName.isNotBlank()) {
                        ErrorState(
                            message = state.message,
                            onRetry = { viewModel.loadAppDetails(packageName) },
                            modifier =
                                Modifier
                                    .fillMaxSize(),
                        )
                    }
                }

                is DetailsUiState.Success -> {
                    DetailsContent(
                        state = state,
                        onAppInfoClick = handleAppInfoClick,
                        onPlayStoreClick = handlePlayStoreClick,
                        isTabletSize = isTabletSize,
                    )
                }
            }
        }

        // Floating back button
        if (!isTabletSize) {
            FilledIconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(8.dp)
                    .size(48.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                )
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
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

            Text(
                text = stringResource(R.string.loading_app_details),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error,
                )

                Text(
                    text = stringResource(R.string.app_not_found),
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                FilledTonalButton(
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.try_again))
                }
            }
        }
    }
}

@Composable
private fun DetailsContent(
    state: DetailsUiState.Success,
    onAppInfoClick: () -> Unit,
    onPlayStoreClick: () -> Unit,
    isTabletSize: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = if (isTabletSize) 0.dp else 64.dp, // No top padding on tablet, leave room for back button on phone
            bottom = 20.dp + navigationBarPadding.calculateBottomPadding(),
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            AppDetailsCard(
                appDetails = state.appDetails,
                onAppInfoClick = onAppInfoClick,
                onPlayStoreClick = onPlayStoreClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            VersionHistoryCard(
                versions = state.versions,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun VersionHistoryCard(
    versions: List<AppVersion>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = stringResource(R.string.version_history),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Text(
                    text = stringResource(R.string.version_history),
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.weight(1f))

                if (versions.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = "${versions.size}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }

            if (versions.isNotEmpty()) {
                VersionTimeline(
                    versions = versions,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Text(
                    text = stringResource(R.string.no_version_history),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}
