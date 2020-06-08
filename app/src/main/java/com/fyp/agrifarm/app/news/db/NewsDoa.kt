package com.fyp.agrifarm.app.news.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fyp.agrifarm.app.news.NewsItem

@Dao
interface NewsDoa {
    // If primary keys clash occurs, replace with latest data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg news: NewsItem)

    @Query("DELETE FROM tableNews")
    fun deleteAllNews()


    @get:Query("SELECT * FROM tableNews")
    val allNews: List<NewsItem>
}