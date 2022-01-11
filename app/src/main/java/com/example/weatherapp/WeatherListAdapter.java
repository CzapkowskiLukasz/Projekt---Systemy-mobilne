package com.example.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
        Picasso.get().load("http:".concat(cityModel.getIcon())).resize(128, 128).into(holder.iconIV);
        holder.nameTV.setText(cityModel.getName());
        int isDay = cityModel.getIsDay();
        if (isDay == 1) {
            //day
            Picasso.get().load("https://images.unsplash.com/photo-1597200381847-30ec200eeb9a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2874&q=80").into(holder.backIV);
        } else {
            Picasso.get().load("https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2942&q=80").into(holder.backIV);
        }
    }

    @Override
    public int getItemCount() {

        if (cityModelArrayList != null)
            return cityModelArrayList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTV;
        private final TextView temperatureTV;
        private final ImageView iconIV;
        private final ImageView backIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.idTVCityListName);
            temperatureTV = itemView.findViewById(R.id.idTVCityListTemp);
            iconIV = itemView.findViewById(R.id.idIVCityListIcon);
            backIV = itemView.findViewById(R.id.idIVCityListBack);

            itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String oldActiveCity = findActuallyActiveCity();
                                                if (oldActiveCity.isEmpty())
                                                    db.manageCityActivity(nameTV.getText().toString(), 1);
                                                else {
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

    public String findActuallyActiveCity() {
        for (int i = 0; i < cityModelArrayList.size(); i++) {
            if (cityModelArrayList.get(i).getActivity() == 1)
                return cityModelArrayList.get(i).getName();
        }
        return "";
    }
}