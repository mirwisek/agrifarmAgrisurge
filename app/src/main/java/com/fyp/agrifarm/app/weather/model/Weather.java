package com.fyp.agrifarm.app.weather.model;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("description")
    public String description;
    @SerializedName("icon")
    public String icon;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
