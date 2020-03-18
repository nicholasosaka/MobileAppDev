package com.example.hw03;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private static final String TAG = "HW03CityAdapter";
    public static InteractWithCity interact;

    public interface InteractWithCity{
        void selectCity(City selection);
        void onItemLongClicked(int position);
    }




    ArrayList<City> cities;
    Context ctx;

    public CityAdapter(ArrayList<City> cities, Context ctx) {
        this.ctx = ctx;
        this.cities = cities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout rv_layout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.city_layout, parent, false);
        return new ViewHolder(rv_layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        interact = (InteractWithCity) ctx;

        holder.itemView.setLongClickable(true);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                interact.onItemLongClicked(position);
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "City: " + cities.get(position));
                Log.d(TAG, "Weather: " + cities.get(position).getWeather());
                interact.selectCity(cities.get(position));
            }
        });

        Log.d(TAG, "in onBindViewHolder with position: " + position);

        final City c = cities.get(position);
        Weather w = c.getWeather();

        Log.d(TAG, "City: " + c);
        Log.d(TAG, "Weather: " + w);

        holder.name.setText(String.format("%s, %s", c.getName(), c.getRegion()));
        holder.temp.setText(String.format("Temperature: %s", w.getMetricTemp()));

        long diff = System.currentTimeMillis() / 1000 - w.getEpochTime();
        PrettyTime p = new PrettyTime(new Date(diff * 1000));
        holder.updated.setText(String.format("Updated %s", p.format(new Date(0))));

        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!c.isFavorite()){
                    Log.d(TAG, "favorited");
                    Picasso.get().load(android.R.drawable.star_big_on).into(holder.star);
                    c.setFavorite(true);
                } else {
                    Log.d(TAG, "unfavorited");
                    Picasso.get().load(android.R.drawable.star_big_off).into(holder.star);
                    c.setFavorite(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView temp;
        public TextView updated;
        public ImageView star;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "in ViewHolder constructor");
            name = itemView.findViewById(R.id.cityNameTextView);
            temp = itemView.findViewById(R.id.tempTextView);
            updated = itemView.findViewById(R.id.updatedTextView);
            star = itemView.findViewById(R.id.star);
        }
    }
}
