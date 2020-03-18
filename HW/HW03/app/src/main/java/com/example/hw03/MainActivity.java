package com.example.hw03;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements CityAdapter.InteractWithCity{

    static final String CITY_KEY = "CITY_KEY";
    static final int CITY_WEATHER_ACTIVITY = 0;
    private String TAG = "HW03MAIN";

    City currentCity = null;

    private Handler handler;

    RecyclerView recyclerView;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;

    ArrayList<City> savedCities = new ArrayList<>();

    Intent toCityWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText cityNameEditText = findViewById(R.id.city_name_editText);
        final EditText countryNameEditText = findViewById(R.id.country_name_editText);

        toCityWeather = new Intent(MainActivity.this, CityWeather.class);

        recyclerView = findViewById(R.id.recylcerView);

        rv_adapter = new CityAdapter(savedCities, this);
        rv_layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(rv_adapter);
        recyclerView.setLayoutManager(rv_layoutManager);


        handler = new Handler(Looper.getMainLooper());

        Toast.makeText(this, isConnected() ? "Connected" : "Not Connected", Toast.LENGTH_SHORT).show();

        findViewById(R.id.search_city_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] str = {cityNameEditText.getText().toString(), countryNameEditText.getText().toString()};
                Log.d(TAG, Arrays.toString(str));


                final OkHttpClient client = new OkHttpClient();

                Log.d(TAG, "City: " + str[0] + ", Country: " + str[1]);

                String url = "https://dataservice.accuweather.com/locations/v1/cities/" + str[1] + "/search?apikey=" + getString(R.string.api_key) + "&q=" + str[0];

                Log.d(TAG, "URL: " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Log.d(TAG, "Request Built");

                final ArrayList<City> cities = new ArrayList<>();
                toggleProgressBar();

                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "FAILURE");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful()){
                                Log.d(TAG, "Response unsuccessful");

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "No connection", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                throw new IOException("Unexpected code " + response);
                            }

                            Log.d(TAG, "Response successful");
                            JSONArray root = new JSONArray(responseBody.string());

                            int len = root.length() < 5 ? root.length() : 5;

                            for(int i = 0; i < len; i++){
                                JSONObject cityJSONData = root.getJSONObject(i);
                                String cityName = cityJSONData.getString("EnglishName");
                                String cityID = cityJSONData.getInt("Key") + "";

                                String cityCountry = cityJSONData.getJSONObject("Country").getString("ID");

                                String cityRegion = cityJSONData.getJSONObject("AdministrativeArea").getString("ID");

                                City city = new City(cityID, cityName, cityRegion, cityCountry);
                                cities.add(city);
                                Log.d(TAG, city.toString());
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    toggleProgressBar();
                                    CharSequence[] list = new CharSequence[cities.size()];

                                    for(int i = 0; i < cities.size(); i++){
                                        list[i] = cities.get(i).regionString();
                                    }

                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Select City")
                                            .setCancelable(false)
                                            .setItems(list, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, final int which) {
                                                    toCityWeather.putExtra(CITY_KEY, cities.get(which));

                                                    startActivityForResult(toCityWeather, CITY_WEATHER_ACTIVITY);

                                                }
                                            })
                                            .create()
                                            .show();
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });


        View.OnClickListener changeCurrentCityListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View alertDiagView = inflater.inflate(R.layout.alert_dialog_get_city_details, null, false);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Enter City Details")
                        .setCancelable(false)
                        .setView(alertDiagView)
                        .setPositiveButton(R.string.alert_dialog_positive, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText cityNameEditText = alertDiagView.findViewById(R.id.ad_city_name);
                                EditText countryNameEditText = alertDiagView.findViewById(R.id.ad_country_name);

                                String[] str = {cityNameEditText.getText().toString(), countryNameEditText.getText().toString()};
                                Log.d(TAG, Arrays.toString(str));


                                final OkHttpClient client = new OkHttpClient();

                                Log.d(TAG, "City: " + str[0] + ", Country: " + str[1]);

                                String url = "https://dataservice.accuweather.com/locations/v1/cities/" + str[1] + "/search?apikey=" + getString(R.string.api_key) + "&q=" + str[0];

                                Log.d(TAG, "URL: " + url);
                                Request request = new Request.Builder()
                                        .url(url)
                                        .build();

                                Log.d(TAG, "Request Built");

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        toggleProgressBar();
                                    }
                                });

                                client.newCall(request).enqueue(new Callback() {

                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Log.d(TAG, "FAILURE");
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        try (ResponseBody responseBody = response.body()) {
                                            if (!response.isSuccessful()){
                                                Log.d(TAG, "Response unsuccessful");

                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(MainActivity.this, "No connection", Toast.LENGTH_SHORT).show();

                                                        toggleProgressBar();
                                                    }
                                                });

                                                throw new IOException("Unexpected code " + response);
                                            }

                                            Log.d(TAG, "Response successful");
                                            JSONArray root = new JSONArray(responseBody.string());

                                            JSONObject cityJSONData = root.getJSONObject(0);
                                            String cityName = cityJSONData.getString("EnglishName");
                                            String cityID = cityJSONData.getInt("Key") +"";

                                            String cityCountry = cityJSONData.getJSONObject("Country").getString("EnglishName");

                                            String cityRegion = cityJSONData.getJSONObject("AdministrativeArea").getString("ID");

                                            final City city = new City(cityID, cityName, cityRegion, cityCountry);

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    setCurrentCity(city);
                                                    toggleProgressBar();
                                                }
                                            });

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    toggleProgressBar();
                                                    Toast.makeText(MainActivity.this, "Error. Please try again.", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    }
                                });


                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_negative, null)
                        .create()
                        .show();
            }
        };

        findViewById(R.id.set_current_city_button).setOnClickListener(changeCurrentCityListener);
        findViewById(R.id.current_city).setOnClickListener(changeCurrentCityListener);
    }


    public void toggleProgressBar() {
        ProgressBar pb = findViewById(R.id.progressBar);
        pb.setVisibility(pb.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
    }

    public void setVisibilityCurrentCity(boolean visibility){
        ImageView weatherIcon = findViewById(R.id.current_city_image_view);
        TextView time = findViewById(R.id.current_city_updated_time);
        TextView weather = findViewById(R.id.current_city_weather);
        TextView temp = findViewById(R.id.current_city_temp);

        if(visibility){
            weather.setVisibility(View.VISIBLE);
            weatherIcon.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
            temp.setVisibility(View.VISIBLE);
            findViewById(R.id.set_current_city_button).setVisibility(View.INVISIBLE);
        } else {
            weather.setVisibility(View.INVISIBLE);
            weatherIcon.setVisibility(View.INVISIBLE);
            time.setVisibility(View.INVISIBLE);
            temp.setVisibility(View.INVISIBLE);
            findViewById(R.id.set_current_city_button).setVisibility(View.VISIBLE);

        }
    }

    private void setCurrentCity(final City current) {
        Log.d(TAG, "Setting current city to: " + current);
        currentCity = current;

        final OkHttpClient client = new OkHttpClient();

        String baseURL = "http://dataservice.accuweather.com/currentconditions/v1/";
        String extendedURL = "?apikey=" + getString(R.string.api_key);

        String url = baseURL + current.getId() + extendedURL;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    final JSONObject root = new JSONArray(responseBody.string()).getJSONObject(0);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ImageView icon = findViewById(R.id.current_city_image_view);
                                TextView temp = findViewById(R.id.current_city_temp);
                                TextView time = findViewById(R.id.current_city_updated_time);
                                TextView cityName = findViewById(R.id.current_city);
                                TextView weatherText = findViewById(R.id.current_city_weather);

                                String tempText = root.getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value") + "C";
                                temp.setText(String.format("Temperature: %s", tempText));

                                String number = root.getInt("WeatherIcon") >= 10 ? root.getInt("WeatherIcon") + "" : "0" + root.getInt("WeatherIcon");
                                String url = "http://developer.accuweather.com/sites/default/files/" + number + "-s.png";
                                Picasso.get().load(url).into(icon);


                                long diff = System.currentTimeMillis() / 1000 - root.getInt("EpochTime");
                                Log.d(TAG, "Diff: " + diff);
                                PrettyTime p = new PrettyTime(new Date(diff * 1000));
                                time.setText(String.format("Updated %s", p.format(new Date(0))));

                                cityName.setText(String.format("%s, %s", current.getName(), current.getRegion()));

                                weatherText.setText(root.getString("WeatherText"));


                                setVisibilityCurrentCity(true);
                                Toast.makeText(MainActivity.this, "Current City saved", Toast.LENGTH_SHORT).show();
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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

    private void checkSavedCitiesCount(){
        if(savedCities.size() <= 0){
            findViewById(R.id.no_cities_textView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.no_cities_textView).setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void selectCity(City selection) {
        //TODO intent to go to weather page
    }

    @Override
    public void onItemLongClicked(int position) {
        City toRemove = savedCities.get(position);
        savedCities.remove(toRemove);
        rv_adapter.notifyDataSetChanged();
        checkSavedCitiesCount();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "in onActivityResult");
        if(data != null) {
            Log.d(TAG, "data not null");
            if (data.getBooleanExtra(CityWeather.SAVE, false)) {
                Log.d(TAG, "saving");

                final City c = (City) data.getSerializableExtra(MainActivity.CITY_KEY);
                assert c != null;

                final OkHttpClient client = new OkHttpClient();

                String baseURL = "http://dataservice.accuweather.com/currentconditions/v1/";
                String extendedURL = "?apikey=" + getString(R.string.api_key);


                String url = baseURL + c.getId() + extendedURL;

                Request request = new Request.Builder()
                        .url(url)
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "FAILED!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful())
                                throw new IOException("Unexpected code " + response);

                            final JSONObject root = new JSONArray(responseBody.string()).getJSONObject(0);

                            final Weather w = new Weather(c);

                            w.setMetricTemp(root.getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value"));
                            w.setEpochTime(root.getInt("EpochTime"));
                            w.setWeatherText(root.getString("WeatherText"));

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    savedCities.add(c);
                                    rv_adapter.notifyDataSetChanged();
                                    checkSavedCitiesCount();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            if (data.getBooleanExtra(CityWeather.CURRENT, false)){
                final OkHttpClient client = new OkHttpClient();
                final City c = (City) data.getSerializableExtra(MainActivity.CITY_KEY);
                assert c != null;

                Log.d(TAG, "City code: " + c.getId());


                String url = "https://dataservice.accuweather.com/locations/v1/cities/" + c.getCountry() + "/search?apikey=" + getString(R.string.api_key) + "&q=" + c.getId();

                Log.d(TAG, "URL: " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Log.d(TAG, "Request Built");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        toggleProgressBar();
                    }
                });

                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "FAILURE");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful()) {
                                Log.d(TAG, "Response unsuccessful");

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "No connection", Toast.LENGTH_SHORT).show();

                                        toggleProgressBar();
                                    }
                                });

                                throw new IOException("Unexpected code " + response);
                            }

                            Log.d(TAG, "Response successful");
                            JSONArray root = new JSONArray(responseBody.string());

                            JSONObject cityJSONData = root.getJSONObject(0);
                            String cityName = cityJSONData.getString("EnglishName");
                            String cityID = cityJSONData.getInt("Key") + "";

                            String cityCountry = cityJSONData.getJSONObject("Country").getString("EnglishName");

                            String cityRegion = cityJSONData.getJSONObject("AdministrativeArea").getString("ID");

                            final City city = new City(cityID, cityName, cityRegion, cityCountry);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setCurrentCity(city);
                                    toggleProgressBar();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    toggleProgressBar();
                                    Toast.makeText(MainActivity.this, "Error. Please try again.", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        }
    }
}
