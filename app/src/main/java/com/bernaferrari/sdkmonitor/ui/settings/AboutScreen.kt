package com.bernaferrari.sdkmonitor.ui.settings

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bernaferrari.sdkmonitor.BuildConfig
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: (() -> Unit)? = null,
    isTabletSize: Boolean = false,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showPrivacyDialog by remember { mutableStateOf(false) }

    val headerAnimation by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "header",
    )

    val contentAnimation by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, delayMillis = 200),
        label = "content",
    )

    val buttonAnimation by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(400, delayMillis = 400),
        label = "button",
    )

    Scaffold(
        topBar =
            if (!isTabletSize) {
                {
                    TopAppBar(
                        title = { Text("") }, // Clean minimal header
                        navigationIcon = {
                            if (onNavigateBack != null) {
                                IconButton(onClick = onNavigateBack) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        stringResource(R.string.back),
                                    )
                                }
                            }
                        },
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                            ),
                    )
                }
            } else {
                { }
            },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Hero Section
            AppHeroCard(
                modifier =
                    Modifier
                        .scale(headerAnimation)
                        .alpha(headerAnimation),
            )

            // Developer Card
            InfoCard(
                title = stringResource(R.string.made_with_care_by),
                content = stringResource(R.string.bernardo_ferrari),
                icon = Icons.Default.Favorite,
                iconTint = Color(0xFFE91E63),
                action = {
                    SocialSection(context)
                },
            )

            // Features Row - Privacy and Open Source
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Privacy Card
                FeatureCard(
                    title = stringResource(R.string.privacy_first_title),
                    subtitle = stringResource(R.string.zero_tracking),
                    icon = Icons.Default.Lock,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f),
                    onClick = { showPrivacyDialog = true },
                )

                // Open Source Card
                FeatureCard(
                    title = stringResource(R.string.open_source),
                    subtitle = stringResource(R.string.view_on_github),
                    icon = Icons.Default.Code,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://github.com/bernaferrari/SDKMonitor".toUri(),
                            ),
                        )
                    },
                )
            }

            // Export Data Card - More compact horizontal layout
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .alpha(contentAnimation),
                onClick = {
                    scope.launch {
                        val exportedFile = settingsViewModel.exportDataToCsv(context)
                        exportedFile?.let { file ->
                            val timestamp =
                                SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(
                                    Date(),
                                )

                            // Use FileProvider to get a content URI
                            val uri =
                                FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file,
                                )

                            val shareIntent =
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/csv"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    putExtra(
                                        Intent.EXTRA_SUBJECT,
                                        context.getString(R.string.export_sdk_monitor_data) + " - $timestamp",
                                    )
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        context.getString(
                                            R.string.sdk_monitor_data_backup,
                                            timestamp,
                                        ),
                                    )
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                            context.startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    context.getString(R.string.export_sdk_monitor_data),
                                ),
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF2196F3).copy(0.1f),
                shadowElevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color(0xFF2196F3).copy(0.2f),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF2196F3),
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.export_data),
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            stringResource(R.string.export_data_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Contact Action - Single elegant button
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .scale(buttonAnimation)
                        .alpha(buttonAnimation),
                onClick = {
                    val sendFeedbackText = context.getString(R.string.send_feedback)
                    val intent =
                        Intent(
                            Intent.ACTION_SENDTO,
                            "mailto:bernaferrari2+sdk@gmail.com".toUri(),
                        ).apply {
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                context.getString(R.string.sdk_monitor_feedback),
                            )
                        }
                    context.startActivity(Intent.createChooser(intent, sendFeedbackText))
                },
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 8.dp,
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onPrimary.copy(0.2f),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.get_in_touch),
                            style =
                                MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Text(
                            stringResource(R.string.send_feedback_or_ask_questions),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(0.8f),
                        )
                    }

                    Icon(
                        Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                    )
                }
            }
        }

        // Privacy Dialog
        if (showPrivacyDialog) {
            PrivacyDialog(
                onDismiss = { showPrivacyDialog = false },
            )
        }
    }
}

