package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class CityList extends AppCompatActivity {

    private ArrayList<CityModel> cityModelArrayList;
    private RecyclerView weatherCityList;
    private WeatherListAdapter weatherListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        Intent intent = getIntent();

        weatherCityList = findViewById(R.id.idCityList);
        cityModelArrayList = new ArrayList<>();


        ArrayList<CityModel> cityModelArrayList = new ArrayList<CityModel>();

        cityModelArrayList.add(new CityModel("dupe", "69.0", "//cdn.weatherapi.com/weather/64x64/day/116.png", "wypizdowie"));
        cityModelArrayList.add(new CityModel("dupe", "69.0", "//cdn.weatherapi.com/weather/64x64/day/116.png", "wypizdowafqwerfqwerqwerfgqrefwerfie"));
        WeatherListAdapter listAdapter = new WeatherListAdapter(CityList.this, cityModelArrayList);

        weatherListAdapter = new WeatherListAdapter(this, cityModelArrayList);
        weatherCityList.setAdapter((weatherListAdapter));

        weatherListAdapter.notifyDataSetChanged();

    }
}