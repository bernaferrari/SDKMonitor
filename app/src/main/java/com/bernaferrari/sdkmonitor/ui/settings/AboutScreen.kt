package com.bernaferrari.sdkmonitor.ui.settings

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bernaferrari.sdkmonitor.BuildConfig
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Android shell for [AboutContent]: intents, export, BuildConfig.
 */
@Composable
fun AboutScreen(
    onNavigateBack: (() -> Unit)? = null,
    isTabletSize: Boolean = false,
    settingsViewModel: SettingsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val s = sdkStrings()

    AboutContent(
        appName = s.appName,
        versionName = BuildConfig.VERSION_NAME,
        onNavigateBack = onNavigateBack,
        showTopBar = !isTabletSize,
        onOpenUrl = { url ->
            runCatching {
                context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
            }
        },
        onExportData = {
            scope.launch {
                val file = settingsViewModel.exportDataToCsv(context) ?: return@launch
                val uri =
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file,
                    )
                val share =
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                context.startActivity(Intent.createChooser(share, s.exportData))
            }
        },
        onContact = {
            runCatching {
                context.startActivity(
                    Intent(Intent.ACTION_SENDTO, "mailto:bernaferrari2+sdkmonitor@gmail.com".toUri()),
                )
            }
        },
    )
}
