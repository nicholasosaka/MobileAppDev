package com.example.ic12;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private static final String TAG = "IC12-LN";

    private EditText emailET;
    private EditText passwordET;
    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null) {
            Log.d(TAG, "onStart: currentUser: " + currentUser.getEmail());
            Log.d(TAG, "onStart: routing to contacts list");
            Intent toContactsList = new Intent(Login.this, ContactsList.class);
            startActivity(toContactsList);
            finish();
        } else {
            Log.d(TAG, "onStart: currentUser is null");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setTitle("Login");
        Log.d(TAG, "onCreate: Login");

        //firebase auth
        auth = FirebaseAuth.getInstance();

        //views
        emailET = findViewById(R.id.loginEmail);
        passwordET = findViewById(R.id.loginPassword);
        Button login = findViewById(R.id.loginLogin);
        Button signUp = findViewById(R.id.loginSignUp);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDataValid()){
                    auth.signInWithEmailAndPassword(getEmail(), getPassword())
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //login success
                                        Log.d(TAG, "Login success: routing to Contacts List");

                                        Toast.makeText(Login.this, "Logged in", Toast.LENGTH_SHORT).show();

                                        Intent toContactsList = new Intent(Login.this, ContactsList.class);
                                        startActivity(toContactsList);
                                        finish();

                                    } else {
                                        //login fail
                                        Log.d(TAG, "Login fail");

                                        Toast.makeText(Login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(Login.this, "Invalid data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSignUp = new Intent(Login.this, SignUp.class);
                startActivity(toSignUp);
                finish();
            }
        });
    }


    private boolean isDataValid() {
        boolean isValid = true;

        String regex = "^(.+)@(.+)\\.(.+)$"; //REGEX: XXX@YYY.ZZZ
        Pattern pattern = Pattern.compile(regex);
        if(getEmail().length() == 0 || !pattern.matcher(getEmail()).matches()){
            isValid = false;
            emailET.setError("Enter a valid email address.");
        }

        if(getPassword().length() == 0){
            isValid = false;
            passwordET.setError("Enter a password");
        }

        return isValid;
    }

    private String getEmail(){
        return emailET.getText().toString().trim();
    }

    private String getPassword(){
        return passwordET.getText().toString().trim();
    }


}
