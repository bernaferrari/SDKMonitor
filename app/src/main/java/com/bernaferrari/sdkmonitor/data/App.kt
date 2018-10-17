package com.bernaferrari.sdkmonitor.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "apps",
    indices = [(Index(value = ["packageName"], unique = true))]
)
data class App(
    @PrimaryKey
    val packageName: String,
    val title: String,
    val backgroundColor: Int,
    val firstInstallTime: Long,
    val isFromPlayStore: Boolean
)