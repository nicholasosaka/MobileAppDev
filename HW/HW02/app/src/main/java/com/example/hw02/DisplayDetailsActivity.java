package com.example.hw02;
/*
    HW02
    DisplayDetailsActivity.java
    Nicholas Osaka & Alexandria Benedict
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DisplayDetailsActivity extends AppCompatActivity {

    Track track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details);

        setTitle("Details");

        Intent intent = getIntent();
        track = (Track) intent.getSerializableExtra(MainActivity.TRACK_KEY);

        TextView title = findViewById(R.id.title_label);
        TextView artist = findViewById(R.id.artist_label);
        TextView genre = findViewById(R.id.genre_label);
        TextView album = findViewById(R.id.album_label);

        TextView trackPrice = findViewById(R.id.track_price_label);
        TextView albumPrice = findViewById(R.id.album_price_label);


        String strTitle = "Track: " + track.getName();
        title.setText(strTitle);

        String strArtist = "Artist: " + track.getArtist();
        artist.setText(strArtist);

        String strAlbum = "Album: " + track.getAlbum();
        album.setText(strAlbum);

        String strGenre = "Genre: " + track.getGenre();
        genre.setText(strGenre);

        String strTrackPrice = "Track Price: $" + track.getTrackPrice();
        trackPrice.setText(strTrackPrice);

        String strAlbumPrice = "Album Price: $" + track.getAlbumPrice();
        albumPrice.setText(strAlbumPrice);


        ImageView albumArt = findViewById(R.id.album_art);

        Picasso.get().load(track.getAlbumArtURL()).into(albumArt);


        findViewById(R.id.finish_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
