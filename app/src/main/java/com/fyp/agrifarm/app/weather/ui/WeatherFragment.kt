package com.fyp.agrifarm.app.weather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.toastFrag
import com.fyp.agrifarm.app.weather.model.CELSIUS
import kotlinx.android.synthetic.main.content_weather.*
import kotlinx.android.synthetic.main.content_weather.view.*
import java.util.*


class WeatherFragment : Fragment() {
    //    private WeatherViewModel weatherViewModel;
    private lateinit var weatherModel: WeatherSharedViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val parent = inflater.inflate(R.layout.content_weather, container,
                false) as ConstraintLayout


        val rvHourlyForecast: RecyclerView = parent.findViewById(R.id.rvHourlyForecast)
        val hourlyRecyclerAdapter = WeatherHourlyRecyclerAdapter(context)

        weatherModel = ViewModelProvider(this).get(WeatherSharedViewModel::class.java)
        weatherModel.updateWeather(null)

        weatherModel.hourlyList.observe(viewLifecycleOwner, Observer {
            hourlyRecyclerAdapter.updateList(it)
        })

        rvHourlyForecast.adapter = hourlyRecyclerAdapter

        val rvDailyForecast: RecyclerView = parent.findViewById(R.id.rvDailyForecast)
        val dailyRecyclerAdapter = WeatherDailyRecyclerAdapter(context)

        weatherModel.dailyList.observe(viewLifecycleOwner, Observer {
            dailyRecyclerAdapter.updateList(it)
        })
        rvDailyForecast.adapter = dailyRecyclerAdapter


        var isNight = false
        val c = Calendar.getInstance()
        val timeOfDay = c[Calendar.HOUR_OF_DAY]

        if (timeOfDay in 0..5) {
            isNight = true
        } else if (timeOfDay in 6..18) {
            isNight = false
        } else if (timeOfDay in 19..23) {
            isNight = true
        }

        val res = if (isNight) R.mipmap.bg_weather_night else R.mipmap.bg_weather_day
        parent.bgWeather.setImageResource(res)

        val wftemperatute = parent.findViewById<TextView>(R.id.wftvWeatherTemp)
        val wfdescription = parent.findViewById<TextView>(R.id.wfWeatherDescription)
        val wfwindpressure = parent.findViewById<TextView>(R.id.wftvWeatherWind)
        val wfhumidity = parent.findViewById<TextView>(R.id.wftvWeatherHumidity)
        val weatherIcon = parent.findViewById<ImageView>(R.id.wfivWeatherIcon)


        weatherModel.currentWeather.observe(viewLifecycleOwner, Observer {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val weatherPref = sharedPref.getString("weatherUnit", "-1")
            val temperature = if (weatherPref == CELSIUS) {
                it.temperature + "°C"
            } else {
                it.temperature + "°F"
            }
            wftemperatute.text = temperature

            wfdescription.text = it.description
            wfhumidity.text = it.humidity

            // Wrapped with catch incase resource ID not found

            try {
                it.icon?.let {  ic ->
                    weatherIcon.setImageResource(ic)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        return parent
    }

    companion object {
        const val TAG = "WeatherFragment"
    }
}