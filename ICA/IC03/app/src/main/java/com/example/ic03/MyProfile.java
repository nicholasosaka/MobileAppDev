package com.example.ic03;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MyProfile extends AppCompatActivity {

    public static final String USER_KEY = "User";
    private ImageView iv_avi;
    private Button btn_save;
    private EditText et_fname;
    private EditText et_lname;

    private static final int AVATAR_SELECT_REQ_CODE = 1;
    private static final int SAVE_REQ_CODE = 2;


    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        setTitle("My Profile");

        et_fname = findViewById(R.id.et_first_name);
        et_lname = findViewById(R.id.et_last_name);

        user = new User();

        iv_avi = findViewById(R.id.iv_avi);

        iv_avi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSelectAvatar = new Intent(MyProfile.this, SelectAvatar.class);

                startActivityForResult(toSelectAvatar, AVATAR_SELECT_REQ_CODE);
            }
        });

        btn_save = findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = et_fname.getText().toString();
                String lname = et_lname.getText().toString();

                boolean errors = false;

                user.setFname(fname);
                user.setLname(lname);

                if(user.getFname() == null || user.getFname().length() == 0){
                    et_fname.setError("Please enter a first name!");
                    errors = true;
                }

                if (user.getLname() == null || user.getLname().length() == 0){
                    et_lname.setError("Please enter a last name!");
                    errors = true;
                }

                if (user.getGender() == null || user.getGender().length() == 0){
                    errors = true;

                    Toast.makeText(MyProfile.this, "Please select an avatar", Toast.LENGTH_SHORT).show();
                }


                if(!errors) {
                    Intent toSaveUser = new Intent(MyProfile.this, DisplayProfile.class);

                    toSaveUser.putExtra(USER_KEY, user);

                    startActivityForResult(toSaveUser, SAVE_REQ_CODE);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AVATAR_SELECT_REQ_CODE && data != null) {
            String gender = data.getExtras().getString(SelectAvatar.GENDER);

            Log.i("AVATAR", "Result Received: " + gender);

            if (gender.equals("female")){
                iv_avi.setImageDrawable(getDrawable(R.drawable.female));
                user.setGender("Female");
            } else {
                iv_avi.setImageDrawable(getDrawable(R.drawable.male));
                user.setGender("Male");
            }

        }
    }
}
