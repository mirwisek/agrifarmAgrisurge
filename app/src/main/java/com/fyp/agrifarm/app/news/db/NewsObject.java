package com.fyp.agrifarm.app.news.db;

import java.util.ArrayList;

public class NewsObject {
    String image;
    String link;
    String guid;
    ArrayList<String> categories;

    public NewsObject() {
    }

    public NewsObject(String image, String link, String guid, ArrayList<String> categories) {
        this.image = image;
        this.link = link;
        this.guid = guid;
        this.categories = categories;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
}
