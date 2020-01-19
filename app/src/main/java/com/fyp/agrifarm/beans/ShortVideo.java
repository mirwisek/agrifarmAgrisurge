package com.fyp.agrifarm.beans;

import com.google.api.client.util.DateTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShortVideo {

    private String id,title,thumbnail, channelTitle, duration;
    private DateTime publishedDate;

    public ShortVideo(String id, String title, DateTime publishedDate, String thumbnail, String channelTitle,
                      String duration) {
        this.id = id;
        this.title = title;
        this.publishedDate = publishedDate;
        this.thumbnail = thumbnail;
        this.channelTitle = channelTitle;
        this.duration = duration;
    }

    public ShortVideo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public DateTime getPublishDate() {
        return publishedDate;
    }

    public void setPublishDate(DateTime publishDate) {
        this.publishedDate = publishDate;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public DateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(DateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void reformatDuration(){
        if(duration.length() <= 0)
            return;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher m = pattern.matcher(duration);
        StringBuilder sb = new StringBuilder();
        while(m.find()){
            sb.append(m.group());
            sb.append(":");
        }
        duration = sb.toString().substring(0, sb.length()-1);
    }
}
