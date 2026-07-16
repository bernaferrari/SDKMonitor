package com.bernaferrari.sdkmonitor.data.source.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.sqlite.SQLiteDriver
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version
import kotlinx.coroutines.Dispatchers

/**
 * Room 3 database in [commonMain] — entities/DAOs/DB definition are fully multiplatform.
 * Platform code only supplies [androidx.room3.Room.databaseBuilder] + [SQLiteDriver].
 */
@Database(entities = [App::class, Version::class], version = 1, exportSchema = true)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun snapsDao(): AppsDao

    abstract fun versionsDao(): VersionsDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

/** Shared builder finish: driver + coroutine context (works on JVM/Android/wasmJs). */
fun RoomDatabase.Builder<AppDatabase>.configureAppDatabase(driver: SQLiteDriver): AppDatabase =
    this
        .fallbackToDestructiveMigration(dropAllTables = false)
        .setDriver(driver)
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
