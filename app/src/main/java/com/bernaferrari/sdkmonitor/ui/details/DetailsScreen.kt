package com.bernaferrari.sdkmonitor.ui.details

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/**
 * Android shell: ViewModel + system intents; UI lives in [DetailsContent] (shared).
 */
@Composable
fun DetailsScreen(
    packageName: String,
    onNavigateBack: () -> Unit = {},
    isTabletSize: Boolean = false,
    viewModel: DetailsViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(packageName) {
        viewModel.loadAppDetails(packageName)
    }

    DetailsContent(
        uiState = uiState,
        onRetry = { viewModel.refreshDetails(packageName) },
        onAppInfoClick = { pkg ->
            runCatching {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", pkg, null)
                    },
                )
            }
        },
        onPlayStoreClick = { pkg ->
            runCatching {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")),
                )
            }.onFailure {
                runCatching {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$pkg"),
                        ),
                    )
                }
            }
        },
        onNavigateBack = onNavigateBack.takeUnless { isTabletSize },
        contentModifier = modifier,
    )
}