@Composable
private fun AppHeroCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val appIconDrawable =
        remember(context) {
            try {
                context.packageManager.getApplicationIcon(context.packageName)
            } catch (e: PackageManager.NameNotFoundException) {
                null // Should not happen for own app, but good practice
            }
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(0.8f),
                                MaterialTheme.colorScheme.secondaryContainer.copy(0.8f),
                            ),
                    ),
                    RoundedCornerShape(24.dp),
                ).padding(32.dp),
    ) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Removed Surface, directly use Box for icon alignment and potential fallback
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                appIconDrawable?.let { drawable ->
                    AsyncImage(
                        model =
                            ImageRequest
                                .Builder(context)
                                .data(drawable)
                                .crossfade(true)
                                .build(),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.size(80.dp), // Icon size, can be adjusted
                    )
                } ?: Icon(
                    // Fallback if icon is somehow null
                    Icons.Default.Android,
                    contentDescription = stringResource(R.string.app_name), // Consistent content description
                    modifier = Modifier.size(64.dp), // Fallback icon size
                    tint = MaterialTheme.colorScheme.primary, // Or onPrimaryContainer if background is primaryContainer
                )
            }

            // App Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style =
                        MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                        ),
                    textAlign = TextAlign.Center,
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(0.9f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            stringResource(R.string.version_format, BuildConfig.VERSION_NAME),
                            style =
                                MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    content: String,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    action: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = iconTint.copy(0.15f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = iconTint,
                        )
                    }
                }

                Column {
                    Text(
                        title,
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                    Text(
                        content,
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            action?.invoke()
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val cardShape = RoundedCornerShape(20.dp)

    Card(
        modifier =
            modifier
                .clip(cardShape)
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = cardShape,
        colors =
            CardDefaults.cardColors(
                containerColor = color.copy(0.1f),
            ),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = color.copy(0.2f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = color,
                        )
                    }
                }
                Text(
                    title,
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun SocialSection(context: android.content.Context) {
    val socials =
        listOf(
            Pair(R.drawable.github_logo, "https://github.com/bernaferrari"),
            Pair(R.drawable.linkedin_logo, "https://linkedin.com/in/bernaferrari"),
            Pair(R.drawable.x_logo, "https://x.com/bernaferrari"),
        )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        socials.forEach { (iconRes, url) ->
            Surface(
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
            ) {
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacyDialog(onDismiss: () -> Unit) {
    val dialogAnimation by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "dialog",
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.alpha(dialogAnimation),
            ) {
                Text(stringResource(R.string.close))
            }
        },
        icon = {
            Surface(
                modifier =
                    Modifier
                        .size(64.dp)
                        .scale(dialogAnimation),
                shape = CircleShape,
                color = Color(0xFF4CAF50).copy(0.15f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color(0xFF4CAF50),
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(R.string.privacy_first_title),
                style =
                    MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                modifier = Modifier.alpha(dialogAnimation),
            )
        },
        text = {
            Column(
                modifier = Modifier.alpha(dialogAnimation),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.privacy_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                // Additional privacy points
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.3f),
                        ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PrivacyFeature("🚫", stringResource(R.string.no_data_collection))
                        PrivacyFeature("📱", stringResource(R.string.everything_stays_local))
                        PrivacyFeature("🔒", stringResource(R.string.no_internet_permissions_needed))
                        PrivacyFeature("👁️", stringResource(R.string.open_source_transparent))
                    }
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .widthIn(max = 500.dp)
            .scale(dialogAnimation),
    )
}

@Composable
private fun PrivacyFeature(
    emoji: String,
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = text,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                ),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    SDKMonitorTheme {
        AboutScreen()
    }
}

@Preview(widthDp = 800)
@Composable
private fun AboutScreenTabletPreview() {
    SDKMonitorTheme {
        AboutScreen(isTabletSize = true)
    }
}
