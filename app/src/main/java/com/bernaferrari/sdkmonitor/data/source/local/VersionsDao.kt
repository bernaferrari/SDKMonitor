package com.bernaferrari.sdkmonitor.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bernaferrari.sdkmonitor.data.Version

/**
 * Data Access Object for the sites table.
 * Inspired from Architecture Components MVVM sample app
 */
@Dao
interface VersionsDao {

    @Query("SELECT targetSdk FROM versions WHERE packageName=:packageName ORDER BY version DESC LIMIT 1")
    fun getLastTargetSDK(packageName: String): Int?

    @Query("SELECT * FROM versions WHERE packageName=:packageName ORDER BY version DESC LIMIT 1")
    fun getLastValue(packageName: String): Version?

    @Query("SELECT * FROM versions WHERE packageName=:packageName ORDER BY version DESC")
    fun getAllValues(packageName: String): List<Version>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVersion(version: Version)

}
