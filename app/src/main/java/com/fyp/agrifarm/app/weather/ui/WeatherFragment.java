package com.fyp.agrifarm.app.weather.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.weather.model.HourlyObject;
import com.fyp.agrifarm.app.weather.model.WeatherSharedViewModel;

import java.util.List;

public class WeatherFragment extends Fragment {


    public static final String TAG = "WeatherFragment";
//    private WeatherViewModel weatherViewModel;
     private WeatherSharedViewModel weatherModel;

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
//        weatherViewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        RecyclerView rvHourlyForecast = parent.findViewById(R.id.rvHourlyForecast);
        WeatherHourlyRecyclerAdapter hourlyRecyclerAdapter = new WeatherHourlyRecyclerAdapter(getContext());
//        weatherViewModel.getHourlyforcastlist().observe(getViewLifecycleOwner(), hourlyRecyclerAdapter::updateList);
//        weatherSharedViewModel = new ViewModelProvider(this).get(WeatherSharedViewModel.class);
//        weatherSharedViewModel.init();
//        weatherSharedViewModel.gethourlyList().observe(getViewLifecycleOwner(),hourlyRecyclerAdapter::changeDataSource);
        weatherModel = new ViewModelProvider(this).get(WeatherSharedViewModel.class);
        weatherModel.init();
//        weatherModel.gethourlyList().observe(getViewLifecycleOwner(), new Observer<List<HourlyObject>>() {
//            @Override
//            public void onChanged(List<HourlyObject> list) {
//                Log.d("LISTWALA"," " +  list.size()) ;
//                hourlyRecyclerAdapter.changeDataSource(list);
//            }
//        });
        weatherModel.gethourlyList().observe(getViewLifecycleOwner(),hourlyRecyclerAdapter::updateList);
        rvHourlyForecast.setAdapter(hourlyRecyclerAdapter);

        RecyclerView rvDailyForecast = parent.findViewById(R.id.rvDailyForecast);
        WeatherDailyRecyclerAdapter dailyRecyclerAdapter = new WeatherDailyRecyclerAdapter(getContext());
        weatherModel.getDailyList().observe(getViewLifecycleOwner(), dailyRecyclerAdapter::updateList);
        rvDailyForecast.setAdapter(dailyRecyclerAdapter);


        TextView wftemperatute = parent.findViewById(R.id.wftvWeatherTemp);
        TextView wfdescription = parent.findViewById(R.id.wftvweatherdescription);
        TextView wfwindpressure = parent.findViewById(R.id.wftvWeatherWind);
        TextView wfhumidity = parent.findViewById(R.id.wftvWeatherHumidity);
        TextView wfday = parent.findViewById(R.id.wftvWeatherDay);
        ImageView weatherIcon = parent.findViewById(R.id.wfivWeatherIcon);

        weatherModel.getCurrentWeather().observe(getViewLifecycleOwner(), weatherDailyForecast -> {

            SharedPreferences sharedPref = PreferenceManager
                    .getDefaultSharedPreferences(requireContext());
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
            wfhumidity.setText(weatherDailyForecast.getHumidity().substring(0,2));
            wfwindpressure.setText(weatherDailyForecast.getWindPressure());

            // Wrapped with catch incase resource ID not found
            try {
                String id = weatherDailyForecast.getIconurl();
                weatherIcon.setImageResource(weatherModel.weatherIconsMap.get(id));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
