package com.fyp.agrifarm.app.weather.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.fyp.agrifarm.app.HomeFragmentKt.KEY_LOCATION_SET;

public class WeatherViewModel extends AndroidViewModel {
    private Context context;
    private MutableLiveData<WeatherDailyForecast> dailyforcast = new MutableLiveData<>();
    private MutableLiveData<List<WeatherHourlyForecast>> hourlyforcastlist = new MutableLiveData<>();
    private MutableLiveData<List<WeatherDailyForecast>> dailyforcastlist = new MutableLiveData<>();
    String latitude = ExtensionsKt.getSharedPrefs(context).getString("lat",null);
    String longitude="";



    public WeatherViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        FindDailyWeather();
        FindDailyandHourlyforcast();

    }


    private void FindDailyandHourlyforcast() {
        List<WeatherHourlyForecast> listforhourly = new ArrayList<>();
        List<WeatherDailyForecast> listfordaily = new ArrayList<>();
        String latitude = ExtensionsKt.getSharedPrefs(context).getString("lat",null);
        String longitude="";
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&units=metric&exclude=current&appid=e1f236cb66f5933607fd7905b42b39c9";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray hourlyarray = response.getJSONArray("hourly");
                            for (int i = 1; i < hourlyarray.length(); i++) {
                                JSONObject hourlyobject = hourlyarray.getJSONObject(i);
                                String temperature = String.valueOf(hourlyobject.getDouble("temp"));
                                temperature = temperature.substring(0, 2);
                                long dt = hourlyobject.getLong("dt");
                                String hour = new SimpleDateFormat("hh:mm a").format(new Date(dt * 1000));
                                WeatherHourlyForecast weatherHourlyForecast = new WeatherHourlyForecast(hour, temperature);
                                listforhourly.add(weatherHourlyForecast);
                            }

                            //For dailyforcast

                            JSONArray daily = response.getJSONArray("daily");
                            for (int i = 1; i < daily.length(); i++) {
                                JSONObject dailobject = daily.getJSONObject(i);

                                long epoch = dailobject.getLong("dt");
                                String day = new SimpleDateFormat("EEEE").format(new Date(epoch * 1000));
                                JSONObject temp = dailobject.getJSONObject("temp");
                                String temperature = String.valueOf(temp.getDouble("day"));
                                temperature = temperature.substring(0, 2);
                                JSONArray des = dailobject.getJSONArray("weather");
                                JSONObject desobj = des.getJSONObject(0);
                                String description = desobj.getString("description");

                                String icon = desobj.getString("icon");
                                String iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";

                                WeatherDailyForecast weatherDailyForecast = new WeatherDailyForecast(day, temperature, description, null, null, iconUrl);
                                listfordaily.add(weatherDailyForecast);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
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
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=e1f236cb66f5933607fd7905b42b39c9&units=metric";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject main_object = response.getJSONObject("main");
                            JSONArray array = response.getJSONArray("weather");
                            JSONObject object = array.getJSONObject(0);
                            String temperature = String.valueOf(main_object.getDouble("temp"));
                            String description = object.getString("description");
                            String icon = object.getString("icon");
                            String iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";
                            String Humidity = main_object.getString("humidity") + "%";
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                            Date d = new Date();
                            String dayOfTheWeek = sdf.format(d);
                            temperature = temperature.substring(0, 2);

                            JSONObject obj = response.getJSONObject("wind");
                            Double windvalue = obj.getDouble("speed");
                            windvalue = 3.6 * windvalue;
                            String windpressure = String.valueOf(windvalue);
                            windpressure = windpressure.substring(0, 2);

                            WeatherDailyForecast weatherDailyForecast = new WeatherDailyForecast(dayOfTheWeek, temperature, description, Humidity, windpressure, iconUrl);
                            dailyforcast.postValue(weatherDailyForecast);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    public MutableLiveData<WeatherDailyForecast> getDailyforcast() {
        return dailyforcast;
    }

}
