package com.bernaferrari.sdkmonitor.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bernaferrari.sdkmonitor.data.App
import io.reactivex.Flowable

/**
 * Data Access Object for the sites table.
 * Inspired from Architecture Components MVVM sample app
 */
@Dao
interface AppsDao {

    @Query("SELECT * FROM apps ORDER BY title DESC")
    fun getAppsList(): Flowable<List<App>>

    /**
     * Insert a app in the database. If the app already exists, replace it.
     *
     * @param app the app to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertApp(app: App)

    @Query("SELECT packageName FROM apps WHERE packageName =:packageName LIMIT 1")
    fun getApp(packageName: String): String?

    /**
     * Delete all snaps.
     */
    @Query("DELETE FROM apps WHERE packageName = :packageName")
    fun deleteApp(packageName: String)

    /**
     * Delete all snaps.
     */
    @Query("DELETE FROM apps")
    fun deleteTasks()
}
