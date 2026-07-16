package com.bernaferrari.sdkmonitor.core

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bernaferrari.sdkmonitor.domain.repository.PreferencesRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinWorker

@KoinWorker

class SyncWorker(
        context: Context,
        workerParameters: WorkerParameters,
        private val appManager: AppManager,
        private val preferencesRepository: PreferencesRepository,
        private val notificationManager: NotificationManager,
    ) : CoroutineWorker(context, workerParameters) {
        private val debugLog = StringBuilder()

        override suspend fun doWork(): Result =
            withContext(Dispatchers.IO) {
                try {
                    Napier.d("🚀 Starting background sync")

                    performHeavyWork()

                    Napier.d("✨ Background sync completed successfully!")
                    Result.success()
                } catch (exception: Exception) {
                    Napier.e("❌ Background sync failed", exception)
                    Result.failure()
                }
            }

        private suspend fun performHeavyWork() {
            debugLog.setLength(0)
            debugLog.appendLine("🔄 Starting app synchronization...")

            val preferences = preferencesRepository.getUserPreferences().first()
            val isDebugEnabled = preferences.backgroundSync

            try {
                // Use the new integrated sync method (includes cleanup)
                appManager.syncAllApps()

                val packages = appManager.getPackagesWithUserPrefs()
                debugLog.appendLine("📱 Synced ${packages.size} apps with cleanup")
            } catch (e: Exception) {
                debugLog.appendLine("❌ Sync failed: ${e.message}")
                throw e
            }

            if (isDebugEnabled) {
                notificationManager.showDebugSyncNotification(
                    title = "🔄 Sync Complete",
                    text = "Apps synced with cleanup",
                    bigText = debugLog.toString(),
                )
            }

            debugLog.appendLine("🎉 Sync completed successfully!")
        }
    }
