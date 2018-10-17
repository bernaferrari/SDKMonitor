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


    // The Int type parameter tells Room to use a PositionalDataSource
    // object, with position-based loading under the hood.
    @Query("SELECT * FROM versions WHERE packageName=:packageName ORDER BY version DESC LIMIT 1")
    fun getValue(packageName: String): Version?


    // The Int type parameter tells Room to use a PositionalDataSource
    // object, with position-based loading under the hood.
    @Query("SELECT EXISTS(SELECT 1 FROM versions WHERE version=:version)")
    fun checkIfExists(version: Long): Int


    /**
     * Insert a app in the database. If the app already exists, replace it.
     *
     * @param app the app to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVersion(version: Version)

}
