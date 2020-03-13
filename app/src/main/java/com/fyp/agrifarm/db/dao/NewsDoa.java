package com.fyp.agrifarm.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fyp.agrifarm.db.entity.NewsEntity;

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
