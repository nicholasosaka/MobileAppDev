package com.example.hw03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    ArrayList<Weather> days;
    Context ctx;
    public ForecastAdapter(ArrayList<Weather> fiveDays, Context ctx) {
        this.ctx = ctx;
        this.days = fiveDays;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_activity, parent, false);
        return new ForecastAdapter.ViewHolder(rv_layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Weather w = days.get(position);
        String dayNum = w.getDayIcon() >= 10 ? w.getDayIcon() + "" : "0" + w.getDayIcon();
        String dayURL = "http://developer.accuweather.com/sites/default/files/" + dayNum + "-s.png";
        Picasso.get().load(dayURL).into(holder.icon);

        holder.text.setText(w.getDayWeather());
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.dayIconImageView);
            text = itemView.findViewById(R.id.dayWeatherSnippet);
        }
    }
}
