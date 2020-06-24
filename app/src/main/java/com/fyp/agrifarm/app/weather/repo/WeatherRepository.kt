package com.fyp.agrifarm.app.weather.repo

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.sharedPrefs
import com.fyp.agrifarm.app.toast
import com.fyp.agrifarm.app.weather.IconMapsUtil
import com.fyp.agrifarm.app.weather.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class WeatherRepository private constructor() {


    companion object {
        private val instance: WeatherRepository = WeatherRepository()
        private const val APP_ID = "e1f236cb66f5933607fd7905b42b39c9"
        private const val BASE_URL = "https://api.openweathermap.org/"
        private const val IMPERIAL = "imperial"
        private const val METRIC = "metric"

        fun getInstance(app: Application): WeatherRepository {
            instance.application = app
            return instance
        }
    }

    private lateinit var application: Application
    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private val service = retrofit.create(WeatherService::class.java)
    private val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)

    var hourlyList = MutableLiveData<List<HourlyWeatherItem>>()
    var dailyList = MutableLiveData<List<DailyWeatherItem>>()
    var currentWeather = MutableLiveData<CurrentWeatherItem>()

    private var weatherUnit: String? = null

    private var lat: String? = null
    private var lon: String? = null

    fun setWeatherUnit(newUnit: String?) {
        if (newUnit == null) return
        weatherUnit = if (newUnit.contentEquals("Fahrenheit")) IMPERIAL else METRIC
    }

    private fun getWeatherUnit() {
        if (weatherUnit == null) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
            val st = sharedPref.getString("weatherUnit", "-1")
            setWeatherUnit(st)
        }
    }

    private val location: Unit
        get() {
            log("$lat $lon")
            lat = application.applicationContext.sharedPrefs.getString("lat", null)
            lon = application.applicationContext.sharedPrefs.getString("lon", null)
        }

    private fun preprocess() {
        getWeatherUnit()
        location
    }

    //TODO Implement LocalCache
    fun fetchWeather() {
        preprocess()
        val call = service.fetchWeather(lat, lon, weatherUnit, "minutely", APP_ID)
        call.enqueue(object : Callback<WeatherResponse?> {
            override fun onResponse(call: Call<WeatherResponse?>, response: Response<WeatherResponse?>) {

                if (response.isSuccessful) {
                    val res = response.body()
                    if (res != null) {
                        parseHourlyWeather(res.hourly)
                        parseCurrentWeather(res.daily)
                        parseDailyForecast(res.daily)
                    }
                } else {
                    //TODO Implement LocalCache
                    log("Something wrong => ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse?>, t: Throwable) {
                application.toast("Couldn't retrieve updated weather")
                t.printStackTrace()
            }
        })
    }

    fun parseHourlyWeather(listHourly: List<WeatherItem>) {
        val mList = mutableListOf<HourlyWeatherItem>()
        // Just get 24 hours no more
        for(i in 0 until 24) {
            val wItem = listHourly[i]
            val weather = wItem.weather[0]
            val resIcon = IconMapsUtil.mapIcon(weather.icon)
            val temp = String.format(Locale.US, "%.0f", wItem.temp)
            mList.add(HourlyWeatherItem(wItem.dt, temp, resIcon))
        }
        hourlyList.postValue(mList)
    }

    fun parseDailyForecast(listDaily: List<WeatherDailyResponse>) {
        val map = IconMapsUtil.weatherIconMap
        val mList = mutableListOf<DailyWeatherItem>()
        log("Daily size: ${listDaily.size}")

        for (i in 1 until 7) {
            val res = listDaily[i]

            val weather = res.weather[0]
            val day = dayFormat.format(Date(res.dateTime))
            val title = weather.title
            val temp = String.format(Locale.US, "%.0f", res.temp.day)
            val resIcon = IconMapsUtil.mapIcon(weather.icon)
            mList.add(DailyWeatherItem(res.dateTime, temp, title, resIcon))
        }
        dailyList.postValue(mList)
    }

    fun parseCurrentWeather(listDaily: List<WeatherDailyResponse>) {

        val dailyResponse = listDaily[0]
        val weather = dailyResponse.weather[0]


        val d = Date()
        val dayOfTheWeek = dayFormat.format(d)

        val title = weather.title
        val temperature = String.format(Locale.US, "%.0f", dailyResponse.temp.day)
        val humidity = String.format(Locale.US, "%.0f", dailyResponse.humidity) + "%"

        // Default metric (meter/s) and imperial (miles/h)
        var windValue = dailyResponse.wind
        // Miles/h to KM/h
        weatherUnit?.let {
            windValue *= if (it.contentEquals(IMPERIAL)) {
                1.60934
            } else {
                // Meter/Sec to KM/H
                3.6
            }
        }

        val windPressure = String.format(Locale.US, "%.0f km/h", windValue)

        val resIcon = IconMapsUtil.mapIcon(weather.icon)
        val currentWeatherItem =
                CurrentWeatherItem(dayOfTheWeek, temperature, title, humidity, windPressure, resIcon)

        currentWeather.postValue(currentWeatherItem)
    }
}