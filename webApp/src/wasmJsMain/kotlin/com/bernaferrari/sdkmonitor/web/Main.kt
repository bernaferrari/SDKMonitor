package com.bernaferrari.sdkmonitor.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.bernaferrari.sdkmonitor.data.source.local.createAppDatabase
import com.bernaferrari.sdkmonitor.shared.demo.RoomDemoHost

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        RoomDemoHost(
            createDatabase = ::createAppDatabase,
            showWebBanner = true,
        )
    }
}
