package com.example.hw03;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

class Weather implements Serializable {
    private City city;
    private String localObservationDatetime;
    private int epochTime;
    private int weatherIcon;
    private String weatherText;
    private double metricTemp;
    private int minTemp;
    private int maxTemp;
    private String link;

    private String DayWeather;
    private String NightWeather;
    private int DayIcon;
    private int NightIcon;

    public Weather(City city) {
        this.city = city;
    }

    public Weather() {
    }

    public Weather(City city, String localObservationDatetime, int epochTime, int weatherIcon, String weatherText, double metricTemp) {
        this.city = city;
        this.localObservationDatetime = localObservationDatetime;
        this.epochTime = epochTime;
        this.weatherIcon = weatherIcon;
        this.weatherText = weatherText;
        this.metricTemp = metricTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDayWeather() {
        return DayWeather;
    }

    public void setDayWeather(String dayWeather) {
        DayWeather = dayWeather;
    }

    public String getNightWeather() {
        return NightWeather;
    }

    public void setNightWeather(String nightWeather) {
        NightWeather = nightWeather;
    }

    public int getDayIcon() {
        return DayIcon;
    }

    public void setDayIcon(int dayIcon) {
        DayIcon = dayIcon;
    }

    public int getNightIcon() {
        return NightIcon;
    }

    public void setNightIcon(int nightIcon) {
        NightIcon = nightIcon;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "city=" + city +
                ", weatherIcon=" + weatherIcon +
                ", weatherText='" + weatherText + '\'' +
                ", metricTemp=" + metricTemp +
                '}';
    }

    public City getCity() {
        return city;
    }

    public String getLocalObservationDatetime() {
        return localObservationDatetime;
    }

    public int getEpochTime() {
        return epochTime;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }

    public String getWeatherText() {
        return weatherText;
    }

    public double getMetricTemp() {
        return metricTemp;
    }

    public void setLocalObservationDatetime(String localObservationDatetime) {
        this.localObservationDatetime = localObservationDatetime;
    }

    public void setEpochTime(int epochTime) {
        this.epochTime = epochTime;
    }

    public void setWeatherIcon(int weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public void setMetricTemp(double metricTemp) {
        this.metricTemp = metricTemp;
    }

    public void updateWeather(){
        final OkHttpClient client = new OkHttpClient();

        String baseURL = "http://dataservice.accuweather.com/currentconditions/v1/349818";
        String extendedURL = "?apikey=6PGlRKECwr72TgdkirGRGQsP7sBDehA7";

        String url = baseURL + getCity().getId() + extendedURL;

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

                    JSONObject root = new JSONArray(responseBody.string()).getJSONObject(0);

                    setLocalObservationDatetime(root.getString("LocalObservationDateTime"));
                    setEpochTime(root.getInt("EpochTime"));
                    setWeatherText(root.getString("WeatherText"));
                    setWeatherIcon(root.getInt("WeatherIcon"));
                    setMetricTemp(root.getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
