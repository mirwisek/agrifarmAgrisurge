package com.fyp.agrifarm.app.weather.repo

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
        @SerializedName("hourly")
        val hourly: List<WeatherItem>,

        @SerializedName("daily")
        val daily: List<WeatherDailyResponse>
)