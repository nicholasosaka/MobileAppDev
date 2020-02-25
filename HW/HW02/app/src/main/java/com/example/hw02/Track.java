package com.example.hw02;

import org.json.JSONException;
import org.json.JSONObject;

public class Track {
    private String name;
    private String genre;
    private String artist;
    private String album;
    private double trackPrice;
    private double albumPrice;

    public Track(JSONObject trackObject) {
        try {
            this.name = trackObject.getString("trackName");
            this.genre = trackObject.getString("primaryGenreName");
            this.artist = trackObject.getString("artistName");
            this.album = trackObject.getString("collectionName");
            this.trackPrice = trackObject.getDouble("trackPrice");
            this.albumPrice = trackObject.getDouble("collectionPrice");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Track(String name, String genre, String artist, String album, double trackPrice, double albumPrice) {
        this.name = name;
        this.genre = genre;
        this.artist = artist;
        this.album = album;
        this.trackPrice = trackPrice;
        this.albumPrice = albumPrice;
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
                ", genre='" + genre + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", trackPrice='" + trackPrice + '\'' +
                ", albumPrice='" + albumPrice + '\'' +
                '}';
    }
}
