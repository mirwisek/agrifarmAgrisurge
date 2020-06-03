package com.fyp.agrifarm.app.news.db;

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
    void insert(FakeNewsEnitity news);

    @Query("DELETE FROM news_table1")
    void deleteAllNews();

    @Query("SELECT * FROM news_table1 ")
    LiveData<List<FakeNewsEnitity>> getAllNews();
}
