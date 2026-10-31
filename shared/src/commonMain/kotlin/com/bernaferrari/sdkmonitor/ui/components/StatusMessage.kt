package com.bernaferrari.sdkmonitor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Whether a [StatusMessage] reports something gone wrong (error colors) or simply nothing to show. */
enum class StatusTone { Neutral, Error }

/**
 * One way to say "nothing here", "something went wrong" or "loading" across the app: a tinted scalloped seal
 * (the app's signature shape) holding an icon, a title, a line of explanation and an optional action, centered.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatusMessage(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    tone: StatusTone = StatusTone.Neutral,
    action: (@Composable () -> Unit)? = null,
) {
    val scheme = MaterialTheme.colorScheme
    val seal = if (tone == StatusTone.Error) scheme.errorContainer else scheme.secondaryContainer
    val onSeal = if (tone == StatusTone.Error) scheme.onErrorContainer else scheme.onSecondaryContainer
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(88.dp).background(seal, MaterialShapes.Cookie9Sided.toShape()),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(36.dp), tint = onSeal)
        }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
        if (!body.isNullOrBlank()) {
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 320.dp),
            )
        }
        if (action != null) {
            Box(Modifier.padding(top = 8.dp)) { action() }
        }
    }
}

/**
 * Loading, the Expressive way: the morphing shape indicator, a line saying what is happening, and (when the
 * work can be measured, like the first sync) a wavy bar for how far it has got.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatusLoading(
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    progress: Float? = null,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LoadingIndicator(modifier = Modifier.size(72.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        if (!body.isNullOrBlank()) {
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
        if (progress != null) {
            LinearWavyProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth(0.7f).padding(top = 4.dp))
        }
    }
}
