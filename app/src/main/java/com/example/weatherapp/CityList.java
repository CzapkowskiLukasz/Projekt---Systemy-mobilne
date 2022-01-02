package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CityList extends AppCompatActivity {

    private ArrayList<CityModel> cityModelArrayList;
    private RecyclerView weatherCityList;
    private WeatherListAdapter weatherListAdapter;

    private DBHelper db;

    private ImageView searchIV;
    private TextInputEditText cityEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        Intent intent = getIntent();

        db = new DBHelper(this);

        weatherCityList = findViewById(R.id.idCityList);
        cityModelArrayList = new ArrayList<>();
        searchIV = findViewById(R.id.idIVSearch);
        cityEdt = findViewById(R.id.idEdtCity);


        ArrayList<CityModel> cityModelArrayList = new ArrayList<CityModel>();

        refreshList();

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                Boolean isAdded = false;
                ArrayList<CityModel> cityModelArrayListHelper = getData();
                if (city.isEmpty()) {
                    Toast.makeText(CityList.this, "nie moze byc puste", Toast.LENGTH_SHORT).show();
                } else {
                    if(cityModelArrayListHelper != null){
                        for(int i=0; i< cityModelArrayListHelper.size() ; i++){
                            if(cityModelArrayListHelper.get(i).getName().equals(city)){
                                Toast.makeText(CityList.this, "juz dodany", Toast.LENGTH_SHORT).show();
                                isAdded = true;
                                break;
                            }
                        }
                    }
                    if(isAdded == false){
                        addCity(city, db);
                    }
                    }
                cityEdt.setText("");
                hideSoftKeyboard(CityList.this);
            }
        });

    }

    private void refreshList() {
        cityModelArrayList = getData();
        weatherListAdapter = new WeatherListAdapter(this, cityModelArrayList);
        weatherCityList.setAdapter((weatherListAdapter));
    }

    private ArrayList<CityModel> getData() {
        ArrayList<CityModel> cityModelArrayListHelper = new ArrayList<>();
        Cursor result = db.getData();
        if (result.getCount() == 0) {
            Toast.makeText(CityList.this, "git", Toast.LENGTH_SHORT).show();
            return null;
        }

        while (result.moveToNext()) {
            cityModelArrayListHelper.add(new CityModel(result.getString(3), result.getString(1), result.getString(2)));
        }
        int kutas = cityModelArrayListHelper.size();
        return cityModelArrayListHelper;
    }

    private void validateData(Boolean result) {
        if (result) {
            refreshList();
            Toast.makeText(CityList.this, "git", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(CityList.this, "nie git", Toast.LENGTH_SHORT).show();
    }

    private void addCity(String cityName, DBHelper dbHelper) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=267e447e5e524f4286d134655213012&q=" + cityName + "&days=1&aqi=no&alerts=no";
        RequestQueue requestQueue = Volley.newRequestQueue((CityList.this));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String name = response.getJSONObject("location").getString("name");
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Boolean result = dbHelper.insertCity(name, temperature, conditionIcon);
                    getData();
                    validateData(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CityList.this, "enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
}