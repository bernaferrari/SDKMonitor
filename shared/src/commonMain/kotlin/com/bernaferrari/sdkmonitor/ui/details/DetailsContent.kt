package com.bernaferrari.sdkmonitor.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun DetailsContent(
    uiState: DetailsUiState,
    onRetry: () -> Unit = {},
    onAppInfoClick: ((packageName: String) -> Unit)? = null,
    onPlayStoreClick: ((packageName: String) -> Unit)? = null,
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
                    imageVector = Icons.Default.Warning,
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
            DetailsBody(
                appDetails = uiState.appDetails,
                versions = uiState.versions,
                onAppInfoClick = onAppInfoClick,
                onPlayStoreClick = onPlayStoreClick,
                contentModifier = contentModifier,
            )
        }
    }
}

@Composable
private fun DetailsBody(
    appDetails: AppDetails,
    versions: List<AppVersion>,
    onAppInfoClick: ((packageName: String) -> Unit)?,
    onPlayStoreClick: ((packageName: String) -> Unit)?,
    contentModifier: Modifier = Modifier,
) {
    val s = sdkStrings()

    Column(
        modifier =
            contentModifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
            Text(
                s.versionHistory,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
            )
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
