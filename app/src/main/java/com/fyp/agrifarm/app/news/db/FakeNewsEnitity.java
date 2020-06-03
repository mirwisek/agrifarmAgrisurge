package com.fyp.agrifarm.app.news.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;


@Entity(tableName = "news_table1")
public class FakeNewsEnitity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "guid")
    private String guid;

    @ColumnInfo(name = "link")
    private String link;

    @ColumnInfo(name = "image")
    private String image;

    @TypeConverters({Converter.class})
    @ColumnInfo(name = "categories")
    private ArrayList<String> catergories;

    @Ignore
    public FakeNewsEnitity() {
    }

    public FakeNewsEnitity(int id, String title, String guid, String link, String image, ArrayList<String> catergories) {
        this.id = id;
        this.title = title;
        this.guid = guid;
        this.link = link;
        this.image = image;
        this.catergories = catergories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getCatergories() {
        return catergories;
    }

    public void setCatergories(ArrayList<String> catergories) {
        this.catergories = catergories;
    }
}
