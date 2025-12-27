package com.bernaferrari.sdkmonitor.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.bernaferrari.sdkmonitor.core.AppManager
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Hilt entry point to access AppManager from composables
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppManagerEntryPoint {
    fun appManager(): AppManager
}

/**
 * Composable function to get a cached app icon.
 * Uses AppManager's centralized cache to avoid duplicate OS calls.
 * 
 * @param packageName The package name to get the icon for
 * @return The cached drawable, or null if not found/loading
 */
@Composable
fun rememberCachedAppIcon(packageName: String): Drawable? {
    val context = LocalContext.current
    
    val icon by produceState<Drawable?>(initialValue = null, packageName) {
        value = withContext(Dispatchers.IO) {
            val appManager = EntryPoints.get(
                context.applicationContext,
                AppManagerEntryPoint::class.java
            ).appManager()
            appManager.getAppIconCached(packageName)
        }
    }
    
    return icon
}
