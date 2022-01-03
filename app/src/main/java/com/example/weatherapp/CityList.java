package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
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

        cityModelArrayList = getData();
        refreshData(cityModelArrayList);
        refreshList();

        setSwiper();

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                Boolean isAdded = false;
                ArrayList<CityModel> cityModelArrayListHelper = getData();
                if (city.isEmpty()) {
                    Toast.makeText(CityList.this, "nie moze byc puste", Toast.LENGTH_SHORT).show();
                } else {

                        manageCity(city, "addCity");
                    }
                cityEdt.setText("");
                hideSoftKeyboard(CityList.this);
            }
        });

    }

    private void setSwiper(){
        weatherCityList.setLayoutManager(new GridLayoutManager(this, 1));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                deleteCity(viewHolder.getLayoutPosition());
            }
        });

        itemTouchHelper.attachToRecyclerView(weatherCityList);
    }

    private void refreshList() {
        cityModelArrayList = getData();
        weatherListAdapter = new WeatherListAdapter(this, db, cityModelArrayList);
        weatherCityList.setAdapter((weatherListAdapter));
    }

    private void refreshData(ArrayList<CityModel> cityModelArrayList){
        for(int i=0; i <cityModelArrayList.size();i++){
           manageCity(cityModelArrayList.get(i).getName(), "editCity");
        }
    }

    private ArrayList<CityModel> getData() {
        ArrayList<CityModel> cityModelArrayListHelper = new ArrayList<>();
        Cursor result = db.getData();
        if (result.getCount() == 0) {
            Toast.makeText(CityList.this, "git", Toast.LENGTH_SHORT).show();
            return null;
        }

        while (result.moveToNext()) {
            cityModelArrayListHelper.add(new CityModel(result.getString(3), result.getString(1), result.getString(2), result.getInt(4)));
        }
        return cityModelArrayListHelper;
    }

    private void validateData(Boolean result) {
        if (result) {
            refreshList();
            Toast.makeText(CityList.this, "git", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(CityList.this, "nie git", Toast.LENGTH_SHORT).show();
    }

    private void deleteCity(int position){
        String cityToDelete = cityModelArrayList.get(position).getName();
        Boolean result = db.deleteCity(cityToDelete);
        if(result) {
            Toast.makeText(CityList.this, "usuwanie udane", Toast.LENGTH_SHORT).show();
            refreshList();
        }
        else
            Toast.makeText(CityList.this, "byku cos nie wyszlo", Toast.LENGTH_SHORT).show();
    }

    private void manageCity(String cityName, String state) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=267e447e5e524f4286d134655213012&q=" + cityName + "&days=1&aqi=no&alerts=no";
        RequestQueue requestQueue = Volley.newRequestQueue((CityList.this));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String name = response.getJSONObject("location").getString("name");
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    if(state.equals("addCity")){
                        if(cityModelArrayList != null) {
                            for (int i = 0; i < cityModelArrayList.size(); i++) {
                                if (cityModelArrayList.get(i).getName().equals(name)) {
                                    Toast.makeText(CityList.this, "juz dodany", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        Boolean result = db.insertCity(name, temperature, conditionIcon, 0);
                        validateData(result);
                    }
                    else{
                        db.updateCity(name, temperature, conditionIcon);
                    }

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