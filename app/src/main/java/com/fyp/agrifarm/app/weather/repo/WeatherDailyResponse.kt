package com.fyp.agrifarm.app.weather.repo
import com.google.gson.annotations.SerializedName

data class WeatherDailyResponse (

        @SerializedName("weather")
    val weather: List<Weather>,

        @SerializedName("temp")
    val temp: Temp,

        @SerializedName("wind_speed")
    val wind: Double,

        @SerializedName("dt")
    val dateTime: Long,

        @SerializedName("id")
    val id: Int,

        @SerializedName("name")
    val name: String,


    @SerializedName("humidity")
    val humidity: Double
)