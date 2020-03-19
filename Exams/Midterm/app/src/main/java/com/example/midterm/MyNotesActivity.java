package com.example.midterm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class MyNotesActivity extends AppCompatActivity {

    ImageView logout;
    ImageView add;
    TextView nameTV;
    RecyclerView recyclerView;

    Context context;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notes);
        setTitle("My Notes");
    }
}
