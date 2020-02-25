package com.example.hw02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/*
    HW02
    TrackAdapter.java
    Nicholas Osaka & Alexandria Benedict
 */

import java.util.List;

public class TrackAdapter extends ArrayAdapter<Track> {

    public TrackAdapter(@NonNull Context context, int resource, @NonNull List<Track> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Track track = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_item, parent, false);
        }
        TextView trackLabel = convertView.findViewById(R.id.track_label);
        TextView artistLabel = convertView.findViewById(R.id.artist_label);
        TextView priceLabel = convertView.findViewById(R.id.price_label);
        TextView dateLabel = convertView.findViewById(R.id.date_label);

        String trackLabelText = "Track: " + track.getName();
        String artistLabelText = "Artist: " + track.getArtist();
        String priceLabelText = "Price: $" + track.getTrackPrice();
        String dateLabelText = "Date: " + track.getDate();

        trackLabel.setText(trackLabelText);
        artistLabel.setText(artistLabelText);
        priceLabel.setText(priceLabelText);
        dateLabel.setText(dateLabelText);

        return convertView;
    }
}
