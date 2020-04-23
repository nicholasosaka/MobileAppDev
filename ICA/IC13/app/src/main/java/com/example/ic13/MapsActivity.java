package com.example.ic13;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String TAG = "IC13-MA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        String json = getJsonFromAssets(this, "trip.json");
        getLocationsFromJson(json);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Locations locations = getLocationsFromJson(getJsonFromAssets(this, "trip.json"));

        Log.d(TAG, "Locations: " + locations);

        PolylineOptions polylineOptions = new PolylineOptions();
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        for(Location l : locations.getPoints()){
            LatLng latLng = new LatLng(l.getLatitude(), l.getLongitude());
            polylineOptions.add(latLng);
            latLngBuilder.include(latLng);
        }

        mMap.addPolyline(polylineOptions);
        final LatLngBounds latLngBounds = latLngBuilder.build();

        Location start = locations.getPoints().get(0);
        Location end = locations.getPoints().get(locations.getPoints().size()-1);

        MarkerOptions startMarker = new MarkerOptions().position(new LatLng(start.getLatitude(), start.getLongitude())).title("Start");
        MarkerOptions endMarker = new MarkerOptions().position(new LatLng(end.getLatitude(), end.getLongitude())).title("End");

        mMap.addMarker(startMarker);
        mMap.addMarker(endMarker);


        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,100));
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,100));
            }
        });
    }

    String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }

    Locations getLocationsFromJson(String json){
        Gson gson = new Gson();
        Locations locations = gson.fromJson(json, Locations.class);
        Log.d(TAG, "GSON return: " + locations.toString());
        return locations;
    }

}
