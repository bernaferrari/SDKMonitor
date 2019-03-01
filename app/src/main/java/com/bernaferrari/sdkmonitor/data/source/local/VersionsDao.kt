package com.bernaferrari.sdkmonitor.data.source.local

import androidx.paging.DataSource
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

    // this will only get versions where there is more than one version for the same package.
    // So, if a package was recently added, there is no reason to be there.
    @Query("SELECT t2.* FROM ( SELECT * FROM versions GROUP BY packageName HAVING COUNT(*) > 1 ) T1 JOIN versions T2 ON T1.packageName = T2.packageName ORDER BY lastUpdateTime DESC")
    fun getVersionsPaged(): DataSource.Factory<Int, Version>

    @Query("SELECT COUNT(*) FROM ( SELECT * FROM versions GROUP BY packageName HAVING COUNT(*) > 1 ) T1 JOIN versions T2 ON T1.packageName = T2.packageName")
    fun countNumberOfChanges(): Int

}
