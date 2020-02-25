package com.example.hw02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String TAG = "HW02";

    Button searchButton;
    Button resetButton;

    EditText searchBar;
    SeekBar seekBar;

    TextView limitLabel;

    Switch toggle;

    int seekBarValue;

    LinearLayout resultContainer;

    String baseURL = "https://itunes.apple.com/search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultContainer = findViewById(R.id.linear_layout);

        searchButton = findViewById(R.id.search_button);
        resetButton = findViewById(R.id.reset_button);

        searchBar = findViewById(R.id.search_bar);
        seekBar = findViewById(R.id.seekBar);
        seekBarValue = 0;

        limitLabel = findViewById(R.id.limit_label);

        toggle = findViewById(R.id.sort_toggle);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue = progress + 10;
                Log.d(TAG, "PROGRESS: " + seekBarValue);
                String labelText = "Limit: " + seekBarValue;
                limitLabel.setText(labelText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarValue = 0;
                seekBar.setProgress(0);
                limitLabel.setText(R.string.seek_bar_tv_default);
                searchBar.setText("");
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
