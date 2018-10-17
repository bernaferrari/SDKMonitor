package com.bernaferrari.sdkmonitor.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Immutable model class for a Version.
 * Inspired from Architecture Components MVVM sample app
 */
@Entity(
    tableName = "versions",
    indices = [(Index(value = ["version", "packageName"], unique = true))],
    foreignKeys = [(
            ForeignKey(
                entity = App::class,
                parentColumns = arrayOf("packageName"),
                childColumns = arrayOf("packageName"),
                onDelete = ForeignKey.CASCADE
            )
            )
    ]
)
data class Version(
    @PrimaryKey
    val version: Long,
    val packageName: String,
    val versionName: String,
    val lastUpdateTime: Long,
    val targetSdk: Int//,
//    val className: String,
//    val sourceDir: String,
//    val dataDir: String
)
