package com.example.weatherapp;

public class CityModel {

    private String temperature;
    private String icon;
    private String name;
    private int activity;

    public CityModel(String icon, String name, String temperature, int activity) {
        this.temperature = temperature;
        this.icon = icon;
        this.name = name;
        this.activity = activity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActivity() {
        return activity;
    }
}
