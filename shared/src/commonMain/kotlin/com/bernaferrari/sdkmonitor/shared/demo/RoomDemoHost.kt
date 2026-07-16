package com.bernaferrari.sdkmonitor.shared.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.bernaferrari.sdkmonitor.data.repository.RoomAppsRepository
import com.bernaferrari.sdkmonitor.data.repository.asRoomAppsRepository
import com.bernaferrari.sdkmonitor.data.repository.seedDemoDataIfEmpty
import com.bernaferrari.sdkmonitor.data.source.local.AppDatabase

/**
 * Opens Room ([commonMain] [AppDatabase]), seeds demo rows once, runs UI on live data.
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
            db.seedDemoDataIfEmpty()
            db.asRoomAppsRepository()
        }.onSuccess { repository = it }
            .onFailure { error = it.message ?: it.toString() }
    }

    when {
        error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Room failed: $error")
            }
        }
        repository == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
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
