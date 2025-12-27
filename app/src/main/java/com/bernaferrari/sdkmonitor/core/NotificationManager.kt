package com.bernaferrari.sdkmonitor.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bernaferrari.sdkmonitor.MainActivity
import com.bernaferrari.sdkmonitor.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        private val notificationManager = NotificationManagerCompat.from(context)

        companion object {
            private const val CHANNEL_ID_SDK_CHANGES = "sdk_changes"
            private const val CHANNEL_ID_DEBUG = "debug"
            private const val NOTIFICATION_ID_SDK_CHANGE = 1001
            private const val NOTIFICATION_ID_DEBUG = 1002
        }

        init {
            createNotificationChannels()
        }

        private fun createNotificationChannels() {
            val sdkChangesChannel =
                NotificationChannel(
                    CHANNEL_ID_SDK_CHANGES,
                    "SDK Changes",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Notifications for target SDK changes in apps"
                }

            val debugChannel =
                NotificationChannel(
                    CHANNEL_ID_DEBUG,
                    "Debug",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    description = "Debug notifications for background sync"
                }

            val systemNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            systemNotificationManager.createNotificationChannel(sdkChangesChannel)
            systemNotificationManager.createNotificationChannel(debugChannel)
        }

        /**
         * Show notification for SDK target change
         */
        fun showSdkChangeNotification(
            appName: String,
            packageName: String,
            oldSdk: Int,
            newSdk: Int,
            appIcon: Bitmap? = null,
        ) {
            if (!notificationManager.areNotificationsEnabled()) return

            val intent =
                Intent(context, MainActivity::class.java).apply {
                    putExtra("package_name", packageName)
                    putExtra("navigate_to_details", true)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    packageName.hashCode(), // Use package name hash as unique request code
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

            val sdkDirection = if (newSdk > oldSdk) "↗" else "↘"

            val contentText = "targetSDK $oldSdk → $newSdk"
            val bigText = "$appName\ntargetSDK $oldSdk → $newSdk"

            val notificationBuilder =
                NotificationCompat
                    .Builder(context, CHANNEL_ID_SDK_CHANGES)
                    .setSmallIcon(R.drawable.ic_target)
                    .setContentTitle(contentText)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(
                        NotificationCompat
                            .BigTextStyle()
                            .bigText(bigText)
                            .setBigContentTitle("$sdkDirection $appName"),
                    )

            // Convert app icon to bitmap and set as large icon
            appIcon?.let { icon ->
                notificationBuilder.setLargeIcon(icon)
            }

            notificationManager.notify(
                NOTIFICATION_ID_SDK_CHANGE + packageName.hashCode(),
                notificationBuilder.build(),
            )
        }

        /**
         * Show debug notification for background sync
         */
        fun showDebugSyncNotification(
            title: String,
            text: String,
            bigText: String,
        ) {
            if (!notificationManager.areNotificationsEnabled()) return

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )

            val bigTextStyle =
                NotificationCompat
                    .BigTextStyle()
                    .bigText(bigText)
                    .setBigContentTitle(title)
                    .setSummaryText(text)

            val notification =
                NotificationCompat
                    .Builder(context, CHANNEL_ID_DEBUG)
                    .setSmallIcon(R.drawable.ic_target)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setStyle(bigTextStyle)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()

            notificationManager.notify(NOTIFICATION_ID_DEBUG, notification)
        }
    }
