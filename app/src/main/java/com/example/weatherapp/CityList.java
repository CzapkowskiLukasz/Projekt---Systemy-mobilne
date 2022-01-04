package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CityList extends AppCompatActivity {

    private ArrayList<CityModel> cityModelArrayList;
    private RecyclerView weatherCityList;
    private WeatherListAdapter weatherListAdapter;
    private FloatingActionButton getLocation;

    private DBHelper db;

    private ImageView searchIV;
    private TextInputEditText cityEdt;

    private LocationManager locationManager;
    private final int PERMISSION_CODE = 1;
    protected MyLocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        Intent intent = getIntent();

        db = new DBHelper(this);

        getLocation = findViewById(R.id.fab);
        weatherCityList = findViewById(R.id.idCityList);
        cityModelArrayList = new ArrayList<>();
        searchIV = findViewById(R.id.idIVSearch);
        cityEdt = findViewById(R.id.idEdtCity);


        ArrayList<CityModel> cityModelArrayList = new ArrayList<CityModel>();

        cityModelArrayList = getData();
        if(cityModelArrayList != null)
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

                        manageCity(normalize(city), "addCity");
                    }
                cityEdt.setText("");
                hideSoftKeyboard(CityList.this);
            }
        });

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(CityList.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CityList.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CityList.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    }, PERMISSION_CODE);
                }
                locationListener = new MyLocationListener();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "dawaj uprawnienia dziadu", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
        refreshList();
    }

    private ArrayList<CityModel> getData() {
        ArrayList<CityModel> cityModelArrayListHelper = new ArrayList<>();
        Cursor result = db.getData();
        if (result.getCount() == 0) {
            Toast.makeText(CityList.this, "git", Toast.LENGTH_SHORT).show();
            return null;
        }

        while (result.moveToNext()) {
            cityModelArrayListHelper.add(new CityModel(result.getString(3), result.getString(1), result.getString(2), result.getInt(4), result.getInt(5)));
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
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    if(state.equals("addCity")){
                        if(cityModelArrayList != null) {
                            for (int i = 0; i < cityModelArrayList.size(); i++) {
                                if (cityModelArrayList.get(i).getName().equals(name)) {
                                    Toast.makeText(CityList.this, "juz dodany", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        Boolean result = db.insertCity(name, temperature, conditionIcon, 0, isDay);
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

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location loc) {
            String city = getCityName(loc.getLongitude(), loc.getLatitude());
            if(!city.equals("Not found")){
                manageCity(city, "addCity");
                locationManager.removeUpdates(locationListener);
                locationManager=null;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }

        private String getCityName(double longitude, double latitude) {
            String cityName = "Not found";
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
                for (Address address : addresses) {
                    if (address != null) {
                        String city = address.getLocality();
                        if (city != null && !city.equals("")) {
                            cityName = city;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return normalize(cityName);
        }
    }

    private String normalize(String cityName){
        String ogCityName = cityName;
        for(int i = 0; i< ogCityName.length(); i++){
            char character = ogCityName.charAt(i);
            switch (character){
                case 'Ą':
                    ogCityName =ogCityName.replaceAll("Ą", "A");
                case 'ą':
                    ogCityName =ogCityName.replaceAll("ą", "a");
                case 'Ę':
                    ogCityName =ogCityName.replaceAll("Ę", "E");
                case 'ę':
                    ogCityName =ogCityName.replaceAll("ę", "e");
                case 'Ć':
                    ogCityName =ogCityName.replaceAll("Ć", "C");
                case 'ć':
                    ogCityName =ogCityName.replaceAll("ć", "c");
                case 'Ł':
                    ogCityName =ogCityName.replaceAll("Ł", "L");
                case 'ł':
                    ogCityName =ogCityName.replaceAll("ł", "l");
                case 'Ń':
                    ogCityName =ogCityName.replaceAll("Ń", "N");
                case 'ń':
                    ogCityName =ogCityName.replaceAll("ń", "n");
                case 'Ó':
                    ogCityName = ogCityName.replaceAll("Ó", "O");
                case 'ó':
                    ogCityName = ogCityName.replaceAll("ó", "o");
                case 'Ś':
                    ogCityName = ogCityName.replaceAll("Ś", "S");
                case 'ś':
                    ogCityName = ogCityName.replaceAll("ś", "s");
                case 'Ź':
                    ogCityName =ogCityName.replaceAll("Ź", "Z");
                case 'ź':
                    ogCityName =ogCityName.replaceAll("ź", "z");
                case 'Ż':
                    ogCityName = ogCityName.replaceAll("Ż", "Z");
                case 'ż':
                    ogCityName = ogCityName.replaceAll("ż", "z");
            }
        }
        return ogCityName;
    }
}

