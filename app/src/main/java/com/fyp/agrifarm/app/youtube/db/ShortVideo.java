package com.fyp.agrifarm.app.youtube.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(tableName = ShortVideo.TABLE_NAME)
public class ShortVideo {

    public static final String TABLE_NAME = "videosTable";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id = "DUMMY_VALUE";

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "thumbnail")
    private String thumbnail;

    @ColumnInfo(name = "channelTitle")
    private String channelTitle;

    @ColumnInfo(name = "duration")
    private String duration;

    @ColumnInfo(name = "publishedDate")
    private DateTime publishedDate;

    public ShortVideo(@NonNull String id, String title, DateTime publishedDate, String thumbnail, String channelTitle,
                      String duration) {
        this.id = id;
        this.title = title;
        this.publishedDate = publishedDate;
        this.thumbnail = thumbnail;
        this.channelTitle = channelTitle;
        this.duration = duration;
    }

    @Ignore
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

    public String getPublishedDateString(){
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("DD, MMM, YYYY", Locale.US);
        Date d = new Date(publishedDate.getValue());

        return outputDateFormat.format(d);
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
