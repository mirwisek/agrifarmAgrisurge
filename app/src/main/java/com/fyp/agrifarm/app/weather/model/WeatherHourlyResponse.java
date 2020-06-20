package com.fyp.agrifarm.app.weather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherHourlyResponse {
    @SerializedName("hourly")
    @Expose
    public List<HourlyObject> weatherHourly;
    @SerializedName("daily")
    @Expose
    public List<DailyObject> weatherDaily;
}


