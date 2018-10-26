package com.bernaferrari.sdkmonitor

import android.content.SharedPreferences
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


/**
 * Helps to deal with Work Manager.
 */
object WorkerHelper {

    const val UNIQUEWORK = "work"
    const val WIFI = "wifi"
    const val CHARGING = "charging"
    const val BATTERYNOTLOW = "batteryNotLow"
    const val IDLE = "idle"
    const val DELAY = "delay"

    class ConstraintsRequired(
        val charging: Boolean,
        val batteryNotLow: Boolean
    )

    fun updateWorkerWithConstraints(
        sharedPrefs: SharedPreferences,
        cancelCurrentWork: Boolean = true
    ) {

        val constraints = ConstraintsRequired(
            sharedPrefs.getBoolean(WorkerHelper.CHARGING, false),
            sharedPrefs.getBoolean(WorkerHelper.BATTERYNOTLOW, false)
        )

        if (cancelCurrentWork) {
            cancelWork()
        }
        WorkManager.getInstance().pruneWork()

        if (sharedPrefs.getBoolean("backgroundSync", false)) {
            reloadWorkManager(sharedPrefs.getLong(DELAY, 30), constraints)
        }
    }

    private fun reloadWorkManager(delay: Long = 15, constraints: WorkerHelper.ConstraintsRequired) {

        val workerConstraints = Constraints.Builder().apply {
            if (constraints.batteryNotLow) this.setRequiresBatteryNotLow(true)
            if (constraints.charging) this.setRequiresCharging(true)
        }

        val syncWork = OneTimeWorkRequest.Builder(SyncWorker::class.java)
            .addTag(UNIQUEWORK)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setConstraints(workerConstraints.build())
            .build()

        WorkManager.getInstance().enqueue(syncWork)
    }

    fun cancelWork() {
        WorkManager.getInstance().cancelAllWorkByTag(UNIQUEWORK)
    }
}
