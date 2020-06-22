package com.fyp.agrifarm.app.weather

import com.fyp.agrifarm.R
import java.util.*

object IconMapsUtil {

    val weatherIconMap = HashMap<String, Int>()

    init {
        weatherIconMap.put("01d", R.drawable.ic_wi_day_sunny)
        weatherIconMap.put("02d", R.drawable.ic_wi_day_cloudy)
        weatherIconMap.put("03d", R.drawable.ic_wi_cloud)
        weatherIconMap.put("04d", R.drawable.ic_wi_cloudy)
        weatherIconMap.put("09d", R.drawable.ic_wi_showers)
        weatherIconMap.put("10d", R.drawable.ic_wi_day_rain_mix)
        weatherIconMap.put("11d", R.drawable.ic_wi_thunderstorm)
        weatherIconMap.put("13d", R.drawable.ic_wi_snow)
        weatherIconMap.put("50d", R.drawable.ic_wi_fog)
        weatherIconMap.put("04n", R.drawable.ic_wi_cloudy)
    }

    fun mapIcon(value: String) : Int? {
        return weatherIconMap[value]
    }

}