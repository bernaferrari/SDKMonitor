package com.bernaferrari.sdkmonitor.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
    tableName = "apps",
    indices = [(Index(value = ["packageName"], unique = true))]
)
data class App(
    @PrimaryKey
    val packageName: String,
    val title: String,
    val backgroundColor: Int,
    val firstInstallTime: Long
)