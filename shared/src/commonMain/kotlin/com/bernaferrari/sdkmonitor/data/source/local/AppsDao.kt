package com.bernaferrari.sdkmonitor.data.source.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.bernaferrari.sdkmonitor.data.App
import kotlinx.coroutines.flow.Flow

@Dao
interface AppsDao {
    @Query("SELECT * FROM apps WHERE (isFromPlayStore = :hasKnownOrigin) ORDER BY title COLLATE NOCASE ASC")
    fun getAppsListFlowFiltered(hasKnownOrigin: Boolean): Flow<List<App>>

    @Query("SELECT * FROM apps ORDER BY title COLLATE NOCASE ASC")
    fun getAppsListFlow(): Flow<List<App>>

    @Query("SELECT * FROM apps")
    suspend fun getAppsList(): List<App>

    @Query("SELECT packageName FROM apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppString(packageName: String): String?

    @Query("SELECT * FROM apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getApp(packageName: String): App?

    @Query("SELECT * FROM apps WHERE packageName = :packageName LIMIT 1")
    fun getAppFlow(packageName: String): Flow<App?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: App)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<App>)

    @Update
    suspend fun updateApp(app: App)

    @Query("DELETE FROM apps WHERE packageName = :packageName")
    suspend fun deleteApp(packageName: String)

    @Query("DELETE FROM apps")
    suspend fun deleteAllApps()

    @Query("SELECT COUNT(*) FROM apps")
    suspend fun getAppsCount(): Int
}
