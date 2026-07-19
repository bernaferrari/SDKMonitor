package com.bernaferrari.sdkmonitor.ui.details

import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.AppDetails
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.ui.details.components.AppDetailsCard
import com.bernaferrari.sdkmonitor.ui.details.components.VersionCard
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings
import com.bernaferrari.sdkmonitor.ui.state.DetailsUiState

private val DetailsBackButtonEasing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)

@Composable
fun DetailsContent(
    uiState: DetailsUiState,
    onRetry: () -> Unit = {},
    onAppInfoClick: ((packageName: String) -> Unit)? = null,
    onPlayStoreClick: ((packageName: String) -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    contentModifier: Modifier = Modifier,
) {
    val s = sdkStrings()

    when (uiState) {
        is DetailsUiState.Loading -> {
            Box(
                modifier = contentModifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = { CircularProgressIndicator() },
            )
        }

        is DetailsUiState.Error -> {
            val errorMessage = uiState.message
            Column(
                modifier =
                    contentModifier
                        .fillMaxSize()
                        .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = MaterialSymbols.Filled.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMessage, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                FilledTonalButton(onClick = onRetry) { Text(s.retry) }
            }
        }

        is DetailsUiState.Success -> {
            val scrollState = rememberScrollState()
            val isScrolled by remember(scrollState) { derivedStateOf { scrollState.value > 0 } }
            val backButtonContainerColor by
                animateColorAsState(
                    targetValue =
                        if (isScrolled) {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0f)
                        },
                    animationSpec =
                        tween(
                            durationMillis = 150,
                            easing = DetailsBackButtonEasing,
                        ),
                    label = "detailsBackButtonContainer",
                )
            Box(
                modifier =
                    contentModifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                DetailsBody(
                    appDetails = uiState.appDetails,
                    versions = uiState.versions,
                    onAppInfoClick = onAppInfoClick,
                    onPlayStoreClick = onPlayStoreClick,
                    showBackButton = onNavigateBack != null,
                    scrollState = scrollState,
                    contentModifier = Modifier.fillMaxSize(),
                )
                onNavigateBack?.let { navigateBack ->
                    FilledIconButton(
                        onClick = navigateBack,
                        modifier = Modifier.padding(8.dp).size(48.dp),
                        shape = CircleShape,
                        colors =
                            IconButtonDefaults.filledIconButtonColors(
                                containerColor = backButtonContainerColor,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                    ) {
                        Icon(MaterialSymbols.Filled.ArrowBack, contentDescription = s.back)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsBody(
    appDetails: AppDetails,
    versions: List<AppVersion>,
    onAppInfoClick: ((packageName: String) -> Unit)?,
    onPlayStoreClick: ((packageName: String) -> Unit)?,
    showBackButton: Boolean,
    scrollState: ScrollState,
    contentModifier: Modifier = Modifier,
) {
    val s = sdkStrings()

    Box(
        modifier = contentModifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = if (showBackButton) 64.dp else 16.dp,
                        bottom = 24.dp,
                    ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            AppDetailsCard(
                appDetails = appDetails,
                onAppInfoClick =
                    onAppInfoClick?.let { handler ->
                        { handler(appDetails.packageName) }
                    },
                onPlayStoreClick =
                    onPlayStoreClick?.let { handler ->
                        { handler(appDetails.packageName) }
                    },
            )

            if (versions.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = s.versionHistory,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ) {
                        Text(
                            text = versions.size.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    }
                }
                Column {
                    versions.forEachIndexed { index, version ->
                        VersionCard(
                            versionInfo = version,
                            isLatest = index == 0,
                            isLast = index == versions.lastIndex,
                        )
                    }
                }
            }
        }
    }
}
