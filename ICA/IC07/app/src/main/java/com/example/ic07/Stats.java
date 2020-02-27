package com.example.ic07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Stats extends AppCompatActivity {


    public static final int QUIT_TO_MAIN = 0;
    public static final int RESTART = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Intent intent = getIntent();

        int correct = intent.getIntExtra(Trivia.CORRECT_NUM_KEY, 0);

        int percentage = (int)((correct/16.0)*100.0);

        ProgressBar correctBar = findViewById(R.id.correct_bar);

        correctBar.setMax(16);
        correctBar.setProgress(correct);

        if(correct == 16){
            TextView tv = findViewById(R.id.try_again_text);
            tv.setText("Congratulations!");
        }

        TextView percentageTextView = findViewById(R.id.correct_percent);
        percentageTextView.setText(percentage + "%");

        findViewById(R.id.stat_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Stats.QUIT_TO_MAIN);
                finish();
            }
        });

        findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                setResult(Stats.RESTART);
                finish();
            }
        });
    }
}
