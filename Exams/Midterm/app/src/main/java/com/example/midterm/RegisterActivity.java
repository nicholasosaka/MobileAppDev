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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "MIDTERM_REGISTER";
    EditText emailET;
    EditText nameET;
    EditText passET;
    EditText repassET;
    Button registerBtn;
    Button cancelBtn;

    Handler handler;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");

        emailET = findViewById(R.id.registerEmailET);
        nameET = findViewById(R.id.registerNameET);
        passET = findViewById(R.id.registerPassET);
        repassET = findViewById(R.id.registerRePassET);
        registerBtn = findViewById(R.id.registerButton);
        cancelBtn = findViewById(R.id.registerCancelButton);


        handler = new Handler(Looper.getMainLooper());
        context = this;
        final SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final OkHttpClient client = new OkHttpClient();

                String url = getString(R.string.baseURL) + "auth/register";

                String email = emailET.getText().toString();
                String password = passET.getText().toString();
                String repeatPass = repassET.getText().toString();
                String name = nameET.getText().toString();

                if(password.equals(repeatPass)){
                    RequestBody body = new FormBody.Builder()
                            .add("email", email)
                            .add("password", password)
                            .add("name", name)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }
                        @Override public void onResponse(Call call, Response response) throws IOException {
                            try (ResponseBody responseBody = response.body()) {
                                if (response.isSuccessful()) {
                                    JSONObject root = new JSONObject(responseBody.string());
                                    String token = root.getString("token");
                                    Log.d(TAG, "onResponse: token: " + token);

                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString(getString(R.string.authToken), token);
                                    editor.commit();

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent toMyNotes = new Intent(RegisterActivity.this, MyNotesActivity.class);
                                            Toast.makeText(context, "Logging in...", Toast.LENGTH_SHORT).show();
                                            startActivity(toMyNotes);
                                            finish();

                                        }
                                    });

                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "Registration unsuccessful, try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(toLogin);
                Log.d(TAG, "onClick: sending to login activity");
                finish();
            }
        });
    }
}
