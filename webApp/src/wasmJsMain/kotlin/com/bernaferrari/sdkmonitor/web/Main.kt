package com.bernaferrari.sdkmonitor.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.bernaferrari.sdkmonitor.data.source.local.createAppDatabase
import com.bernaferrari.sdkmonitor.shared.demo.RoomDemoHost
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val root = document.getElementById("root") ?: document.body!!
    ComposeViewport(root) {
        RoomDemoHost(
            createDatabase = ::createAppDatabase,
            showWebBanner = true,
        )
    }
}
