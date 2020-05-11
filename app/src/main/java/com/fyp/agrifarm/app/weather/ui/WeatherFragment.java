package com.fyp.agrifarm.app.weather.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.ExtensionsKt;
import com.fyp.agrifarm.app.weather.model.WeatherDailyForecast;
import com.fyp.agrifarm.app.weather.model.WeatherViewModel;
import com.squareup.picasso.Picasso;

public class WeatherFragment extends Fragment {


    public static final String TAG = "WeatherFragment";
    private WeatherViewModel weatherViewModel;


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstraintLayout parent = (ConstraintLayout) inflater.inflate(R.layout.content_weather, container,
                false);

//        parent.findViewById(R.id.layout_rel).setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);


        RecyclerView rvHourlyForecast = parent.findViewById(R.id.rvHourlyForecast);

        WeatherHourlyRecyclerAdapter hourlyRecyclerAdapter = new WeatherHourlyRecyclerAdapter(getContext());
        weatherViewModel.getHourlyforcastlist().observe(getViewLifecycleOwner(), hourlyRecyclerAdapter::updateList);
        rvHourlyForecast.setAdapter(hourlyRecyclerAdapter);

        RecyclerView rvDailyForecast = parent.findViewById(R.id.rvDailyForecast);

        WeatherDailyRecyclerAdapter dailyRecyclerAdapter = new WeatherDailyRecyclerAdapter(getContext());
        weatherViewModel.getDailyforcastlist().observe(getViewLifecycleOwner(), dailyRecyclerAdapter::updateList);
        rvDailyForecast.setAdapter(dailyRecyclerAdapter);


        TextView wftemperatute = parent.findViewById(R.id.tvWeatherTemp);
        TextView wfdescription = parent.findViewById(R.id.tvweatherdescription);
        TextView wfwindpressure = parent.findViewById(R.id.tvWeatherWind);
        TextView wfhumidity = parent.findViewById(R.id.tvWeatherHumidity);
        TextView wfday = parent.findViewById(R.id.tvWeatherDay);
        ImageView weatherIcon = parent.findViewById(R.id.ivWeatherIcon);

        weatherViewModel.getDailyforcast().observe(getViewLifecycleOwner(), weatherDailyForecast -> {
            String temperatue = weatherDailyForecast.getTemperature() + "°C";
            wftemperatute.setText(temperatue);
            wfdescription.setText(weatherDailyForecast.getDescription());
            wfday.setText(weatherDailyForecast.getDay());
            wfhumidity.setText(weatherDailyForecast.getHumidity());
            wfwindpressure.setText(weatherDailyForecast.getWindPressure());
            Picasso.get().load(weatherDailyForecast.getIconurl()).into(weatherIcon);
        });


        return parent;
    }


}
