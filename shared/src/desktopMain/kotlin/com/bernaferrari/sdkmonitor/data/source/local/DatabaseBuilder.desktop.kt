package com.bernaferrari.sdkmonitor.data.source.local

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

fun createAppDatabase(): AppDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "sdkmonitor-demo.db")
    return Room
        .databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
        .configureAppDatabase(BundledSQLiteDriver())
}
