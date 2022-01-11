package com.example.weatherapp;

import android.database.Cursor;

public interface IDbHelper {
    Boolean insertCity(String name, String temperature, String icon, int activity, int isDay);

    Boolean manageCityActivity(String name, int activity);

    Cursor getCityByActivity();

    Boolean updateCity(String name, String temperature, String icon);

    Boolean deleteCity(String name);

    Cursor getData();
}
