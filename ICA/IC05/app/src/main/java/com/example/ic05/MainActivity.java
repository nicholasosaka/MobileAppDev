package com.example.ic05;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView iv_photo;
    ImageView iv_next;
    ImageView iv_prev;
    Button btn;
    ArrayList<String> links = new ArrayList<>();

    ProgressBar progressBar;

    int currentLink = 0;
    boolean isURLValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        btn = findViewById(R.id.search_button);
        iv_next = findViewById(R.id.next_image);
        iv_prev = findViewById(R.id.prev_image);
        iv_photo = findViewById(R.id.imageView);

        progressBar.setVisibility(View.INVISIBLE);
        iv_prev.setVisibility(View.INVISIBLE);
        iv_next.setVisibility(View.INVISIBLE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetKeywords().execute();
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isURLValid) return;

                Log.d("ICA05", "Links:" + links);
                currentLink = ++currentLink % links.size();

                Log.d("ICA05", "Moving forward to link #" + currentLink);
                progressBar.setVisibility(View.VISIBLE);
                iv_photo.setImageResource(0);
                new GetImage(iv_photo).execute(links.get(currentLink));
                Log.d("ICA05", "currentLink: " + currentLink);
            }
        });

        iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isURLValid) return;

                Log.d("ICA05", "Links:" + links);
                currentLink = --currentLink;

                if(currentLink < 0){
                    currentLink = links.size() -1;
                }

                Log.d("ICA05", "Moving backwards to link #" + currentLink);
                progressBar.setVisibility(View.VISIBLE);
                iv_photo.setImageResource(0);
                new GetImage(iv_photo).execute(links.get(currentLink));
                Log.d("ICA05", "currentLink: " + currentLink);

            }
        });


        if(isConnected()){
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
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

    class GetImageURLS extends AsyncTask<String, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            String strUrl = null;
            String result = null;
            ArrayList<String> results = new ArrayList<>();

            try {
                strUrl = "http://dev.theappsdr.com/apis/photos/index.php" + "?" +
                        "keyword=" + URLEncoder.encode(strings[0], "UTF-8");
                URL url = new URL(strUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = IOUtils.toString(connection.getInputStream(), "UTF8");

                    String[] substrings = result.split("\n");

                    for(String s : substrings){
                        Log.d("ICA05", "URL: " + s);
                        results.add(s.trim());
                    }
                }


            } catch (UnsupportedEncodingException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            Log.d("ICA05", "Array size: " + strings.size());
            if(strings.size() == 0 | strings.get(0).length() == 0){
                Toast.makeText(MainActivity.this, "No Images Found", Toast.LENGTH_LONG).show();
                isURLValid = false;
                iv_photo.setImageResource(0);
                validateButtons();
                return;
            }
            Log.d("ICA05", "post Image URLS: " + strings);
            links = strings;
            progressBar.setVisibility(View.VISIBLE);
            isURLValid = true;
            new GetImage(iv_photo).execute(links.get(0));
            currentLink = 0;
        }
    }

    class GetKeywords extends AsyncTask<Void, Void, String[]>{

        @Override
        protected String[] doInBackground(Void... voids) {

            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result;
            String[] keywordStrings = new String[0];
            try {
                URL url = new URL("http://dev.theappsdr.com/apis/photos/keywords.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    result = stringBuilder.toString();

                    keywordStrings = result.split(";");
                    Log.d("ICA05", Arrays.toString(keywordStrings));

                } else {
                    Log.d("ICA05", "Failed to establish HTTP_OK");
                }
            } catch (Exception e) {
                Log.e("ICA05", Objects.requireNonNull(e.getMessage()));
            } finally {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return keywordStrings;
        }

        @Override
        protected void onPostExecute(final String[] strings) {
            super.onPostExecute(strings);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Seelct a keyword")
                    .setItems(strings, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String keyword = strings[which];
                            Log.d("ICA05", "Clicked on: " + keyword);
                            new GetImageURLS().execute(keyword);
                            TextView tv = findViewById(R.id.textView);
                            tv.setText(keyword);
                        }
                    });

            builder.create().show();
        }
    }

    class GetImage extends AsyncTask<String, Void, Void>{

        ImageView iv;
        Bitmap bitmap = null;

        public GetImage(ImageView iv) {
            this.iv = iv;
        }

        @Override
        protected Void doInBackground(String... strings) {

            if(strings[0].length() == 0){
                return null;
            }

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(bitmap != null && iv != null){
                iv.setImageBitmap(bitmap);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (iv != null){
                iv.setImageResource(0);
                progressBar.setVisibility(View.INVISIBLE);
            }

            validateButtons();
        }
    }

    private void validateButtons() {
        if(links.size() <= 1 || !isURLValid){
            iv_prev.setVisibility(View.INVISIBLE);
            iv_next.setVisibility(View.INVISIBLE);
        } else {
            iv_prev.setVisibility(View.VISIBLE);
            iv_next.setVisibility(View.VISIBLE);
        }
    }
}
