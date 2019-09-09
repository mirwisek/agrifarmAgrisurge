package com.fyp.agrifarm.beans;

import android.graphics.Bitmap;

public class News {

    String url;
    String title;
    Bitmap thumbnail;

    public News(String url, String title, Bitmap thumbnail) {
        this.url = url;
        this.title = title;
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
