package com.fyp.agrifarm.app.weather.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "HourlyForecasts")
public class HourlyObject {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "time")
    @SerializedName("dt")
    @Expose
    Integer time;
    @ColumnInfo(name = "temp")
    @SerializedName("temp")
    @Expose
    Double temperature;

    public HourlyObject(String id, Integer time, Double temperature) {
        this.id = id;
        this.time = time;
        this.temperature = temperature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
