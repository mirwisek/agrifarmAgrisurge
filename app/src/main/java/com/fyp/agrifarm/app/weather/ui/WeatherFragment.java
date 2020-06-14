package com.fyp.agrifarm.app.weather.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.weather.model.WeatherViewModel;

import java.util.HashMap;
import java.util.Map;

public class WeatherFragment extends Fragment {


    public static final String TAG = "WeatherFragment";
    private WeatherViewModel weatherViewModel;
    private String temperatue;


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

            SharedPreferences sharedPref = PreferenceManager
                    .getDefaultSharedPreferences(getContext());
            String WeatherPref = sharedPref
                    .getString("weatherUnit", "-1");
            if (WeatherPref.equals("Celsius"))
            {
                temperatue = weatherDailyForecast.getTemperature() + "°C";

            }
            else
            {
                temperatue = weatherDailyForecast.getTemperature() + "°F";

            }

            wftemperatute.setText(temperatue);
            wfdescription.setText(weatherDailyForecast.getDescription());
            wfday.setText(weatherDailyForecast.getDay());
            wfhumidity.setText(weatherDailyForecast.getHumidity());
            wfwindpressure.setText(weatherDailyForecast.getWindPressure());

            // Wrapped with catch incase resource ID not found
            try {
                String id = weatherDailyForecast.getIconurl();
                weatherIcon.setImageResource(weatherViewModel.weatherIconsMap.get(id));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


        return parent;
    }


}
