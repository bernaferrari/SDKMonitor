package com.bernaferrari.sdkmonitor.data

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "apps",
    indices = [Index(value = ["packageName"], unique = true)],
)
data class App(
    @PrimaryKey
    val packageName: String,
    val title: String,
    val backgroundColor: Int,
    val isFromPlayStore: Boolean,
)
