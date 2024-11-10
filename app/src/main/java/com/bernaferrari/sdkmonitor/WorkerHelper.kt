package com.bernaferrari.sdkmonitor

import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Helps to deal with Work Manager.
 */
object WorkerHelper {

    const val UNIQUEWORK = "work"
    const val SERVICEWORK = "work"
    const val CHARGING = "charging"
    const val BATTERYNOTLOW = "batteryNotLow"

    class ConstraintsRequired(
        val charging: Boolean,
        val batteryNotLow: Boolean
    )

    fun updateBackgroundWorker(cancelCurrentWork: Boolean = true) {
        if (cancelCurrentWork) cancelWork()
        WorkManager.getInstance().pruneWork()

        if (Injector.get().backgroundSync().get()) {

            val sharedPrefs = Injector.get().sharedPrefs()

            val constraints = ConstraintsRequired(
                sharedPrefs.getBoolean(WorkerHelper.CHARGING, false),
                sharedPrefs.getBoolean(WorkerHelper.BATTERYNOTLOW, false)
            )

            loadWorkManager(Injector.get().syncInterval().get(), constraints)
        }
    }

    private fun loadWorkManager(delay: String, constraints: WorkerHelper.ConstraintsRequired) {

        val workerConstraints = Constraints.Builder().apply {
            if (constraints.batteryNotLow) this.setRequiresBatteryNotLow(true)
            if (constraints.charging) this.setRequiresCharging(true)
        }

        val realDelay = delay.substring(1).toLong()

        val timeUnit = when (delay.substring(0, 1).toInt()) {
            1 -> TimeUnit.MINUTES
            2 -> TimeUnit.HOURS
            else -> TimeUnit.DAYS
        }

        val syncWork = OneTimeWorkRequest.Builder(SyncWorker::class.java)
            .addTag(UNIQUEWORK)
            .setInitialDelay(realDelay, timeUnit)
            .setConstraints(workerConstraints.build())
            .build()

        WorkManager.getInstance().enqueue(syncWork)
    }

    private fun cancelWork() {
        WorkManager.getInstance().cancelAllWorkByTag(UNIQUEWORK)
    }
}
