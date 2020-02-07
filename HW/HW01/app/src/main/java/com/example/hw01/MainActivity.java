package com.example.hw01;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    SeekBar seekBar;
    TextView seekLabel;
    Button button;
    TextView minLabel;
    TextView maxLabel;
    TextView avgLabel;

    ExecutorService threadPool;
    static Handler handler;

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

        threadPool = Executors.newFixedThreadPool(2);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Log.d("HANDLER", "Message Received: " + msg);
                switch(msg.what){
                    case HeavyWork.STATUS_START:
                        Log.d("HANDLER", "Starting...");

                        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                        findViewById(R.id.button).setVisibility(View.INVISIBLE);
                        findViewById(R.id.tv_average_label).setVisibility(View.INVISIBLE);
                        findViewById(R.id.tv_average).setVisibility(View.INVISIBLE);
                        findViewById(R.id.tv_maximum).setVisibility(View.INVISIBLE);
                        findViewById(R.id.tv_maximum_label).setVisibility(View.INVISIBLE);
                        findViewById(R.id.tv_minimum).setVisibility(View.INVISIBLE);
                        findViewById(R.id.tv_minimum_label).setVisibility(View.INVISIBLE);
                        findViewById(R.id.tv_complexity_label).setVisibility(View.INVISIBLE);
                        findViewById(R.id.tv_seek_label).setVisibility(View.INVISIBLE);
                        findViewById(R.id.sb_complexity).setVisibility(View.INVISIBLE);


                        break;
                    case HeavyWork.STATUS_FINISH:
                        Log.d("HANDLER", "Stopping...");

                        //cast message object back to double array
                        Map<String, Double> map = (Map<String, Double>) msg.obj;

                        ((TextView) findViewById(R.id.tv_minimum)).setText(map.get(HeavyWork.MIN) +"");
                        ((TextView) findViewById(R.id.tv_average)).setText(map.get(HeavyWork.AVG) +"");
                        ((TextView) findViewById(R.id.tv_maximum)).setText(map.get(HeavyWork.MAX) +"");
                        findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);


                        findViewById(R.id.button).setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_average_label).setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_average).setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_maximum).setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_maximum_label).setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_minimum).setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_minimum_label).setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_complexity_label).setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_seek_label).setVisibility(View.VISIBLE);
                        findViewById(R.id.sb_complexity).setVisibility(View.VISIBLE);

                        break;
                }

                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seekBarValue = seekBar.getProgress();
                if(seekBarValue != 0){
                    threadPool.execute(new HeavyWork(seekBarValue));
                } else {
                    Toast.makeText(MainActivity.this, "Please select a non zero value", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
