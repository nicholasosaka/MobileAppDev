package com.example.ic03;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayProfile extends AppCompatActivity {

    ImageView iv_display_avi;
    TextView gender;
    TextView name;
    Button btn_edit;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);

        setTitle("Display Profile");

        Intent intent = getIntent();

        user = (User) intent.getExtras().getSerializable(MyProfile.USER_KEY);

        Log.i("DisplayProfile", "User: " + user);

        iv_display_avi = findViewById(R.id.iv_display_avi);
        name = findViewById(R.id.tv_name);
        gender = findViewById(R.id.tv_gender);

        if(user.getGender().equals("Female")){
            iv_display_avi.setImageDrawable(getDrawable(R.drawable.female));
        } else if (user.getGender().equals("Male")){
            iv_display_avi.setImageDrawable(getDrawable(R.drawable.male));
        }

        String fullName = user.getFname() + " " + user.getLname();
        name.setText(fullName);


        gender.setText(user.getGender());


        findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
