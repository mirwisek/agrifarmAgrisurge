package com.fyp.agrifarm.app.weather.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fyp.agrifarm.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherSharedViewModel extends AndroidViewModel {
    private Application app;
    private MutableLiveData<List<HourlyObject>> hourlyList;
    private MutableLiveData<List<DailyObject>> dailyList;
    private MutableLiveData<CurrentWeatherObject> currentWeathetObject;
    private WeatherRepository weatherRepository;
    public Map<String, Integer> weatherIconsMap = new HashMap<>();

    public WeatherSharedViewModel(@NonNull Application app) {
        super(app);
        this.app = app;
        loadMapIcons();
    }

    public void init()
    {
        if (hourlyList != null)
        {
            return;
        }
        weatherRepository = new WeatherRepository().getInstance();
        weatherRepository.getHourlyForecastByApi(app);
        hourlyList = weatherRepository.hourlyList;

        weatherRepository.getDailyListForecastByApi(app);
        dailyList = weatherRepository.dailyList;

        weatherRepository.FetchDailyForecast(app);
        currentWeathetObject = weatherRepository.currentWeather;

    }

    public LiveData<List<HourlyObject>> gethourlyList()
    {
        return hourlyList;
    }
    public LiveData<List<DailyObject>> getDailyList()
    {
        return dailyList;
    }
    public LiveData<CurrentWeatherObject> getCurrentWeather()
    {
        return currentWeathetObject;
    }

    private void loadMapIcons() {
        weatherIconsMap.put("01d", R.drawable.ic_wi_day_sunny);
        weatherIconsMap.put("02d", R.drawable.ic_wi_day_cloudy);
        weatherIconsMap.put("03d", R.drawable.ic_wi_cloud);
        weatherIconsMap.put("04d", R.drawable.ic_wi_cloudy);
        weatherIconsMap.put("09d", R.drawable.ic_wi_showers);
        weatherIconsMap.put("10d", R.drawable.ic_wi_day_rain_mix);
        weatherIconsMap.put("11d", R.drawable.ic_wi_thunderstorm);
        weatherIconsMap.put("13d", R.drawable.ic_wi_snow);
        weatherIconsMap.put("50d", R.drawable.ic_wi_fog);
        weatherIconsMap.put("04n", R.drawable.ic_wi_cloudy);
    }

}
