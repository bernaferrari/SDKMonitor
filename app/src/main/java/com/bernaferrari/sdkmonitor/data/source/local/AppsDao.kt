package com.bernaferrari.sdkmonitor.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.bernaferrari.sdkmonitor.data.App
import io.reactivex.Flowable

/**
 * Data Access Object for the sites table.
 * Inspired from Architecture Components MVVM sample app
 */
@Dao
interface AppsDao {

    // The Int type parameter tells Room to use a PositionalDataSource
    // object, with position-based loading under the hood.
    @Query("SELECT * FROM apps ORDER BY title DESC")
    fun concertsByDate(): Flowable<List<App>>

    /**
     * Insert a snap in the database. If the snap already exists, replace it.
     *
     * @param snap the snap to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApp(app: App)

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
