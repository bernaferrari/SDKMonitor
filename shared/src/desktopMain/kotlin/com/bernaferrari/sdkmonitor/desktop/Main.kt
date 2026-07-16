package com.bernaferrari.sdkmonitor.desktop

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.bernaferrari.sdkmonitor.data.source.local.createAppDatabase
import com.bernaferrari.sdkmonitor.shared.demo.RoomDemoHost

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "SDK Monitor (Room)",
            state = rememberWindowState(width = 480.dp, height = 900.dp),
        ) {
            RoomDemoHost(createDatabase = ::createAppDatabase)
        }
    }
