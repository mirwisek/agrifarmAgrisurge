package com.fyp.agrifarm.app.weather.model

const val CELSIUS = "Celsius"
const val FAHRENHEIT = "Fahrenheit"

data class CurrentWeatherItem (
    val day: String,
    val temperature: String,
    val description: String,
    val humidity: String,
    val windPressure: String,
    val icon: Int?
)

data class DailyWeatherItem (
        val time: Long,
        val temperature: String,
        val title: String,
        val icon: Int?
)

data class HourlyWeatherItem (
        val time: Long,
        val temperature: String,
        val icon: Int?
)