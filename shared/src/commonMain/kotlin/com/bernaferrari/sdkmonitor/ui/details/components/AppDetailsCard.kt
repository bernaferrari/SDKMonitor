package com.bernaferrari.sdkmonitor.ui.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bernaferrari.sdkmonitor.domain.AppDetails
import com.bernaferrari.sdkmonitor.domain.logic.formatFileSize
import com.bernaferrari.sdkmonitor.ui.components.SdkSeal
import com.bernaferrari.sdkmonitor.ui.icons.BrandIcons
import com.bernaferrari.sdkmonitor.ui.icons.MaterialSymbols
import com.bernaferrari.sdkmonitor.ui.platform.PlatformAppIcon
import com.bernaferrari.sdkmonitor.ui.platform.apiContainerColor
import com.bernaferrari.sdkmonitor.ui.platform.apiContentColor
import com.bernaferrari.sdkmonitor.ui.platform.apiToComposeColor
import com.bernaferrari.sdkmonitor.ui.platform.apiToVersionName
import com.bernaferrari.sdkmonitor.ui.platform.sdkStrings

@Composable
fun AppDetailsCard(
    appDetails: AppDetails,
    modifier: Modifier = Modifier,
    onAppInfoClick: (() -> Unit)? = null,
    onPlayStoreClick: (() -> Unit)? = null,
) {
    val s = sdkStrings()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            HeroHeaderSection(appDetails = appDetails)
            SDKInfoSection(appDetails = appDetails)
            if (onAppInfoClick != null || onPlayStoreClick != null) {
                ActionButtonsSection(
                    appInfoLabel = s.appInformation,
                    playStoreLabel = s.playStore,
                    onAppInfoClick = onAppInfoClick,
                    onPlayStoreClick = onPlayStoreClick,
                )
            }
            AppInfoSection(appDetails = appDetails)
        }
    }
}

@Composable
private fun HeroHeaderSection(appDetails: AppDetails) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) {
            PlatformAppIcon(
                packageName = appDetails.packageName,
                size = 80.dp,
                modifier = Modifier.clip(RoundedCornerShape(20.dp)),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = appDetails.title,
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = appDetails.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SDKInfoSection(appDetails: AppDetails) {
    val s = sdkStrings()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SdkSealTile(
            level = appDetails.targetSdk,
            label = s.targetSdk,
            seal = appDetails.targetSdk.apiToComposeColor(),
            onSeal = Color.White,
            content = appDetails.targetSdk.apiContentColor(),
            container = appDetails.targetSdk.apiContainerColor(),
            modifier = Modifier.weight(1f),
        )
        SdkSealTile(
            level = appDetails.minSdk,
            label = s.minSdk,
            seal = MaterialTheme.colorScheme.secondary,
            onSeal = MaterialTheme.colorScheme.onSecondary,
            // The minimum is a fact, not a grade: a neutral circle.
            graded = false,
            content = MaterialTheme.colorScheme.onSecondaryContainer,
            container = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.weight(1f),
        )
    }
}

/** An SDK level in its seal (the target's shape grades it), named under it. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SdkSealTile(
    level: Int,
    label: String,
    seal: Color,
    onSeal: Color,
    graded: Boolean = true,
    content: Color,
    container: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(container, RoundedCornerShape(24.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SdkSeal(
            level = level,
            size = 64.dp,
            fill = seal,
            textColor = onSeal,
            textStyle = MaterialTheme.typography.headlineSmall,
            graded = graded,
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = content)
            Text(
                level.apiToVersionName(),
                style = MaterialTheme.typography.bodyLarge,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ActionButtonsSection(
    appInfoLabel: String,
    playStoreLabel: String,
    onAppInfoClick: (() -> Unit)?,
    onPlayStoreClick: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (onAppInfoClick != null) {
            OutlinedButton(
                onClick = onAppInfoClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    imageVector = MaterialSymbols.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    appInfoLabel,
                    style =
                        MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (onPlayStoreClick != null) {
            OutlinedButton(
                onClick = onPlayStoreClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    imageVector = BrandIcons.GooglePlay,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    playStoreLabel,
                    style =
                        MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AppInfoSection(appDetails: AppDetails) {
    val s = sdkStrings()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        InfoRow(
            label = s.versionLabel,
            value = "${appDetails.versionName} (${appDetails.versionCode})",
        )
        if (appDetails.lastUpdateTime.isNotBlank()) {
            InfoRow(
                label = s.updatedLabel,
                value = appDetails.lastUpdateTime,
            )
        }
        InfoRow(
            label = s.sizeLabel,
            value = formatFileSize(appDetails.size),
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.width(88.dp).alignByBaseline(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f).alignByBaseline(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
