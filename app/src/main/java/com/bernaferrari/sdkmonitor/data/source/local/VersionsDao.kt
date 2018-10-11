package com.bernaferrari.sdkmonitor.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import com.bernaferrari.sdkmonitor.data.Version

/**
 * Data Access Object for the sites table.
 * Inspired from Architecture Components MVVM sample app
 */
@Dao
interface VersionsDao {

    /**
     * Insert a snap in the database. If the snap already exists, replace it.
     *
     * @param snap the snap to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVersion(version: Version)


}
