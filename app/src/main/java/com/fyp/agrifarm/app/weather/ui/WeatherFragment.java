package com.fyp.agrifarm.app.weather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.weather.model.WeatherViewModel;

import java.util.HashMap;
import java.util.Map;

public class WeatherFragment extends Fragment {


    public static final String TAG = "WeatherFragment";
    private WeatherViewModel weatherViewModel;
    Map<String, Integer> WeatherIconMap;


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstraintLayout parent = (ConstraintLayout) inflater.inflate(R.layout.content_weather, container,
                false);

//        parent.findViewById(R.id.layout_rel).setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        weatherViewModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        WeatherIconMap = new HashMap<>();
        WeatherIconMap.put("01d", R.drawable.ic_wi_day_sunny);
        WeatherIconMap.put("02d", R.drawable.ic_wi_day_cloudy);
        WeatherIconMap.put("03d", R.drawable.ic_wi_cloud);
        WeatherIconMap.put("04d", R.drawable.ic_wi_cloudy);
        WeatherIconMap.put("09d", R.drawable.ic_wi_showers);
        WeatherIconMap.put("10d", R.drawable.ic_wi_day_rain_mix);
        WeatherIconMap.put("11d", R.drawable.ic_wi_thunderstorm);
        WeatherIconMap.put("13d", R.drawable.ic_wi_snow);
        WeatherIconMap.put("50d", R.drawable.ic_wi_fog);
        WeatherIconMap.put("04n", R.drawable.ic_wi_cloudy);


        RecyclerView rvHourlyForecast = parent.findViewById(R.id.rvHourlyForecast);


        WeatherHourlyRecyclerAdapter hourlyRecyclerAdapter = new WeatherHourlyRecyclerAdapter(getContext());
        weatherViewModel.getHourlyforcastlist().observe(getViewLifecycleOwner(), hourlyRecyclerAdapter::updateList);
        rvHourlyForecast.setAdapter(hourlyRecyclerAdapter);

        RecyclerView rvDailyForecast = parent.findViewById(R.id.rvDailyForecast);
        WeatherDailyRecyclerAdapter dailyRecyclerAdapter = new WeatherDailyRecyclerAdapter(getContext());
        weatherViewModel.getDailyforcastlist().observe(getViewLifecycleOwner(), dailyRecyclerAdapter::updateList);
        rvDailyForecast.setAdapter(dailyRecyclerAdapter);


        TextView wftemperatute = parent.findViewById(R.id.wftvWeatherTemp);
        TextView wfdescription = parent.findViewById(R.id.wftvweatherdescription);
        TextView wfwindpressure = parent.findViewById(R.id.wftvWeatherWind);
        TextView wfhumidity = parent.findViewById(R.id.wftvWeatherHumidity);
        TextView wfday = parent.findViewById(R.id.wftvWeatherDay);
        ImageView weatherIcon = parent.findViewById(R.id.wfivWeatherIcon);

        weatherViewModel.getDailyforcast().observe(getViewLifecycleOwner(), weatherDailyForecast -> {
            String temperatue = weatherDailyForecast.getTemperature() + "°C";
            wftemperatute.setText(temperatue);
            wfdescription.setText(weatherDailyForecast.getDescription());
            wfday.setText(weatherDailyForecast.getDay());
            wfhumidity.setText(weatherDailyForecast.getHumidity());
            wfwindpressure.setText(weatherDailyForecast.getWindPressure());
            if (weatherDailyForecast.getIconurl().equals("01d")) {
                weatherIcon.setImageResource(WeatherIconMap.get("01d"));
            }
            if (weatherDailyForecast.getIconurl().equals("02d")) {
                weatherIcon.setImageResource(WeatherIconMap.get("02d"));
            }
            if (weatherDailyForecast.getIconurl().equals("03d")) {
                weatherIcon.setImageResource(WeatherIconMap.get("03d"));
            }
            if (weatherDailyForecast.getIconurl().equals("04d")) {
                weatherIcon.setImageResource(WeatherIconMap.get("04d"));
            }
            if (weatherDailyForecast.getIconurl().equals("04n")) {
                weatherIcon.setImageResource(WeatherIconMap.get("04n"));
            }
            if (weatherDailyForecast.getIconurl().equals("50d"))
            {
                weatherIcon.setImageResource(WeatherIconMap.get("50d"));
            }
            if (weatherDailyForecast.getIconurl().equals("09d"))
            {
                weatherIcon.setImageResource(WeatherIconMap.get("09d"));
            }
            if (weatherDailyForecast.getIconurl().equals("10d"))
            {
                weatherIcon.setImageResource(WeatherIconMap.get("10d"));
            }
            if (weatherDailyForecast.getIconurl().equals("11d"))
            {
                weatherIcon.setImageResource(WeatherIconMap.get("11d"));
            }
            if (weatherDailyForecast.getIconurl().equals("13d"))
            {
                weatherIcon.setImageResource(WeatherIconMap.get("13d"));
            }

        });


        return parent;
    }


}
