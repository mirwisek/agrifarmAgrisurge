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
import com.fyp.agrifarm.app.weather.model.HourlyWeatherItem;
import com.fyp.agrifarm.app.weather.repo.WeatherItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherHourlyRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<HourlyWeatherItem> weatherList;


    public WeatherHourlyRecyclerAdapter(Context context){
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.rv_item_hourly_forecast, viewGroup, false);

        return new WeatherListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        HourlyWeatherItem record = weatherList.get(i);
        WeatherListViewHolder holder = (WeatherListViewHolder) viewHolder;
        String hour = new SimpleDateFormat("h a").format(new Date(record.getTime() * 1000));
        holder.tvTime.setText(hour);
        String temperature = record.getTemperature();
        temperature = temperature.substring(0,2);
        holder.tvTemperature.setText(temperature+ "\u00B0");


        try {
            holder.imageView.setImageResource(record.getIcon());
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

        TextView tvTime, tvTemperature;
        ImageView imageView;

        public WeatherListViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ic_hourly);
            tvTime = view.findViewById(R.id.tvWeatherHourlyTime);
            tvTemperature = view.findViewById(R.id.tvWeatherHourlyTemp);
        }
    }
    public void updateList(List<HourlyWeatherItem> list){
        weatherList = new ArrayList(list);
        notifyDataSetChanged();
    }

}

