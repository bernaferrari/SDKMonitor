package com.bernaferrari.sdkmonitor.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version

/**
 * Inspired from Architecture Components MVVM sample app
 */
@Database(entities = [App::class, Version::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun snapsDao(): AppsDao

    abstract fun versionsDao(): VersionsDao
}
