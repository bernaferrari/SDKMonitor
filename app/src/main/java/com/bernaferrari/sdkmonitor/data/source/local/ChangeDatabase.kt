package com.bernaferrari.sdkmonitor.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version

/**
 * The Room Database that contains the Version table.
 * Inspired from Architecture Components MVVM sample app
 */
@Database(entities = [App::class, Version::class], version = 3, exportSchema = false)
abstract class ChangeDatabase : RoomDatabase() {

    abstract fun snapsDao(): AppsDao

    abstract fun versionsDao(): VersionsDao
}
