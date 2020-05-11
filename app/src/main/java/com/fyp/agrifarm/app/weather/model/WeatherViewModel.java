package com.fyp.agrifarm.app.weather.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.agrifarm.app.ExtensionsKt;
import com.google.type.LatLng;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.fyp.agrifarm.app.HomeFragmentKt.KEY_LOCATION_SET;

public class WeatherViewModel extends AndroidViewModel {

    private static final String APP_ID = "e1f236cb66f5933607fd7905b42b39c9";
    private static final String VERSION = "2.5";
    private static final String BASE_URL = "https://api.openweathermap.org/";

    private Context context;
    private MutableLiveData<WeatherDailyForecast> dailyforcast = new MutableLiveData<>();
    private MutableLiveData<List<WeatherHourlyForecast>> hourlyforcastlist = new MutableLiveData<>();
    private MutableLiveData<List<WeatherDailyForecast>> dailyforcastlist = new MutableLiveData<>();


    public WeatherViewModel(@NonNull Application application) {
        super(application);
        this.context = application;
        FindDailyWeather();
        FindDailyandHourlyforcast();
    }

    private String getPrefix() {
        return BASE_URL + "data/" + VERSION + "/";
    }

    private String getPostfix() {
        LatLng location = getLocation();
        if(location == null) return null;
        return "lat=" +
        location.getLatitude() + "&lon="+ location.getLongitude() +
                "&units=metric&appid=" + APP_ID;
    }

    private LatLng getLocation() {
        String latitude = ExtensionsKt.getSharedPrefs(context).getString("lat",null);
        String longitude = ExtensionsKt.getSharedPrefs(context).getString("lon",null);
        try {
            double lat = Double.parseDouble(latitude);
            double lng = Double.parseDouble(longitude);
            return LatLng.newBuilder().setLatitude(lat).setLongitude(lng).build();
        } catch (Exception e){
            return null;
        }
    }


    private void FindDailyandHourlyforcast() {
        // Get location now from SharedPreferences
        String postfix = getPostfix();
        if(postfix == null)
            return;
        String url = getPrefix() + "onecall?exclude=current&" + postfix;
        List<WeatherHourlyForecast> listforhourly = new ArrayList<>();
        List<WeatherDailyForecast> listfordaily = new ArrayList<>();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray hourlyarray = response.getJSONArray("hourly");

                        for (int i = 1; i < hourlyarray.length(); i++) {
                            JSONObject hourlyobject = hourlyarray.getJSONObject(i);
                            String temperature = String.valueOf(hourlyobject.getDouble("temp"));
                            temperature = temperature.substring(0, 2);
                            long dt = hourlyobject.getLong("dt");
                            String hour = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dt * 1000));
                            WeatherHourlyForecast weatherHourlyForecast = new WeatherHourlyForecast(hour, temperature);
                            listforhourly.add(weatherHourlyForecast);
                        }

                        //For dailyforcast

                        JSONArray daily = response.getJSONArray("daily");
                        for (int i = 1; i < daily.length(); i++) {
                            JSONObject dailobject = daily.getJSONObject(i);

                            long epoch = dailobject.getLong("dt");
                            String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date(epoch * 1000));
                            JSONObject temp = dailobject.getJSONObject("temp");
                            String temperature = String.valueOf(temp.getDouble("day"));
                            temperature = temperature.substring(0, 2);
                            JSONArray des = dailobject.getJSONArray("weather");
                            JSONObject desobj = des.getJSONObject(0);
                            String description = desobj.getString("description");

                            String icon = desobj.getString("icon");
                            String iconUrl = BASE_URL + "img/w/" + icon + ".png";

                            WeatherDailyForecast weatherDailyForecast = new WeatherDailyForecast(day, temperature, description, null, null, iconUrl);
                            listfordaily.add(weatherDailyForecast);


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, Throwable::printStackTrace);

        dailyforcastlist.postValue(listfordaily);
        hourlyforcastlist.postValue(listforhourly);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public MutableLiveData<List<WeatherDailyForecast>> getDailyforcastlist() {
        return dailyforcastlist;
    }

    public MutableLiveData<List<WeatherHourlyForecast>> getHourlyforcastlist() {
        return hourlyforcastlist;
    }

    private void FindDailyWeather() {

        String postfix = getPostfix();
        if(postfix == null)
            return;
        String url = getPrefix() + "weather?" + postfix;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main_object = response.getJSONObject("main");
                        JSONArray array = response.getJSONArray("weather");
                        JSONObject object = array.getJSONObject(0);
                        String temperature = String.valueOf(main_object.getDouble("temp"));
                        String description = object.getString("description");
                        String icon = object.getString("icon");
                        String iconUrl = BASE_URL + "img/w/" + icon + ".png";
                        String Humidity = main_object.getString("humidity") + "%";
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                        Date d = new Date();
                        String dayOfTheWeek = sdf.format(d);
                        temperature = temperature.substring(0, 2);

                        JSONObject obj = response.getJSONObject("wind");
                        double windvalue = obj.getDouble("speed");
                        windvalue = 3.6 * windvalue;
                        String windpressure = String.valueOf(windvalue);
                        windpressure = windpressure.substring(0, 2);

                        WeatherDailyForecast weatherDailyForecast =
                                new WeatherDailyForecast(dayOfTheWeek, temperature, description,
                                        Humidity, windpressure, iconUrl);
                        dailyforcast.postValue(weatherDailyForecast);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    public MutableLiveData<WeatherDailyForecast> getDailyforcast() {
        return dailyforcast;
    }

}
