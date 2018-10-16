package com.bernaferrari.sdkmonitor.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.bernaferrari.sdkmonitor.data.Version

/**
 * Data Access Object for the sites table.
 * Inspired from Architecture Components MVVM sample app
 */
@Dao
interface VersionsDao {


    // The Int type parameter tells Room to use a PositionalDataSource
    // object, with position-based loading under the hood.
    @Query("SELECT EXISTS(SELECT 1 FROM versions WHERE version=:version)")
    fun checkIfExists(version: Long): Boolean


    /**
     * Insert a snap in the database. If the snap already exists, replace it.
     *
     * @param snap the snap to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVersion(version: Version)

}
