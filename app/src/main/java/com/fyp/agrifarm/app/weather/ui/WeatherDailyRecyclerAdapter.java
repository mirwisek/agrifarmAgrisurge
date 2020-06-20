package com.fyp.agrifarm.app.weather.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.weather.model.DailyObject;
import com.fyp.agrifarm.app.weather.model.Weather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherDailyRecyclerAdapter extends RecyclerView.Adapter {
    private Map<String, Integer> WeatherIconMap;
    private Context context;
    private List<DailyObject> weatherList;


    public WeatherDailyRecyclerAdapter(Context context){
        this.context = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.rv_item_daily_forecast, viewGroup, false);

        return new WeatherListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        DailyObject record = weatherList.get(i);
        WeatherListViewHolder holder = (WeatherListViewHolder) viewHolder;
        String day = new SimpleDateFormat("EEEE").format(new Date(record.getDt() * 1000));
        holder.tvDay.setText(day);
        Weather weather = record.getWeather().get(0);
        holder.tvDescription.setText(weather.getDescription());
        String temperature = record.getTemp().toString();
        temperature = temperature.substring(0,2);
        holder.tvTemperature.setText(temperature+"\u00B0");

        if (weather.getIcon().equals("01d")) {
            holder.weatherIcon.setImageResource(WeatherIconMap.get("01d"));
        }
        if (weather.getIcon().equals("02d")) {
            holder.weatherIcon.setImageResource(WeatherIconMap.get("02d"));
        }
        if (weather.getIcon().equals("03d")) {
            holder.weatherIcon.setImageResource(WeatherIconMap.get("03d"));
        }
        if (weather.getIcon().equals("04d")) {
            holder.weatherIcon.setImageResource(WeatherIconMap.get("04d"));
        }
        if (weather.getIcon().equals("04n")) {
            holder.weatherIcon.setImageResource(WeatherIconMap.get("04n"));
        }
        if (weather.getIcon().equals("50d"))
        {
            holder.weatherIcon.setImageResource(WeatherIconMap.get("50d"));
        }
        if (weather.getIcon().equals("09d"))
        {
            holder.weatherIcon.setImageResource(WeatherIconMap.get("09d"));
        }
        if (weather.getIcon().equals("10d"))
        {
            holder.weatherIcon.setImageResource(WeatherIconMap.get("10d"));
        }
        if (weather.getIcon().equals("11d"))
        {
            holder.weatherIcon.setImageResource(WeatherIconMap.get("11d"));
        }
        if (weather.getIcon().equals("13d"))
        {
            holder.weatherIcon.setImageResource(WeatherIconMap.get("13d"));
        }

//        Picasso.get().load(record.getIconurl()).into(holder.weatherIcon);
    }

    @Override
    public int getItemCount() {
        if(weatherList == null)
            return 0;
        return weatherList.size();
    }

    private class WeatherListViewHolder extends RecyclerView.ViewHolder {

        TextView tvDay, tvDescription, tvTemperature;
        ImageView weatherIcon;

        public WeatherListViewHolder(View view) {
            super(view);
            tvDay = view.findViewById(R.id.tvWeatherDailyDay);
            tvDescription = view.findViewById(R.id.tvWeatherDailyDesc);
            tvTemperature = view.findViewById(R.id.tvWeatherDailyTemp);
            weatherIcon = view.findViewById(R.id.ivWeatherDailyIcon);
        }
    }

    public void updateList(List<DailyObject> list){
        weatherList = new ArrayList(list);
        notifyDataSetChanged();
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
    }
}

