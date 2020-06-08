package com.fyp.agrifarm.app.youtube.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg video: ExtendedVideo)

    @Query("DELETE FROM " + ExtendedVideo.TABLE_NAME)
    fun deleteAll()

    @get:Query("SELECT * FROM " + ExtendedVideo.TABLE_NAME)
    val allVideos: List<ExtendedVideo>
}