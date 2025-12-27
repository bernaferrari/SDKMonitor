package com.bernaferrari.sdkmonitor

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bernaferrari.sdkmonitor.ui.AppNavigation
import com.bernaferrari.sdkmonitor.ui.theme.SDKMonitorTheme
import com.bernaferrari.sdkmonitor.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.colorMode = ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT

        // Extract package name from intent (from notification or deep link)
        val packageName = intent?.getStringExtra("package_name")

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()

            SDKMonitorTheme(
                darkTheme = themeViewModel.shouldUseDarkTheme(),
                dynamicColor = themeViewModel.shouldUseDynamicColor(),
            ) {
                AppNavigation(
                    modifier = Modifier.fillMaxSize(),
                    initialPackageName = packageName,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // Handle new intents (when app is already running)
        val packageName = intent.getStringExtra("package_name")
        if (!packageName.isNullOrEmpty()) {
            // Recreate activity to trigger navigation with new package
            recreate()
        }
    }
}
