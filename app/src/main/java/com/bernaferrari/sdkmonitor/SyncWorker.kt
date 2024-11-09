package com.bernaferrari.sdkmonitor

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bernaferrari.sdkmonitor.core.AppManager
import com.orhanobut.logger.Logger
import io.karn.notify.Notify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class SyncWorker(
    val context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    private val debugLog = StringBuilder()
    private val isDebugEnabled = false //Injector.get().sharedPrefs().getBoolean("debug", true)

    override fun doWork(): Result {
        heavyWork()
        WorkerHelper.updateBackgroundWorker(false)
        return Result.success()
    }

    private fun heavyWork() = runBlocking(Dispatchers.IO) {
        Logger.d("Doing background work! ")

        debugLog.setLength(0)

        AppManager.getPackagesWithUserPrefs().forEach {
            AppManager.insertNewApp(it)
            AppManager.insertNewVersion(it)
        }

        if (isDebugEnabled) {
            Notify.with(context)
                .meta {
                    this.clickIntent = PendingIntent.getActivity(
                        context, 0,
                        Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                .asBigText {
                    title = "[Debug] There has been a sync"
                    text = "Expand to see the full log"
                    expandedText = "..."
                    bigText = debugLog
                }
                .show()
        }
    }
}
