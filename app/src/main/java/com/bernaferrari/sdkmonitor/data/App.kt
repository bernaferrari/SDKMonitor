package com.bernaferrari.sdkmonitor.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "apps",
    indices = [(Index(value = ["packageName"], unique = true))]
)
@Parcelize
data class App(
    @PrimaryKey
    val packageName: String,
    val title: String,
    val backgroundColor: Int,
    val isFromPlayStore: Boolean
) : Parcelable
