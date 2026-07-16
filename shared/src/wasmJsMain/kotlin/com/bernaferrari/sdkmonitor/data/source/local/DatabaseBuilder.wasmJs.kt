package com.bernaferrari.sdkmonitor.data.source.local

import androidx.room3.Room
import com.bernaferrari.sdkmonitor.worker.createSQLiteWasmWorker

/** Room 3 on wasmJs: OPFS-backed SQLite in a Web Worker. */
fun createAppDatabase(): AppDatabase =
    Room
        .databaseBuilder<AppDatabase>(name = "sdkmonitor.db")
        .configureAppDatabase(createSQLiteWasmWorker())
