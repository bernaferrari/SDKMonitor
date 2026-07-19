package com.bernaferrari.sdkmonitor.shared.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.bernaferrari.sdkmonitor.data.repository.RoomAppsRepository
import com.bernaferrari.sdkmonitor.data.repository.asRoomAppsRepository
import com.bernaferrari.sdkmonitor.data.repository.resetDemoData
import com.bernaferrari.sdkmonitor.data.source.local.AppDatabase
import com.bernaferrari.sdkmonitor.domain.logic.formatRelativeTimestamp
import com.bernaferrari.sdkmonitor.shared.resources.Res
import com.bernaferrari.sdkmonitor.shared.resources.room_failed
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource

/**
 * Opens Room ([commonMain] [AppDatabase]), refreshes its disposable mock rows, runs UI on live data.
 */
@Composable
fun RoomDemoHost(
    createDatabase: () -> AppDatabase,
    showWebBanner: Boolean = false,
) {
    var repository by remember { mutableStateOf<RoomAppsRepository?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runCatching {
            val db = createDatabase()
            db.resetDemoData()
            db.asRoomAppsRepository { timestamp ->
                formatRelativeTimestamp(timestamp, Clock.System.now().toEpochMilliseconds())
            }
        }.onSuccess { repository = it }
            .onFailure { error = it.message ?: it.toString() }
    }

    when {
        error != null -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(DemoStartupBackground),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(Res.string.room_failed, error.orEmpty()))
            }
        }
        repository == null -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(DemoStartupBackground),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = DemoStartupAccent,
                        strokeWidth = 3.dp,
                    )
                    Text(
                        text = "Loading SDK Monitor…",
                        color = DemoStartupText,
                        fontSize = 15.sp,
                        letterSpacing = 0.02.em,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        else -> {
            DemoSdkMonitorApp(
                roomRepository = repository!!,
                showWebBanner = showWebBanner,
            )
        }
    }
}

private val DemoStartupBackground = Color(0xFF121212)
private val DemoStartupAccent = Color(0xFF90CAF9)
private val DemoStartupText = Color(0xFFE8E8E8).copy(alpha = 0.9f)
