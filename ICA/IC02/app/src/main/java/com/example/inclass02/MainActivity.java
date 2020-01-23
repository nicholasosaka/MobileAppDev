/*
    In Class 02
    MainActivity.java
    Nicholas Osaka
    Team Member: Alexandria Benedict
 */

package com.example.inclass02;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText et_weight;
    private EditText et_height_feet;
    private EditText et_height_inches;

    private TextView tv_verdict;
    private TextView tv_result;

    private double weight;
    private int feet;
    private int inches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weight = 0.0;
        feet = 0;
        inches = 0;

        et_weight = findViewById(R.id.et_weight);
        et_height_feet = findViewById(R.id.et_height_feet);
        et_height_inches = findViewById(R.id.et_height_inches);

        tv_verdict = findViewById(R.id.tv_verdict);
        tv_result = findViewById(R.id.tv_result);

        Button button_calculate = findViewById(R.id.button_calculate);



        button_calculate.setOnClickListener(v -> {
                String weight_text = et_weight.getText().toString();
                String feet_text = et_height_feet.getText().toString();
                String inches_text = et_height_inches.getText().toString();

                boolean errors = false;

                //validating numbers exist
                if(weight_text.equals("")){
                    et_weight.setError("Weight can't be empty");
                    Toast.makeText(MainActivity.this, "Invalid Inputs", Toast.LENGTH_SHORT).show();
                    errors = true;

                } else {
                    weight = Double.parseDouble(weight_text);
                }

                if (feet_text.equals("")){
                    et_height_feet.setError("Feet can't be empty");
                    Toast.makeText(MainActivity.this, "Invalid Inputs", Toast.LENGTH_SHORT).show();
                    errors = true;

                } else {
                    feet = Integer.parseInt(feet_text);
                }

                if (inches_text.equals("")){
                    et_height_inches.setError("Inches can't be empty");
                    Toast.makeText(MainActivity.this, "Invalid Inputs", Toast.LENGTH_SHORT).show();
                    errors = true;

                } else {
                    inches = Integer.parseInt(inches_text);

                    if(inches > 11){
                        et_height_inches.setError("Inches cannot be over 11");
                        errors = true;
                    }
                }



                //BMI = (Weight in Pounds / (Height in inches x Height in inches)) x 703 1 foot = 12 inches
                if(!errors){
                    inches = feet * 12 + inches;

                    double BMI = (weight / (inches * inches)) * 703;

                    tv_result.setText("Your BMI: " + BMI);

                    if(BMI < 18.5){

                        tv_verdict.setText(R.string.underweight_prompt);

                    } else if (BMI >= 18.5 && BMI <= 24.9){

                        tv_verdict.setText(R.string.normal_prompt);

                    } else if (BMI > 24.9 && BMI <= 29.9){

                        tv_verdict.setText(R.string.overweight_prompt);

                    } else if (BMI > 29.9) {

                        tv_verdict.setText(R.string.obese_prompt);

                    }

                    Toast.makeText(MainActivity.this, "BMI Calculated", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "Invalid Inputs", Toast.LENGTH_SHORT).show();
                }


            }
        );
    }

}
