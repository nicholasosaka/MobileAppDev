package com.example.ic07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IC07";
    TextView progressStatus;
    ProgressBar progressBar;
    Button start_button;

    String url = "http://dev.theappsdr.com/apis/trivia_json/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressStatus = findViewById(R.id.progress_status_tv);
        progressBar = findViewById(R.id.progressBar);
        start_button = findViewById(R.id.start_button);

        start_button.setEnabled(false); //disable button

        Toast.makeText(this, isConnected() ? "Connected" : "Not Connected", Toast.LENGTH_SHORT).show();

        new GetJSONData().execute(url);


    }

    class GetJSONData extends AsyncTask<String, Void, ArrayList<Question>> {
        boolean dataRetrieved = false;

        @Override
        protected ArrayList<Question> doInBackground(String... strings) {
            ArrayList<Question> questions = new ArrayList<>();

            HttpURLConnection connection;

            try{
                Log.d(TAG, "Accessing URL: " + strings[0]);

                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray jsonQuestions = root.getJSONArray("questions");

                    for(int i = 0; i < jsonQuestions.length(); i++){
                        JSONObject o = (JSONObject) jsonQuestions.get(i);
                        Question q = new Question(o);
                        Log.d(TAG, "Added " + q);
                    }

                    dataRetrieved = true;

                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return questions;
        }

        @Override
        protected void onPostExecute(ArrayList<Question> questions) {
            super.onPostExecute(questions);

            if(dataRetrieved) {
                start_button.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                ImageView banner = findViewById(R.id.trivia_banner);
                banner.setVisibility(View.VISIBLE);

                progressStatus.setText(R.string.trivia_ready_label);
            }
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

}
