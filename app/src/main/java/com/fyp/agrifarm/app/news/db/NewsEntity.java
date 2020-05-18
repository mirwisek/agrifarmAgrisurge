package com.fyp.agrifarm.app.news.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "news_table")
public class NewsEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "date")
    private String date;

//    private Bitmap thumbnail;

    public NewsEntity(String title, String description, String url,String date) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.date=date;
    }
    @Ignore
    public NewsEntity(String title, String url, String date) {
        this.title = title;
        this.url = url;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }
}
