package com.example.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.ViewHolder> {


    private final Context context;
    private final DBHelper db;
    private final ArrayList<CityModel> cityModelArrayList;

    public WeatherListAdapter(Context context, DBHelper db, ArrayList<CityModel> cityModelArrayList) {
        this.context = context;
        this.db = db;
        this.cityModelArrayList = cityModelArrayList;
    }


    @NonNull
    @Override
    public WeatherListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.city_list_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherListAdapter.ViewHolder holder, int position) {
        CityModel cityModel = cityModelArrayList.get(position);
        holder.temperatureTV.setText(cityModel.getTemperature() + "°C");
        Picasso.get().load("http:".concat(cityModel.getIcon())).into(holder.iconIV);
        holder.nameTV.setText(cityModel.getName());
    }

    @Override
    public int getItemCount() {

        if (cityModelArrayList!= null)
            return cityModelArrayList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTV;
        private final TextView temperatureTV;
        private final ImageView iconIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.idTVCityListName);
            temperatureTV = itemView.findViewById(R.id.idTVCityListTemp);
            iconIV = itemView.findViewById(R.id.idIVCityListIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String oldActiveCity = findActuallyActiveCity();
                                                if(oldActiveCity.isEmpty())
                                                    db.manageCityActivity(nameTV.getText().toString(), 1);
                                                else{
                                                    db.manageCityActivity(oldActiveCity, 0);
                                                    db.manageCityActivity(nameTV.getText().toString(), 1);
                                                }
                                                Intent intent = new Intent(context, MainActivity.class);
                                                context.startActivity(intent);
                                            }
                                        }

            );
        }
    }

    public String findActuallyActiveCity(){
        for(int i=0; i<cityModelArrayList.size();i++){
            if(cityModelArrayList.get(i).getActivity() == 1)
                return cityModelArrayList.get(i).getName();
        }
        return "";
    }
}