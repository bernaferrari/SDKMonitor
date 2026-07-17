package com.bernaferrari.sdkmonitor.ui.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.shared.resources.Res
import com.bernaferrari.sdkmonitor.shared.resources.github_logo
import com.bernaferrari.sdkmonitor.shared.resources.linkedin_logo
import com.bernaferrari.sdkmonitor.shared.resources.reddit_logo
import com.bernaferrari.sdkmonitor.shared.resources.x_logo
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.DrawableResource

sealed class AboutAction {
    data class OpenUrl(
        val url: String,
    ) : AboutAction()

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
)

private data class SocialLink(
    val name: String,
    val url: String,
    val drawable: org.jetbrains.compose.resources.DrawableResource,
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

    val projectLinks =
        listOf(
            AboutLink(
                title = s.openSource,
                subtitle = s.viewOnGitHub,
                action = AboutAction.OpenUrl("https://github.com/bernaferrari/SDKMonitor"),
                drawable = Res.drawable.github_logo,
            ),
            AboutLink(
                title = s.contact,
                subtitle = s.contactDescription,
                action = AboutAction.ContactEmail,
                vectorIcon = Icons.Default.Email,
            ),
        )
    val socialLinks =
        listOf(
            SocialLink(
                name = "LinkedIn",
                url = "https://www.linkedin.com/in/bernaferrari",
                drawable = Res.drawable.linkedin_logo,
            ),
            SocialLink(
                name = "X",
                url = "https://x.com/bernaferrari",
                drawable = Res.drawable.x_logo,
            ),
            SocialLink(
                name = "Reddit",
                url = "https://www.reddit.com/user/bernaferrari",
                drawable = Res.drawable.reddit_logo,
            ),
        )
    val dataLinks =
        listOf(
            AboutLink(
                title = s.privacy,
                subtitle = s.noDataCollection,
                action = AboutAction.ShowPrivacy,
                vectorIcon = Icons.Default.Lock,
            ),
            AboutLink(
                title = s.exportData,
                subtitle = s.exportDataDescription,
                action = AboutAction.ExportData,
                vectorIcon = Icons.Default.Download,
            ),
        )

    fun activate(action: AboutAction) {
        when (action) {
            is AboutAction.OpenUrl -> onOpenUrl(action.url)
            AboutAction.ExportData -> onExportData()
            AboutAction.ShowPrivacy -> showPrivacyDialog = true
            AboutAction.ContactEmail -> onContact()
        }
    }

    Scaffold(
        modifier = contentModifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            text = s.about,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        if (onNavigateBack != null) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.cancel)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier =
                    Modifier
                        .widthIn(max = 680.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                AboutHero(appName = appName, versionName = versionName)
                AboutLinkGroup(title = "Project", links = projectLinks, onActivate = ::activate)
                AboutLinkGroup(title = "Data & privacy", links = dataLinks, onActivate = ::activate)
                AboutSocialRow(
                    title = s.getInTouch,
                    links = socialLinks,
                    onOpenUrl = onOpenUrl,
                )
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            icon = { Icon(Icons.Default.Lock, contentDescription = null) },
            title = { Text(s.privacy) },
            text = { Text(s.privacyBody) },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text(s.cancel) }
            },
        )
    }
}

@Composable
private fun AboutSocialRow(
    title: String,
    links: List<SocialLink>,
    onOpenUrl: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = segmentedListItemShape(isFirst = true, isLast = true),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            links.forEach { link ->
                IconButton(onClick = { onOpenUrl(link.url) }) {
                    Icon(
                        painter = painterResource(link.drawable),
                        contentDescription = link.name,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutHero(
    appName: String,
    versionName: String,
) {
    val s = sdkStrings()
    val shape = RoundedCornerShape(28.dp)
    val heroContentColor = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(24.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(heroContentColor.copy(alpha = 0.07f)),
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(heroContentColor.copy(alpha = 0.08f)),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(19.dp),
                color = heroContentColor.copy(alpha = 0.14f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "SDK",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = heroContentColor,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = heroContentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = s.madeWithLove,
                    style = MaterialTheme.typography.bodyMedium,
                    color = heroContentColor.copy(alpha = 0.76f),
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = heroContentColor.copy(alpha = 0.14f),
                ) {
                    Text(
                        text = "${s.versionLabel} $versionName",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = heroContentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutLinkGroup(
    title: String,
    links: List<AboutLink>,
    onActivate: (AboutAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            links.forEachIndexed { index, link ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = segmentedListItemShape(isFirst = index == 0, isLast = index == links.lastIndex),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    AboutLinkRow(link = link, onActivate = { onActivate(link.action) })
                }
            }
        }
    }
}

/** Matches NetGuard's expressive list geometry: emphasized outer corners, compact inner corners. */
@Composable
private fun segmentedListItemShape(
    isFirst: Boolean,
    isLast: Boolean,
): Shape {
    val base = MaterialTheme.shapes.small as? RoundedCornerShape ?: return MaterialTheme.shapes.small
    val emphasis = MaterialTheme.shapes.large as? RoundedCornerShape ?: return MaterialTheme.shapes.small

    return RoundedCornerShape(
        topStart = if (isFirst) emphasis.topStart else base.topStart,
        topEnd = if (isFirst) emphasis.topEnd else base.topEnd,
        bottomEnd = if (isLast) emphasis.bottomEnd else base.bottomEnd,
        bottomStart = if (isLast) emphasis.bottomStart else base.bottomStart,
    )
}

@Composable
private fun AboutLinkRow(
    link: AboutLink,
    onActivate: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onActivate).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(13.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                when {
                    link.drawable != null -> Icon(
                        painter = painterResource(link.drawable),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    link.vectorIcon != null -> Icon(
                        imageVector = link.vectorIcon,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = link.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = link.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector =
                if (link.action is AboutAction.OpenUrl) {
                    Icons.AutoMirrored.Filled.OpenInNew
                } else {
                    Icons.AutoMirrored.Filled.KeyboardArrowRight
                },
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}
