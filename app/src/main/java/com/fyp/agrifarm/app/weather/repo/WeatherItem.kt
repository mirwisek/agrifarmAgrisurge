package com.fyp.agrifarm.app.weather.repo

import com.google.gson.annotations.SerializedName

data class WeatherItem(

        @SerializedName("dt")
        val dt: Long,

        @SerializedName("temp")
        val temp: Double,

        @SerializedName("weather")
        val weather: List<Weather>
)

data class Weather (
        @SerializedName("main")
        val title: String,

        @SerializedName("description")
        val description: String,

        @SerializedName("icon")
        val icon: String
)

data class Temp(
        @SerializedName("day")
        val day: Double,
        @SerializedName("min")
        val min: Double,
        @SerializedName("max")
        val max: Double,
        @SerializedName("night")
        val night: Double,
        @SerializedName("eve")
        val eve: Double,
        @SerializedName("morn")
        val morn: Double
)
