package com.example.hw02;
/*
    HW02
    MainActivity.java
    Nicholas Osaka & Alexandria Benedict
 */
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    static final String TRACK_KEY = "TRACK";
    private String TAG = "HW02";

    Button searchButton;
    Button resetButton;

    EditText searchBar;
    SeekBar seekBar;

    TextView limitLabel;

    Switch toggle;

    int seekBarValue;
    boolean toggleStatus;

    ListView resultContainer;

    String baseURL = "https://itunes.apple.com/search";

    ArrayList<Track> tracks;

    TrackAdapter adapter;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("iTunes Music Search");

        tracks = new ArrayList<>();
        toggleStatus = true;

        progressBar = findViewById(R.id.progressBar);

        resultContainer = findViewById(R.id.list_view);

        searchButton = findViewById(R.id.search_button);
        resetButton = findViewById(R.id.reset_button);

        searchBar = findViewById(R.id.search_bar);
        seekBar = findViewById(R.id.seekBar);
        seekBarValue = 10;

        limitLabel = findViewById(R.id.limit_label);

        toggle = findViewById(R.id.sort_toggle);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue = progress + 10;
                Log.d(TAG, "PROGRESS: " + seekBarValue);
                String labelText = "Limit: " + seekBarValue;
                limitLabel.setText(labelText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarValue = 0;
                seekBar.setProgress(0);
                limitLabel.setText(R.string.seek_bar_tv_default);
                searchBar.setText("");
                toggle.setChecked(true);


                tracks = new ArrayList<>();
                adapter = new TrackAdapter(MainActivity.this, R.layout.track_item, tracks);
                resultContainer.setAdapter(adapter);

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected()){
                    Toast.makeText(MainActivity.this, "Not Connected", Toast.LENGTH_LONG).show();
                } else {

                    String searchText = searchBar.getText().toString().trim();

                    if (searchText.length() <= 0) {
                        searchBar.setError("Please enter search term");
                    } else {
                        new GetJSONData().execute(searchText);
                    }

                }
            }
        });

        resultContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = tracks.get(position);

                Intent toDisplayDetails = new Intent(MainActivity.this, DisplayDetailsActivity.class);

                toDisplayDetails.putExtra(TRACK_KEY, track);

                startActivity(toDisplayDetails);
            }
        });

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG,"TOGGLE: " + isChecked);
                toggleStatus = isChecked;

                sort(tracks);

                adapter = new TrackAdapter(MainActivity.this, R.layout.track_item, tracks);
                resultContainer.setAdapter(adapter);

            }
        });

        if(isConnected()){
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_LONG).show();
        }

    }

    void sort(ArrayList<Track> tracks) {
        if(toggleStatus){ //date
            Log.d(TAG, "Sorting by DATE");
            Collections.sort(tracks, new Comparator<Track>() {
                @Override
                public int compare(Track t1, Track t2) {
                    if(t1.getYear() > t2.getYear()){
                        return -1;
                    } else if(t1.getYear() < t2.getYear()){
                        return 1;
                    }

                    if(t1.getMonth() > t2.getMonth()){
                        return -1;
                    } else if(t1.getMonth() < t2.getMonth()){
                        return 1;
                    }

                    if(t1.getDay() > t2.getDay()){
                        return -1;
                    } else if(t1.getDay() < t2.getDay()){
                        return 1;
                    }

                    return 0;
                }
            });
            Log.d(TAG, "Sorted");

        } else { //price
            Log.d(TAG, "Sorting by PRICE");

            Collections.sort(tracks, new Comparator<Track>() {
                @Override
                public int compare(Track t1, Track t2) {
                    double t1p = t1.getTrackPrice();
                    double t2p = t2.getTrackPrice();

                    if(t1p > t2p){
                        return 1;
                    } else if (t1p < t2p){
                        return -1;
                    }

                    return 0;
                }
            });
            Log.d(TAG, "Sorted");

        }

    }

    class GetJSONData extends AsyncTask<String, Void, ArrayList<Track>>{

        @Override
        protected ArrayList<Track> doInBackground(String... strings) {
            ArrayList<Track> retrievedTracks = new ArrayList<>();
            HttpURLConnection connection;

            try{
                String strURL = baseURL +
                        "?term=" + URLEncoder.encode(strings[0], "UTF-8") +
                        "&limit=" + URLEncoder.encode(String.valueOf(seekBarValue), "UTF-8") +
                        "&media=music"; //added music filter to API because podcasts are free, so filtering became kind of useless.

                Log.d(TAG, "PARSED URL: " + strURL);

                URL url = new URL(strURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray results = root.getJSONArray("results");

                    for(int i = 0; i < results.length(); i++){
                        Track track = new Track(results.getJSONObject(i));
                        Log.d(TAG, "Retrieved: " + track);
                        retrievedTracks.add(track);
                    }

                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return retrievedTracks;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(ArrayList<Track> retrievedTracks) {
            super.onPostExecute(retrievedTracks);
            progressBar.setVisibility(View.INVISIBLE);


            sort(retrievedTracks);

            adapter = new TrackAdapter(MainActivity.this, R.layout.track_item, retrievedTracks);

            resultContainer.setAdapter(adapter);

            tracks = retrievedTracks;
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected() &&
                (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }


}
