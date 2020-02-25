package com.example.hw02;
/*
    HW02
    Track.java
    Nicholas Osaka & Alexandria Benedict
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Track implements Serializable {
    private String name;
    private String genre;
    private String artist;
    private String album;
    private double trackPrice;
    private double albumPrice;
    private String date;
    private String albumArtURL;

    public Track(JSONObject trackObject) {
        try {
            this.name = trackObject.getString("trackName");
            this.genre = trackObject.getString("primaryGenreName");
            this.artist = trackObject.getString("artistName");
            this.album = trackObject.getString("collectionName");
            this.trackPrice = trackObject.getDouble("trackPrice");
            this.albumPrice = trackObject.getDouble("collectionPrice");

            String raw = trackObject.getString("releaseDate");
            String[] datePieces = raw.substring(0,raw.indexOf("T")).split("-");

            this.date = datePieces[1] + "-" + datePieces[2] + "-" + datePieces[0];
            this.albumArtURL = trackObject.getString("artworkUrl100");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Track(String name, String genre, String artist, String album, String date, double trackPrice, double albumPrice) {
        this.name = name;
        this.genre = genre;
        this.artist = artist;
        this.album = album;
        this.trackPrice = trackPrice;
        this.albumPrice = albumPrice;
        this.date = date;
    }

    public String getAlbumArtURL() {
        return albumArtURL;
    }

    public int getYear(){
        return Integer.parseInt(date.split("-")[2]);
    }

    public int getDay(){
        return Integer.parseInt(date.split("-")[1]);
    }

    public int getMonth(){
        return Integer.parseInt(date.split("-")[0]);
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public double getTrackPrice() {
        return trackPrice;
    }

    public double getAlbumPrice() {
        return albumPrice;
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", trackPrice=" + trackPrice +
                ", date='" + date + '\'' +
                '}';
    }
}
