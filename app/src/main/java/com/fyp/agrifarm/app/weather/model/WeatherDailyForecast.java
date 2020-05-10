package com.fyp.agrifarm.app.weather.model;

public class WeatherDailyForecast {

    String day;
    String temperature;
    String description;
    String humidity;
    String windPressure;
    String iconurl;


    public WeatherDailyForecast(String day, String temperature, String description, String humidity, String windPressure, String iconurl) {
        this.day = day;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.windPressure = windPressure;
        this.iconurl = iconurl;
    }

    public WeatherDailyForecast(String day, String temperature, String description, String humidity, String windPressure) {
        this.day = day;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.windPressure = windPressure;
    }

    public WeatherDailyForecast(String day, String temperature, String description, String humidity) {
        this.day = day;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
    }

    public WeatherDailyForecast(String day, String temperature, String description) {
        this.day = day;
        this.temperature = temperature;
        this.description = description;
    }



    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindPressure() {
        return windPressure;
    }

    public void setWindPressure(String windPressure) {
        this.windPressure = windPressure;
    }
}
