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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.regex.Pattern;


public class SignUp extends AppCompatActivity {

    private static final String TAG = "IC12-SU";

    private EditText firstNameET;
    private EditText lastNameET;
    private EditText emailET;
    private EditText passwordET;
    private EditText passwordConfirmET;

    private Button signUp;
    private Button cancel;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        Intent intent = getIntent();
        Log.d(TAG, "onCreate: SignUp");

        //firebase auth
        auth = FirebaseAuth.getInstance();

        //views
        firstNameET = findViewById(R.id.signUpFirstName);
        lastNameET = findViewById(R.id.signUpLastName);
        emailET = findViewById(R.id.signUpEmail);
        passwordET = findViewById(R.id.signUpPassword);
        passwordConfirmET = findViewById(R.id.signUpPasswordConfirm);

        signUp = findViewById(R.id.signUpButton);
        cancel = findViewById(R.id.signUpCancel);

        //listeners
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDataValid()){
                    auth.createUserWithEmailAndPassword(getEmail(), getPassword())
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Log.d(TAG, "User Registration Successful: " + getEmail() + ", " + getPassword());
                                        Toast.makeText(SignUp.this, "User Created", Toast.LENGTH_SHORT).show();

                                        Log.d(TAG, "Routing to Contacts List from Sign Up");
                                        Intent toContactsList = new Intent(SignUp.this, ContactsList.class);
                                        startActivity(toContactsList);
                                        finish();

                                    } else {
                                        Log.d(TAG, "User Registration Failed: " + getEmail() + ", " + getPassword());
                                        Toast.makeText(SignUp.this, "User Creation Failed", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: User Registration", e);
                                    Toast.makeText(SignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(SignUp.this, "Invalid Inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //cancel should end activity
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(SignUp.this, Login.class);
                startActivity(toLogin);
                finish();
            }
        });
    }

    private boolean isDataValid() {
        //get text as string and trim
        String firstName = firstNameET.getText().toString().trim();
        String lastName = lastNameET.getText().toString().trim();
        String email = getEmail();
        String password = getPassword();
        String passwordConfirm = passwordConfirmET.getText().toString().trim();

        boolean isValid = true;

        //first name validation
        if(firstName.length() == 0){
            isValid = false;
            firstNameET.setError("Enter a first name.");
        }

        //last name validation
        if(lastName.length() == 0){
            isValid = false;
            lastNameET.setError("Enter a last name.");
        }

        //email validation
        String regex = "^(.+)@(.+)\\.(.+)$"; //REGEX: XXX@YYY.ZZZ
        Pattern pattern = Pattern.compile(regex);
        if(!pattern.matcher(email).matches()){
            isValid = false;
            emailET.setError("Enter a valid email address.");
        }

        //password validation
        if(password.length() == 0) {
            isValid = false;
            passwordET.setError("Please enter a password.");
        }

        if(passwordConfirm.length() == 0){
            isValid = false;
            passwordConfirmET.setError("Please enter a password.");
        }

        if(!password.equals(passwordConfirm)){
            isValid = false;
            passwordET.setError("Passwords must match.");
            passwordConfirmET.setError("Passwords must match.");
        }

        Log.d(TAG, "Information Validated.");
        return isValid;
    }

    private String getEmail(){
        return emailET.getText().toString().trim();
    }

    private String getPassword(){
        return passwordET.getText().toString().trim();
    }

}
