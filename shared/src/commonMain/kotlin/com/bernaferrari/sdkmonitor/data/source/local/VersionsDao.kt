package com.bernaferrari.sdkmonitor.data.source.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.bernaferrari.sdkmonitor.data.Version
import kotlinx.coroutines.flow.Flow

@Dao
interface VersionsDao {
    @Query("SELECT targetSdk FROM versions WHERE packageName = :packageName ORDER BY version DESC LIMIT 1")
    suspend fun getLastTargetSDK(packageName: String): Int?

    @Query("SELECT * FROM versions WHERE packageName = :packageName ORDER BY version DESC LIMIT 1")
    suspend fun getLastValue(packageName: String): Version?

    @Query("SELECT * FROM versions WHERE packageName = :packageName ORDER BY version DESC")
    suspend fun getAllValues(packageName: String): List<Version>

    @Query("SELECT * FROM versions WHERE packageName = :packageName ORDER BY version DESC")
    fun getAllValuesFlow(packageName: String): Flow<List<Version>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVersion(version: Version)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVersions(versions: List<Version>)

    @Query("DELETE FROM versions WHERE packageName = :packageName")
    suspend fun deleteAllVersionsForApp(packageName: String)

    @Query(
        "SELECT t2.* FROM ( SELECT * FROM versions GROUP BY packageName HAVING COUNT(*) > 1 ) T1 JOIN versions T2 ON T1.packageName = T2.packageName ORDER BY lastUpdateTime DESC",
    )
    fun getAllChangesFlow(): Flow<List<Version>>

    @Query(
        "SELECT COUNT(*) FROM ( SELECT * FROM versions GROUP BY packageName HAVING COUNT(*) > 1 ) T1 JOIN versions T2 ON T1.packageName = T2.packageName",
    )
    suspend fun countNumberOfChanges(): Int

    @Query("DELETE FROM versions WHERE packageName = :packageName")
    suspend fun deleteVersionsForPackage(packageName: String)

    @Query("DELETE FROM versions")
    suspend fun deleteAllVersions()

    @Query("SELECT * FROM versions ORDER BY lastUpdateTime DESC")
    suspend fun getAllVersionsSync(): List<Version>

    @Query("SELECT * FROM versions ORDER BY lastUpdateTime DESC")
    fun getAllVersionsFlow(): Flow<List<Version>>
}
