package com.fyp.agrifarm.app.weather.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.List;

@Dao
public interface WeatherDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void Insert(HourlyItem... hourlyForecasts);
//
//    @Query("SELECT * FROM HourlyForecasts")
//    LiveData<List<HourlyItem>> getRoomHourlyForecast();
}
