package com.fyp.agrifarm.repo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NewsDoa {

    // If primary keys clash occurs, replace with latest data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NewsEntity news);

    @Query("DELETE FROM news_table")
    void deleteAllNews();

    @Query("SELECT * FROM news_table ")
    LiveData<List<NewsEntity>> getAllNews();
}
