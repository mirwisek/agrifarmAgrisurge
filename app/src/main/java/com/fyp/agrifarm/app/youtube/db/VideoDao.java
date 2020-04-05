package com.fyp.agrifarm.app.youtube.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ShortVideo video);

    @Query("DELETE FROM " + ShortVideo.TABLE_NAME)
    void deleteAll();

    @Query("SELECT * FROM " + ShortVideo.TABLE_NAME)
    LiveData<List<ShortVideo>> getAllVideos();
}
