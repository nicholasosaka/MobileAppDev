package com.example.ic07;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class Trivia extends AppCompatActivity implements QuestionAdapter.InteractWithTriviaActivity {
    public static final String CORRECT_NUM_KEY = "CORRECT_NUM_KEY";
    public static final int STAT_ACTIVITY = 0;
    private static final String TAG = "IC07-TRIVIA";
    int currentQuestion = 0;
    int correctQuestions = 0;

    ArrayList<Question> questions;

    ImageView image;
    ProgressBar progressBar;

    TextView questionNumber;
    TextView timer;
    TextView questionText;

    //RecyclerView rvQuestions;

    Intent toStats;
    RecyclerView recyclerView;

    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        toStats = new Intent(Trivia.this, Stats.class);

        setTitle("Trivia Questions");
        Intent intent = getIntent();

        image = findViewById(R.id.image);
        progressBar = findViewById(R.id.image_load_progress);
        questionNumber = findViewById(R.id.question_number);
        timer = findViewById(R.id.timer_label);
        questionText = findViewById(R.id.question_text);

        questions = (ArrayList<Question>) intent.getSerializableExtra(MainActivity.QUESTIONS_KEY);

        Log.d(TAG, "Questions: " + Arrays.toString(questions.toArray()));


        populateQuestion();

        //For a two minutes count down:
        new CountDownTimer(2*60*1000, 1000) {
            @Override
            public void onTick(long l) {
                long minute = l/1000/60;
                long second = (l - minute*60*1000)/1000;
                //Log.d(TAG, "onTick: "+ minute+ " "+second);
                second += minute * 60;

                if(second != 1){
                    timer.setText("Time Left: " + second + " seconds");
                } else {
                    timer.setText("Time Left: " + second + " second");
                }
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "FINISHED");

                toStats.putExtra(CORRECT_NUM_KEY, correctQuestions);

            }
        }.start();

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentQuestion < questions.size()-2){
                    currentQuestion++;
                    populateQuestion();
                } else {
                    toStats.putExtra(CORRECT_NUM_KEY, correctQuestions);
                    startActivityForResult(toStats, STAT_ACTIVITY);
                }
            }
        });

        findViewById(R.id.quit_button_questions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "ACTIVITY RESULT: " + requestCode + " RESULT: " + requestCode);
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == STAT_ACTIVITY && resultCode == Stats.QUIT_TO_MAIN){
            finish();
        }

        if(requestCode == STAT_ACTIVITY && resultCode == Stats.RESTART){
            currentQuestion = 0;
            correctQuestions = 0;
            populateQuestion();
        }
    }

    private void populateQuestion() {
        Question q = questions.get(currentQuestion);

        String questionNumText = "Q" + (q.getId()+1);
        questionNumber.setText(questionNumText);

        Log.d(TAG, "Getting image from " + q.getImageURL());

        progressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(q.getImageURL()).into(image);

        progressBar.setVisibility(View.INVISIBLE);

        questionText.setText(q.getText());

        recyclerView = findViewById(R.id.recyclerView);

        rv_layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rv_layoutManager);

        rv_adapter = new QuestionAdapter(questions.get(currentQuestion).getChoices(), this);
        recyclerView.setAdapter(rv_adapter);


    }

    @Override
    public void selectAnswer(int selection) {
        Question q = questions.get(currentQuestion);
        if(q.getCorrectOption()-1 == selection){
            correctQuestions++;
            Log.d(TAG, "CORRECT, # correct is now " + correctQuestions);
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "INCORRECT");
        }

        if(currentQuestion < questions.size()-2){
            currentQuestion++;
            populateQuestion();
        } else {
            toStats.putExtra(CORRECT_NUM_KEY, correctQuestions);
            startActivityForResult(toStats, STAT_ACTIVITY);
        }
    }


    class GetImage extends AsyncTask<String, Void, Void> {

        ImageView iv;
        Bitmap bitmap = null;

        public GetImage(ImageView iv) {
            this.iv = iv;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {

            if(strings[0].length() == 0){
                return null;
            }

            try {
                URL url = new URL(strings[0]);
                Log.d("demo", url+"");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.d(TAG, "HTTP OK");
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }

            } catch (IOException e) {
                Log.d(TAG, "Exception!");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            iv.setImageBitmap(bitmap);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }


}
