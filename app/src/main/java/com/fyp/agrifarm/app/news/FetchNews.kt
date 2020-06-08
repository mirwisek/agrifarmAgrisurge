package com.fyp.agrifarm.app.news

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fyp.agrifarm.app.news.db.Converter
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

@Entity(tableName = "tableNews")
data class NewsItem(
        @PrimaryKey
        @ColumnInfo(name = "guid")
        val guid: String,
        @ColumnInfo(name = "pubDate")
        val pubDate: String,
        @ColumnInfo(name = "title")
        val title: String,
        @TypeConverters(Converter::class)
        @ColumnInfo(name = "categories")
        val categories: List<String>,
        @ColumnInfo(name = "link")
        val link: String,
        @ColumnInfo(name = "image")
        val image: String
)

interface ApiRequest {
    @GET("news/fetch")
    fun getNews(): Call<List<NewsItem>>
}