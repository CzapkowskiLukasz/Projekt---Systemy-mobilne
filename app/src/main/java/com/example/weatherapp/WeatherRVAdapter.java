package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<WeatherRVModel> weatherRVModels;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModels) {
        this.context = context;
        this.weatherRVModels = weatherRVModels;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherRVModel model = weatherRVModels.get(position);
        holder.temperatueTV.setText(model.getTemperature() + "°C");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.conditionTV);
        holder.windtTV.setText(model.getWindSpeed() + "km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(model.getTime());
            holder.timeTV.setText((output.format(t)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView windtTV;
        private final TextView temperatueTV;
        private final TextView timeTV;
        private final ImageView conditionTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windtTV = itemView.findViewById(R.id.idTVWindSpeed);
            temperatueTV = itemView.findViewById(R.id.idTTVTemperature);
            timeTV = itemView.findViewById(R.id.idTTVTime);
            conditionTV = itemView.findViewById(R.id.idIVCondition);
        }
    }
}
