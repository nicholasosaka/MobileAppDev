package com.example.midterm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyNotesActivity extends AppCompatActivity implements NoteAdapter.InteractWithNote{

    private static final String TAG = "MIDTERM_NOTES";
    ImageView logout;
    ImageView add;
    TextView nameTV;
    RecyclerView recyclerView;

    Context context;
    Handler handler;

    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_manager;

    ArrayList<Note> notes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notes);
        setTitle("My Notes");

        Intent intent = getIntent();

        handler = new Handler(Looper.getMainLooper());

        recyclerView = findViewById(R.id.recyclerView);
        nameTV = findViewById(R.id.nameTV);
        logout = findViewById(R.id.logoutIV);
        add = findViewById(R.id.addIV);

        rv_manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rv_manager);
        rv_adapter = new NoteAdapter(this, notes);
        recyclerView.setAdapter(rv_adapter);

        context = this;

        final SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        networkCalls();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAddNote = new Intent(MyNotesActivity.this, AddNoteActivity.class);
                startActivity(toAddNote);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final OkHttpClient client = new OkHttpClient();

                String url = getString(R.string.baseURL) + "auth/logout";

                Request request = new Request.Builder()
                        .url(url)
                        .header("x-access-token", sharedPref.getString(getString(R.string.authToken), ""))
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
                                    Intent toLogin = new Intent(MyNotesActivity.this, LoginActivity.class);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString(getString(R.string.authToken), "");
                                    editor.commit();
                                    startActivity(toLogin);
                                    finish();
                                }
                            });

                        }
                    }
                });
            }
        });


    }

    @Override
    public void selectNote(Note note) {
        Intent toDisplay = new Intent(MyNotesActivity.this, DisplayNoteActivity.class);
        toDisplay.putExtra(DisplayNoteActivity.NOTE_ID, note);
        startActivity(toDisplay);
        finish();
    }

    @Override
    public void deleteNote(final Note note) {

        new AlertDialog.Builder(context)
                .setTitle("Are you sure you want to delete?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notes.remove(note);
                        rv_adapter.notifyDataSetChanged();

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
                                            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });

                    }
                })
                .show();

    }

    public void networkCalls(){

        final SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final OkHttpClient client = new OkHttpClient();

        String url = getString(R.string.baseURL) + "note/getall";

        Request request = new Request.Builder()
                .url(url)
                .header("x-access-token", sharedPref.getString(getString(R.string.authToken), ""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    JSONObject root = new JSONObject(responseBody.string());
                    JSONArray noteJSONArray = root.getJSONArray("notes");

                    for (int i = 0; i < noteJSONArray.length(); i++) {
                        JSONObject noteJSON = noteJSONArray.getJSONObject(i);
                        String id = noteJSON.getString("_id");
                        String userID = noteJSON.getString("userId");
                        String text = noteJSON.getString("text");

                        Note note = new Note(id, userID, text);
                        notes.add(note);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            rv_adapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        url = getString(R.string.baseURL) + "auth/me";

        request = new Request.Builder()
                .url(url)
                .header("x-access-token", sharedPref.getString(getString(R.string.authToken), ""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    JSONObject root = new JSONObject(responseBody.string());
                    final String name = root.getString("name");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            nameTV.setText(name);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
