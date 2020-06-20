package com.fyp.agrifarm.app.weather.model;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.google.type.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.fyp.agrifarm.app.ExtensionsKt.getSharedPrefs;

public class WeatherRepository {

    private static WeatherRepository instance;
    private static final String APP_ID = "e1f236cb66f5933607fd7905b42b39c9";
    private static final String BASE_URL = "https://api.openweathermap.org/";
    private List<HourlyObject> hourlyObject = new ArrayList<>();
    public MutableLiveData<List<HourlyObject>> hourlyList = new MutableLiveData<>();
    private List<DailyObject> dailyObject = new ArrayList<>();
    public MutableLiveData<List<DailyObject>> dailyList = new MutableLiveData<>();
    public MutableLiveData<CurrentWeatherObject> currentWeather = new MutableLiveData<>();

    public WeatherRepository getInstance() {
        if (instance == null) {
            instance = new WeatherRepository();
        }
        return instance;
    }

    private String getWeatherUnit(Application application) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
        String WeatherPref = sharedPref.getString("weatherUnit", "-1");
        return WeatherPref;
    }

    private LatLng getLocation(Application application) {
        String latitude = getSharedPrefs(application.getApplicationContext()).getString("lat", null);
        String longitude = getSharedPrefs(application.getApplicationContext()).getString("lon", null);
        try {
            double lat = Double.parseDouble(latitude);
            double lng = Double.parseDouble(longitude);
            return LatLng.newBuilder().setLatitude(lat).setLongitude(lng).build();
        } catch (Exception e) {
            return null;
        }
    }

    public void getHourlyForecastByApi(Application application) {

        String unit = getWeatherUnit(application);
        String lat = null;
        String lon = null;

        LatLng location = getLocation(application);
        if (location != null) {
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
        }

        if (unit.equals("Celsius")) {
            unit = "metric";
        } else if (unit.equals("imperial")) {
            unit = "imperial";

        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherHourlyResponse> call = service.getHourlyForecastList(lat, lon, unit, "current", APP_ID);
        call.enqueue(new Callback<WeatherHourlyResponse>() {
            @Override
            public void onResponse(Call<WeatherHourlyResponse> call, Response<WeatherHourlyResponse> response) {
                if (response.isSuccessful()) {
                    WeatherHourlyResponse weatherHourlyResponse = response.body();
                    hourlyObject = weatherHourlyResponse.weatherHourly;
                    hourlyList.postValue(hourlyObject);
                } else {
                    //TODO Implement LocalCache
                }
            }

            @Override
            public void onFailure(Call<WeatherHourlyResponse> call, Throwable t) {
                Toast.makeText(application, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDailyListForecastByApi(Application application) {

        String unit = getWeatherUnit(application);
        String lat = null;
        String lon = null;

        LatLng location = getLocation(application);
        if (location != null) {
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
        }

        if (unit.equals("Celsius")) {
            unit = "metric";
        } else {
            unit = "imperial";

        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherHourlyResponse> call = service.getDailyForecastList(lat, lon, unit, "current", APP_ID);
        call.enqueue(new Callback<WeatherHourlyResponse>() {
            @Override
            public void onResponse(Call<WeatherHourlyResponse> call, Response<WeatherHourlyResponse> response) {
                if (response.isSuccessful()) {
                    WeatherHourlyResponse weatherHourlyResponse = response.body();
                    dailyObject = weatherHourlyResponse.weatherDaily;
                    dailyList.postValue(dailyObject);
                } else {
                    //TODO Implement LocalCache
                }
            }

            @Override
            public void onFailure(Call<WeatherHourlyResponse> call, Throwable t) {
                Toast.makeText(application, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void FetchDailyForecast(Application application) {
        String unit = getWeatherUnit(application);
        String lat = null;
        String lon = null;

        LatLng location = getLocation(application);
        if (location != null) {
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
        }

        if (unit.equals("Celsius")) {
            unit = "metric";
        } else {
            unit = "imperial";

        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherDailyResponse> call = service.getCurrentWeatherData(lat, lon, APP_ID, unit);
        call.enqueue(new Callback<WeatherDailyResponse>() {
            @Override
            public void onResponse(Call<WeatherDailyResponse> call, Response<WeatherDailyResponse> response) {
                if (response.isSuccessful()) {
                    WeatherDailyResponse weatherDailyResponse = response.body();
//                Log.d("FSDSF","" + response.body().main.temp);
                    Weather weather = weatherDailyResponse.weather.get(0);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                    Date d = new Date();
                    String dayOfTheWeek = sdf.format(d);
                    String description = weather.description;
                    String tempertaure = String.valueOf(weatherDailyResponse.main.temp);
                    tempertaure = tempertaure.substring(0, 2);
                    String humidity = String.valueOf(weatherDailyResponse.main.humidity);
                    String icon = weather.icon;
                    double windvalue = weatherDailyResponse.wind.speed * 3.6;
                    String windpressure = String.valueOf(windvalue);
                    windpressure = windpressure.substring(0, 3);
                    CurrentWeatherObject currentWeatherObject = new CurrentWeatherObject(dayOfTheWeek,
                            tempertaure, description, humidity, windpressure, icon);
                    currentWeather.postValue(currentWeatherObject);
                }
                else
                {
                    //TODO Implement This
                }

            }

            @Override
            public void onFailure(Call<WeatherDailyResponse> call, Throwable t) {
                Log.d("HALA", t.getMessage());
            }
        });

    }


}
