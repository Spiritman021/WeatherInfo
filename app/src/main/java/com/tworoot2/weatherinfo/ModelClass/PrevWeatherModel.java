package com.tworoot2.weatherinfo.ModelClass;

public class PrevWeatherModel {
    String time;
    String iconUrl;
    String tempt;

    public PrevWeatherModel(String time, String iconUrl, String tempt) {
        this.time = time;
        this.iconUrl = iconUrl;
        this.tempt = tempt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTempt() {
        return tempt;
    }

    public void setTempt(String tempt) {
        this.tempt = tempt;
    }
}
