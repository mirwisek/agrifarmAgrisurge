package com.fyp.agrifarm.app.weather.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(HourlyObject... hourlyForecasts);

    @Query("SELECT * FROM HourlyForecasts")
    LiveData<List<HourlyObject>> getRoomHourlyForecast();
}
