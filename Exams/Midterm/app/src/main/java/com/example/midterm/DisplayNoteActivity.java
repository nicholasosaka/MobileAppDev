package com.example.midterm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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

public class DisplayNoteActivity extends AppCompatActivity {

    public static final String NOTE_ID = "NOTE";
    Context context;
    Handler handler;

    TextView title;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);
        setTitle("Display Note");

        handler = new Handler(Looper.getMainLooper());
        context = this;

        final SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final OkHttpClient client = new OkHttpClient();

        Intent intent = getIntent();

        final Note note = (Note) intent.getSerializableExtra(NOTE_ID);

        title = findViewById(R.id.noteTitleTV);
        text = findViewById(R.id.noteTextTV);

        assert note != null;

        text.setText(note.getText());
        title.setText(note.getTitle());

        findViewById(R.id.displayDeleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Are you sure you want to delete?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent toMyNotes = new Intent(DisplayNoteActivity.this, MyNotesActivity.class);
                                startActivity(toMyNotes);
                                finish();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final SharedPreferences sharedPref = context.getSharedPreferences(
                                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);

                                final OkHttpClient client = new OkHttpClient();

                                String url = getString(R.string.baseURL) + "note/delete";

                                RequestBody body = new FormBody.Builder()
                                        .add("id", note.getId())
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

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent toMyNotes = new Intent(DisplayNoteActivity.this, MyNotesActivity.class);
                                                    startActivity(toMyNotes);
                                                    finish();
                                                }
                                            });

                                        }
                                    }
                                });

                            }
                        })
                        .show();

            }
        });

        findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMyNotes = new Intent(DisplayNoteActivity.this, MyNotesActivity.class);
                startActivity(toMyNotes);
                finish();
            }
        });

    }
}
