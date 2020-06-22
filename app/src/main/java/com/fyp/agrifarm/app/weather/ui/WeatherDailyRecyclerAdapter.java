package com.fyp.agrifarm.app.weather.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fyp.agrifarm.R;
import com.fyp.agrifarm.app.weather.model.DailyWeatherItem;
import com.fyp.agrifarm.app.weather.repo.WeatherItem;
import com.fyp.agrifarm.app.weather.repo.Weather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherDailyRecyclerAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<DailyWeatherItem> weatherList;


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
        DailyWeatherItem record = weatherList.get(i);
        WeatherListViewHolder holder = (WeatherListViewHolder) viewHolder;
        String day = new SimpleDateFormat("EEEE").format(new Date(record.getTime() * 1000));
        holder.tvDay.setText(day);
//        holder.tvDescription.setText(weather.getDescription());
        String temperature = record.getTemperature();
        holder.tvTemperature.setText(temperature+"\u00B0");


        try {
            holder.weatherIcon.setImageResource(record.getIcon());
        } catch (Exception e) {
            Log.d("ffnet", "onBindViewHolder: Icon Null");
        }
    }

    @Override
    public int getItemCount() {
        if(weatherList == null)
            return 0;
        return weatherList.size();
    }

    private class WeatherListViewHolder extends RecyclerView.ViewHolder {

        TextView tvDay, tvTemperature;
        ImageView weatherIcon;

        public WeatherListViewHolder(View view) {
            super(view);
            tvDay = view.findViewById(R.id.tvWeatherDailyDay);
            tvTemperature = view.findViewById(R.id.tvWeatherDailyTemp);
            weatherIcon = view.findViewById(R.id.ivWeatherDailyIcon);
        }
    }

    public void updateList(List<DailyWeatherItem> list){
        weatherList = new ArrayList(list);
        notifyDataSetChanged();
    }
}

