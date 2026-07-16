package com.bernaferrari.sdkmonitor.data.source.local

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun createAppDatabase(context: Context): AppDatabase {
    val appContext = context.applicationContext
    val dbPath = appContext.getDatabasePath("Apps.db").absolutePath
    return Room
        .databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbPath,
        ).configureAppDatabase(BundledSQLiteDriver())
}
