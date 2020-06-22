package com.fyp.agrifarm.app.weather.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.weather.repo.WeatherRepository
import java.util.*

class WeatherSharedViewModel(private val app: Application) : AndroidViewModel(app) {

    private var weatherRepository = WeatherRepository.getInstance(app)

    var hourlyList = Transformations.map(weatherRepository.hourlyList) { it }
    var dailyList = Transformations.map(weatherRepository.dailyList) { it }
    var currentWeather = Transformations.map(weatherRepository.currentWeather) { it }

    @JvmField
    var weatherIconsMap: MutableMap<String, Int> = HashMap()


    fun updateWeather(weatherUnit: String?) {
        weatherRepository.setWeatherUnit(weatherUnit)
        weatherRepository.fetchWeather()
    }
}