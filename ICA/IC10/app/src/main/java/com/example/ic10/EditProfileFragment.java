package com.example.ic10;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class EditProfileFragment extends Fragment{

    private static final String TAG = "ICA10-EPF";

    private EditText et_first_name;
    private EditText et_last_name;
    private Button button_save;
    private ImageView iv_avatar;

    ToAvatarFromEdit avatarContext;
    ToDisplayFromEdit displayContext;

    private String gender = "";
    private Context ctx;


    public EditProfileFragment() {
        // Required empty public constructor
    }


    public void setGender(String gender){
        this.gender = gender;
    }

    @Override
    public void onResume() {
        super.onResume();

        iv_avatar = getView().findViewById(R.id.iv_edit_avatar);

        if (this.gender.equals("male")) {
            iv_avatar.setImageResource(R.drawable.male);
        } else if (this.gender.equals("female")){
            iv_avatar.setImageResource(R.drawable.female);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        avatarContext = (ToAvatarFromEdit) getActivity();
        displayContext = (ToDisplayFromEdit) getActivity();
        ctx = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        button_save = view.findViewById(R.id.button_save);
        iv_avatar = view.findViewById(R.id.iv_edit_avatar);
        et_first_name = view.findViewById(R.id.et_edit_first_name);
        et_last_name = view.findViewById(R.id.et_edit_last_name);

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarContext.startSelectAvatar();
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first = et_first_name.getText().toString();
                String last = et_last_name.getText().toString();
                String gender = getGender();

                if(first.length() == 0 || last.length() == 0) {
                    Toast.makeText(ctx, "Please enter a valid name", Toast.LENGTH_SHORT).show();
                } else if (gender.length() == 0){
                    Toast.makeText(ctx, "Please select a profile picture", Toast.LENGTH_SHORT).show();
                } else {
                    displayContext.startDisplay(first, last, gender);
                }

            }
        });

        return view;
    }

    public String getGender() {
        return gender;
    }

    public interface ToAvatarFromEdit{
        void startSelectAvatar();
    }

    public interface ToDisplayFromEdit{
        void startDisplay(String first, String last, String gender);
    }

}
