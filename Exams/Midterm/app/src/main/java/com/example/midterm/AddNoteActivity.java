package com.example.midterm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AddNoteActivity extends AppCompatActivity {

    private static final String TAG = "MIDTERM_ADD";
    EditText titleET;
    EditText textET;
    Button add;
    Button cancel;

    Handler handler;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        add = findViewById(R.id.addAddButton);
        cancel = findViewById(R.id.addCancelButton);
        titleET = findViewById(R.id.addTitleET);
        textET = findViewById(R.id.addTextET);

        handler = new Handler(Looper.getMainLooper());
        context = this;

        final SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMyNotes = new Intent(AddNoteActivity.this, MyNotesActivity.class);
                startActivity(toMyNotes);
                finish();

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleET.getText().toString();
                String text = textET.getText().toString();

                Log.d(TAG, "add click: " + title + ", " + text);

                final String noteTextToSend = String.format("%s%s%s", title, Note.breakStr, text);

                final OkHttpClient client = new OkHttpClient();

                String url = getString(R.string.baseURL) + "note/post";

                RequestBody body = new FormBody.Builder()
                        .add("text", noteTextToSend)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .header("x-access-token", sharedPref.getString(getString(R.string.authToken), ""))
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                    @Override public void onResponse(Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                            final JSONObject root = new JSONObject(responseBody.string());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if(root.getBoolean("posted")){
                                            Toast.makeText(context, "Note created!", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onResponse: Note made");
                                        } else {
                                            Toast.makeText(context, "Note creation failed, try again", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    Intent toMyNotes = new Intent(AddNoteActivity.this, MyNotesActivity.class);
                                    startActivity(toMyNotes);
                                    finish();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }
}
