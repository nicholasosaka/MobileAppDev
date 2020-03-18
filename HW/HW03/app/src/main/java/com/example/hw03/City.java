package com.example.hw03;

import java.io.Serializable;

class City implements Serializable {
    private String id;
    private String name;
    private String region;
    private String country;
    private Weather weather;
    private boolean favorite;

    public City (String id, String name, String region, String country) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.country = country;

        this.weather = new Weather(this);
        this.favorite = false;
    }

    public City(String id, String name, String region, String country, boolean favorite) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.country = country;
        this.favorite = favorite;

        this.weather = new Weather(this);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return id + ": " + name + ", " + region + ", " + country;
    }

    public String regionString() {
        return name +  ", " + region;
    }
}
