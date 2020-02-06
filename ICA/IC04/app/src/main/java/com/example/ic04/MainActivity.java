package com.example.ic04;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    SeekBar seekBar;
    TextView seekLabel;
    Button button;
    TextView minLabel;
    TextView maxLabel;
    TextView avgLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        seekLabel = findViewById(R.id.tv_seek_label);
        seekBar = findViewById(R.id.sb_complexity);
        button = findViewById(R.id.button);
        minLabel = findViewById(R.id.tv_minimum);
        maxLabel = findViewById(R.id.tv_maximum);
        avgLabel = findViewById(R.id.tv_average);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String str = progress + " Times";
                seekLabel.setText(str);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seekBar.getProgress() != 0){
                    new GetNumbers().execute(seekBar.getProgress());
                } else {
                    Toast.makeText(MainActivity.this, "Please select a non zero value", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    class GetNumbers extends AsyncTask<Integer, Void, ArrayList<Double>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("IC04", "Starting HeavyWork");

            findViewById(R.id.button).setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_minimum_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_minimum).setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_maximum_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_maximum).setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_average_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_average).setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_complexity_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_seek_label).setVisibility(View.INVISIBLE);
            findViewById(R.id.sb_complexity).setVisibility(View.INVISIBLE);


            progressBar.setVisibility(ProgressBar.VISIBLE);
        }


        @Override
        protected ArrayList<Double> doInBackground(Integer... integers) {
            return HeavyWork.getArrayNumbers(integers[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Double> doubles) {
            super.onPostExecute(doubles);
            Log.d("IC04", "Finished HeavyWork");
            progressBar.setVisibility(ProgressBar.INVISIBLE);

            double min = findMin(doubles);
            double max = findMax(doubles);
            double avg = findAvg(doubles);

            minLabel.setText(min+"");
            maxLabel.setText(max+"");
            avgLabel.setText(avg+"");

            findViewById(R.id.button).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_minimum_label).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_minimum).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_maximum_label).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_maximum).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_average_label).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_average).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_complexity_label).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_seek_label).setVisibility(View.VISIBLE);
            findViewById(R.id.sb_complexity).setVisibility(View.VISIBLE);


        }


    }

    private double findAvg(ArrayList<Double> doubles) {
        double avg = 0.0;

        for(double d : doubles){
            avg += d;
        }

        avg /= doubles.size();

        return avg;
    }

    private double findMax(ArrayList<Double> doubles) {
        double max = Double.MIN_VALUE;

        for(double d : doubles){
            if(d > max) max = d;
        }

        return max;
    }

    private double findMin(ArrayList<Double> doubles) {
        double min = Double.MAX_VALUE;

        for(double d : doubles){
            if(d < min) min = d;
        }

        return min;
    }
}
