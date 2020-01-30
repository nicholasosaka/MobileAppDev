package com.example.ic03;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class SelectAvatar extends AppCompatActivity implements View.OnClickListener {

    public static final String GENDER = "gender";
    ImageView iv_male, iv_female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_avatar);

        setTitle("Select Avatar");

        iv_female = findViewById(R.id.iv_female);
        iv_male = findViewById(R.id.iv_male);

        iv_female.setOnClickListener(this);
        iv_male.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent returnData = new Intent();

        if(v == iv_female){
            returnData.putExtra(GENDER, "female");


        } else if (v == iv_male){

            returnData.putExtra(GENDER, "male");
        }

        setResult(RESULT_OK, returnData);

        finish();
    }
}
