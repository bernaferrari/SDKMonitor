package com.bernaferrari.sdkmonitor.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Ignore
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "versions",
    indices = [Index(value = ["packageName", "versionId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = App::class,
            parentColumns = ["packageName"],
            childColumns = ["packageName"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class Version(
    @PrimaryKey
    val versionId: Int,
    val version: Long,
    val packageName: String,
    val versionName: String,
    val lastUpdateTime: Long,
    val targetSdk: Int,
) {
    @Ignore
    constructor(
        version: Long,
        packageName: String,
        versionName: String,
        lastUpdateTime: Long,
        targetSdk: Int,
    ) : this(
        versionId = "$packageName $version $versionName $targetSdk".hashCode(),
        version = version,
        packageName = packageName,
        versionName = versionName,
        lastUpdateTime = lastUpdateTime,
        targetSdk = targetSdk,
    )
}
