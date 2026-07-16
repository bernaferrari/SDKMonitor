package com.bernaferrari.sdkmonitor.ui.settings

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Android
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.shared.resources.Res
import com.bernaferrari.sdkmonitor.shared.resources.github_logo
import com.bernaferrari.sdkmonitor.shared.resources.linkedin_logo
import com.bernaferrari.sdkmonitor.shared.resources.reddit_logo
import com.bernaferrari.sdkmonitor.shared.resources.x_logo
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

sealed class AboutAction {
    data class OpenUrl(val url: String) : AboutAction()

    data object ExportData : AboutAction()

    data object ShowPrivacy : AboutAction()

    data object ContactEmail : AboutAction()
}

private data class AboutLink(
    val title: String,
    val subtitle: String,
    val action: AboutAction,
    val vectorIcon: ImageVector? = null,
    val drawable: DrawableResource? = null,
    val tint: Color? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutContent(
    appName: String,
    versionName: String,
    onNavigateBack: (() -> Unit)? = null,
    showTopBar: Boolean = true,
    onOpenUrl: (String) -> Unit = {},
    onExportData: () -> Unit = {},
    onContact: () -> Unit = {},
    contentModifier: Modifier = Modifier,
) {
    val s = sdkStrings()
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

    val onSurfaceTint = MaterialTheme.colorScheme.onSurfaceVariant
    val links =
        listOf(
            AboutLink(
                title = s.privacy,
                subtitle = s.privacyBody,
                action = AboutAction.ShowPrivacy,
                vectorIcon = Icons.Default.Lock,
            ),
            AboutLink(
                title = s.openSource,
                subtitle = "github.com/bernaferrari/SDKMonitor",
                action = AboutAction.OpenUrl("https://github.com/bernaferrari/SDKMonitor"),
                drawable = Res.drawable.github_logo,
                tint = onSurfaceTint,
            ),
            AboutLink(
                title = s.rateApp,
                subtitle = "Star the repository",
                action = AboutAction.OpenUrl("https://github.com/bernaferrari/SDKMonitor"),
                vectorIcon = Icons.Default.Star,
            ),
            AboutLink(
                title = "X / Twitter",
                subtitle = "@bernaferrari",
                action = AboutAction.OpenUrl("https://x.com/bernaferrari"),
                drawable = Res.drawable.x_logo,
                tint = onSurfaceTint,
            ),
            AboutLink(
                title = "LinkedIn",
                subtitle = "Bernardo Ferrari",
                action = AboutAction.OpenUrl("https://www.linkedin.com/in/bernaferrari"),
                drawable = Res.drawable.linkedin_logo,
                tint = onSurfaceTint,
            ),
            AboutLink(
                title = "Reddit",
                subtitle = "u/bernaferrari",
                action = AboutAction.OpenUrl("https://www.reddit.com/user/bernaferrari"),
                drawable = Res.drawable.reddit_logo,
                tint = onSurfaceTint,
            ),
            AboutLink(
                title = s.exportData,
                subtitle = "CSV backup of versions",
                action = AboutAction.ExportData,
                vectorIcon = Icons.Default.Download,
            ),
            AboutLink(
                title = s.contact,
                subtitle = "Reach the author",
                action = AboutAction.ContactEmail,
                vectorIcon = Icons.Default.Email,
            ),
        )

    Scaffold(
        modifier = contentModifier,
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text(s.about) },
                    navigationIcon = {
                        if (onNavigateBack != null) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.cancel)
                            }
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(96.dp)
                        .scale(headerAnimation)
                        .alpha(headerAnimation)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary,
                                ),
                            ),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Android,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(contentAnimation),
            ) {
                Text(appName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "${s.versionLabel} $versionName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    s.madeWithLove,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            links.forEach { link ->
                AboutLinkCard(
                    link = link,
                    contentAlpha = contentAnimation,
                    onActivate = {
                        when (val action = link.action) {
                            is AboutAction.OpenUrl -> onOpenUrl(action.url)
                            AboutAction.ExportData -> onExportData()
                            AboutAction.ShowPrivacy -> showPrivacyDialog = true
                            AboutAction.ContactEmail -> onContact()
                        }
                    },
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp),
                )
                Text(s.madeWithLove, style = MaterialTheme.typography.labelMedium)
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(s.privacy) },
            text = { Text(s.privacyBody) },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text(s.cancel) }
            },
        )
    }
}

@Composable
private fun AboutLinkCard(
    link: AboutLink,
    contentAlpha: Float,
    onActivate: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .alpha(contentAlpha)
                .clickable(onClick = onActivate),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    when {
                        link.drawable != null -> {
                            val painter: Painter = painterResource(link.drawable)
                            Icon(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = link.tint ?: MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        link.vectorIcon != null -> {
                            Icon(
                                imageVector = link.vectorIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(link.title, fontWeight = FontWeight.SemiBold)
                Text(
                    link.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
