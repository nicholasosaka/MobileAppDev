package com.example.hw03;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CityWeather extends AppCompatActivity {

    public static final String SAVE = "SAVE";
    public static final String CURRENT = "CURRENT";
    Handler handler;

    boolean willSave = false;

    ArrayList<Weather> fiveDays = new ArrayList<Weather>();

    RecyclerView recyclerView;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_manager;
    private String TAG  = "HW03CITYWEATHER";

    City c = null;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);
        setTitle("City Weather");
        handler = new Handler(Looper.getMainLooper());

         intent = getIntent();



        final TextView cityNameTV = findViewById(R.id.cityName);
        final TextView headlineTV = findViewById(R.id.headlineText);
        final TextView forecastTV = findViewById(R.id.forecastText);
        final TextView highLowTV = findViewById(R.id.highLowTemp);
        final TextView dayStatus = findViewById(R.id.dayWeatherCondition);
        final TextView nightStatus = findViewById(R.id.nightWeatherCondition);
        final ImageView dayIcon = findViewById(R.id.dayWeatherIcon);
        final ImageView nightIcon = findViewById(R.id.nightWeatherIcon);

        final TextView linkTV = findViewById(R.id.detailsLink);

        recyclerView = findViewById(R.id.recyclerViewWeathers);

        rv_adapter = new ForecastAdapter(fiveDays, this);
        rv_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


        recyclerView.setAdapter(rv_adapter);
        recyclerView.setLayoutManager(rv_manager);


        final City city = (City) intent.getSerializableExtra(MainActivity.CITY_KEY);
        assert city != null;


        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SAVING CITY");
                intent.putExtra(MainActivity.CITY_KEY, city);
                intent.putExtra(SAVE, true);
            }
        });

        findViewById(R.id.setCurrent).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                intent.putExtra(MainActivity.CITY_KEY, city);
                intent.putExtra(CURRENT, true);
            }
        });

        findViewById(R.id.cityWeatherProgress).setVisibility(View.VISIBLE);

        final OkHttpClient client = new OkHttpClient();

        //http://dataservice.accuweather.com/forecasts/v1/daily/5day/349818?apikey=PGqDyZPAzA1nZAuLxjibgaNhg8NZFUaa

        String baseURL = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/";
        String extendedURL = "?apikey=" + getString(R.string.api_key);


        String url = baseURL + city.getId() + extendedURL;

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

                    final JSONObject root = new JSONObject(responseBody.string());
                    final Weather w = new Weather(city);


                    JSONObject headline = root.getJSONObject("Headline");

                    JSONArray fiveDayArray = root.getJSONArray("DailyForecasts");

                    for(int i = 0; i < fiveDayArray.length(); i++){
                        Weather dailyWeather = new Weather();
                        JSONObject day = fiveDayArray.getJSONObject(i);
                        dailyWeather.setEpochTime(day.getInt("EpochDate"));
                        dailyWeather.setWeatherIcon(day.getJSONObject("Day").getInt("Icon"));
                        dailyWeather.setMaxTemp((int) day.getJSONObject("Temperature").getJSONObject("Maximum").getDouble("Value"));
                        dailyWeather.setMinTemp((int) day.getJSONObject("Temperature").getJSONObject("Minimum").getDouble("Value"));
                        dailyWeather.setDayIcon(day.getJSONObject("Day").getInt("Icon"));
                        dailyWeather.setNightIcon(day.getJSONObject("Night").getInt("Icon"));
                        dailyWeather.setNightWeather(day.getJSONObject("Night").getString("IconPhrase"));
                        dailyWeather.setDayWeather(day.getJSONObject("Day").getString("IconPhrase"));

                        Log.d(TAG, "Day num: " + dailyWeather.getDayIcon() + " Night num: " + dailyWeather.getNightIcon());

                        fiveDays.add(dailyWeather);
                    }

                    w.setEpochTime(headline.getInt("EffectiveEpochDate"));
                    final String headlineText = headline.getString("Text");
                    final String link = headline.getString("MobileLink");
                    w.setWeatherIcon(fiveDays.get(0).getWeatherIcon());


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            city.setWeather(w);

                            Weather defaultWeather = fiveDays.get(0);

                            cityNameTV.setText(String.format("%s, %s", city.getName(), city.getCountry()));
                            headlineTV.setText(headlineText);
                            forecastTV.setText(String.format("Forecast on %s", prettyDate(w.getEpochTime())));
                            highLowTV.setText("Temperature " + defaultWeather.getMaxTemp() + "/" + defaultWeather.getMinTemp() +"F");
                            String dayNum = defaultWeather.getDayIcon() >= 10 ? defaultWeather.getDayIcon() + "" : "0" + defaultWeather.getDayIcon();
                            String nightNum = defaultWeather.getNightIcon() >= 10 ? defaultWeather.getNightIcon() + "" : "0" + defaultWeather.getNightIcon();
                            nightStatus.setText(defaultWeather.getNightWeather());
                            dayStatus.setText(defaultWeather.getDayWeather());

                            linkTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(link));
                                    startActivity(i);
                                }
                            });

                            String dayURL = "http://developer.accuweather.com/sites/default/files/" + dayNum + "-s.png";
                            String nightURL = "http://developer.accuweather.com/sites/default/files/" + nightNum + "-s.png";

                            Picasso.get().load(dayURL).into(dayIcon);
                            Picasso.get().load(nightURL).into(nightIcon);

                            rv_adapter.notifyDataSetChanged();

                            findViewById(R.id.cityWeatherProgress).setVisibility(View.INVISIBLE);

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Finishing");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private String prettyDate(int epochTime) {
        Date d = new Date(epochTime);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(d);
    }
}
