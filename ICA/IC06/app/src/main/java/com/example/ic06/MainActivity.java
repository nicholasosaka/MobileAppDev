package com.example.ic06;
/*
    ICA06
    MainActivity.java
    Nicholas Osaka & Alexandria Benedict
 */

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    String baseURL = "http://newsapi.org/v2/top-headlines";
    private String TAG = "IC06";

    Button btn;

    TextView title;
    TextView publishedAt;
    TextView description;
    TextView counter;

    ImageView next;
    ImageView prev;

    ImageView image;

    int currentIndex = 0;

    ArrayList<News> newsList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] possibleCategories = {"Business", "Entertainment", "General", "Health", "Science",  "Sports",  "Technology"};

        title = findViewById(R.id.title);
        publishedAt = findViewById(R.id.publishedAt);
        description = findViewById(R.id.description);
        counter = findViewById(R.id.counter);

        image = findViewById(R.id.imageView);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);

        image.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        prev.setVisibility(View.INVISIBLE);

        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Choose Category")
                        .setItems(possibleCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(isConnected()) {
                                    String category = possibleCategories[which].toLowerCase();
                                    Log.d(TAG, "Clicked on " + category);
                                    new GetJSONData().execute(category);
                                    TextView tv = findViewById(R.id.category_label);
                                    tv.setText(possibleCategories[which]);
                                } else {
                                    Toast.makeText(MainActivity.this, "No Valid Connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.create().show();
            }
        });

        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                currentIndex = ++currentIndex % newsList.size();

                populateInfo();
            }

        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(--currentIndex < 0){
                    currentIndex = newsList.size() -1;
                }

                populateInfo();
            }
        });




        Log.d(TAG, "END");

        if(isConnected()){
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_LONG).show();
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

    class GetJSONData extends AsyncTask<String, Void, ArrayList<News>> {

        @Override
        protected ArrayList<News> doInBackground(String... strings) {
            Log.d(TAG, "In doInBackground");
            HttpURLConnection connection = null;
            ArrayList<News> newsItems = new ArrayList<>();
            String category = strings[0];


            try{
                String strURL = baseURL
                        + "?apiKey=" + getResources().getString(R.string.news_api_key)
                        + "&category=" + URLEncoder.encode(category, "UTF-8");

                URL url = new URL(strURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray articles = root.getJSONArray("articles");

                    for(int i = 0; i < articles.length(); i++){
                        JSONObject article = (JSONObject) articles.get(i);
                        String titleText = article.getString("title");
                        String imageUrlText = article.getString("urlToImage");
                        String descriptionText = article.getString("description");
                        String publishedAtText = article.getString("publishedAt");

                        News news = new News(titleText,publishedAtText, imageUrlText, descriptionText);

                        newsItems.add(news);

                    }

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


            Log.d(TAG, "Returning from doInBackground, news size: " + newsItems.size());
            return newsItems;
        }

        @Override
        protected void onPostExecute(ArrayList<News> news) {
            super.onPostExecute(news);
            Log.d(TAG, "In PostExecute, news size: " + news.size());

            for(News n : news){
                Log.d(TAG, "Article: " + n.toString());
            }

            navigationVisibility(news.size() > 1);
            currentIndex = 0;

            if(news.size() > 0){
                image.setVisibility(View.VISIBLE);
                newsList = news;
                populateInfo();
            } else {
                Toast.makeText(MainActivity.this, "No News Found", Toast.LENGTH_SHORT).show();
            }

        }
    }




    public void populateInfo(){
        News news = newsList.get(currentIndex);

        Log.d(TAG, "Image URL for " + currentIndex + ": " + news.imageUrl);

        if(news.imageUrl.equalsIgnoreCase("null")){
            image.setImageResource(R.mipmap.ic_launcher);
            Toast.makeText(MainActivity.this, "No Image Associated with Article", Toast.LENGTH_SHORT).show();
        } else {
            Picasso.get().load(news.imageUrl).into(image);
        }

        title.setText(news.title);
        publishedAt.setText(news.publishedAt);
        description.setText(news.description);

        counter.setText(new StringBuilder().append(currentIndex + 1).append(" of ").append(newsList.size()).toString());
    }



    public void navigationVisibility(boolean lightswitch){
        if(lightswitch){
            Log.d(TAG, "Turning navigations ON");
            next.setVisibility(View.VISIBLE);
            prev.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Turning navigations OFF");
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.INVISIBLE);
        }
    }
}
