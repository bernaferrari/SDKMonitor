package com.bernaferrari.sdkmonitor.core

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import org.koin.core.annotation.Single

@Single
class SyncScheduler(
        private val context: Context,
    ) {
        private val workManager = WorkManager.getInstance(context)

        companion object {
            private const val SYNC_WORK_NAME = "work"
            private const val MIN_INTERVAL_MINUTES = 15L // WorkManager minimum
        }

        /**
         * Schedule periodic background sync
         */
        suspend fun schedulePeriodicSync(intervalString: String): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    val intervalMinutes = parseIntervalToMinutes(intervalString)

                    // Ensure minimum interval requirement
                    val actualInterval = maxOf(intervalMinutes, MIN_INTERVAL_MINUTES)

                    if (actualInterval != intervalMinutes) {
                        Napier.w("⚠️ Adjusted sync interval from ${intervalMinutes}m to ${actualInterval}m (WorkManager minimum)")
                    }

                    val constraints =
                        Constraints
                            .Builder()
                            .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // No network needed for local sync
                            .setRequiresBatteryNotLow(true) // Don't run when battery is low
                            .setRequiresDeviceIdle(false) // Can run when device is active
                            .build()

                    val syncWorkRequest =
                        PeriodicWorkRequestBuilder<SyncWorker>(
                            actualInterval,
                            TimeUnit.MINUTES,
                        ).setConstraints(constraints)
                            .addTag("work")
                            .build()

                    // Schedule the work
                    workManager.enqueueUniquePeriodicWork(
                        SYNC_WORK_NAME,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        syncWorkRequest,
                    )

                    Napier.d("✅ Scheduled background sync every $actualInterval minutes")
                    true
                } catch (e: Exception) {
                    Napier.e("❌ Failed to schedule background sync", e)
                    false
                }
            }

        /**
         * Cancel all background sync work
         */
        suspend fun cancelPeriodicSync(): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    workManager.cancelUniqueWork(SYNC_WORK_NAME)
                    Napier.d("🛑 Cancelled background sync")
                    true
                } catch (e: Exception) {
                    Napier.e("❌ Failed to cancel background sync", e)
                    false
                }
            }

        /**
         * Check if background sync is currently scheduled (using Flow API)
         */
        suspend fun isSyncScheduled(): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    val workInfos = workManager.getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME).first()

                    val hasScheduledWork =
                        workInfos.any { workInfo ->
                            workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
                        }

                    Napier.d("🔍 Sync scheduled status: $hasScheduledWork (found ${workInfos.size} work items)")
                    hasScheduledWork
                } catch (e: Exception) {
                    Napier.e("❌ Failed to check sync status", e)
                    false
                }
            }

        /**
         * Get current sync work status for debugging (using Flow API)
         */
        suspend fun getSyncWorkStatus(): String =
            withContext(Dispatchers.IO) {
                try {
                    val workInfos = workManager.getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME).first()

                    if (workInfos.isEmpty()) {
                        "No work scheduled"
                    } else {
                        workInfos.joinToString(", ") { "${it.state}" }
                    }
                } catch (e: Exception) {
                    "Error checking status: ${e.message}"
                }
            }

        /**
         * Parse interval string to minutes
         * Supports formats like "30m", "1h", "2d", "7d", "30d"
         */
        private fun parseIntervalToMinutes(interval: String): Long =
            try {
                when {
                    interval.endsWith("m") -> {
                        interval.dropLast(1).toLong()
                    }

                    interval.endsWith("h") -> {
                        interval.dropLast(1).toLong() * 60
                    }

                    interval.endsWith("d") -> {
                        interval.dropLast(1).toLong() * 24 * 60
                    }

                    // Handle legacy numeric format (assume hours)
                    interval.toIntOrNull() != null -> {
                        interval.toLong() * 60
                    }

                    else -> {
                        Napier.w("⚠️ Unknown interval format: $interval, defaulting to 7 days")
                        7 * 24 * 60 // 7 days in minutes
                    }
                }
            } catch (e: Exception) {
                Napier.e("❌ Failed to parse interval: $interval", e)
                7 * 24 * 60 // Default to 7 days
            }
    }
