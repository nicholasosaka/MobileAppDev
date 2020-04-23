package com.example.inclass11;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Expense implements Serializable {
    private Date date;
    private String title;
    private String category;
    private double cost;
    private String id;
    private static final String TAG = "IC11-Expense";

    private HashMap<String, Object> hashmap;

    public Expense(String title, String category, double cost, Date date) {
        this.title = title;
        this.category = category;
        this.cost = cost;
        this.date = date;

        Log.d(TAG, "Expense Generated: " + toString());
    }

    public Expense(String firebaseID, Map<String, Object> firebaseData) {
        this.title = (String) firebaseData.get("title");
        this.category = (String) firebaseData.get("category");
        this.cost = Double.parseDouble(firebaseData.get("cost").toString());
        this.date = ((Timestamp) firebaseData.get("date")).toDate();
        this.id = firebaseID;

        Log.d(TAG, "Expense Generated: " + toString());
    }

    @Override
    public String toString() {
        return "Expense{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", cost=" + cost +
                ", date=" + date +
                ", id='" + id + '\'' +
                '}';
    }

    public HashMap<String, Object> toHashMap(){
        hashmap = new HashMap<>();
        hashmap.put("title", getTitle());
        hashmap.put("category", getCategory());
        hashmap.put("cost", getCost());
        hashmap.put("date", getDate());

        return this.hashmap;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
