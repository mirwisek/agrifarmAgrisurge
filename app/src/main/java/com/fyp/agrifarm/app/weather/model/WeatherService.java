package com.fyp.agrifarm.app.weather.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather?")
    Call<WeatherDailyResponse> getCurrentWeatherData(@Query("lat") String lat, @Query("lon") String lon, @Query("appid")
            String app_id, @Query("units") String units);

    @GET("data/2.5/onecall?")
    Call<WeatherHourlyResponse> getHourlyForecastList(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String units,
                                                      @Query("exclude") String exclude, @Query("appid") String app_id);
    @GET("data/2.5/onecall?")
    Call<WeatherHourlyResponse> getDailyForecastList(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String units,
                                                      @Query("exclude") String exclude, @Query("appid") String app_id);


}
