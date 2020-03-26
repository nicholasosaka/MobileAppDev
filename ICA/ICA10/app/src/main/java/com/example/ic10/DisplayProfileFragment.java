package com.example.ic10;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class DisplayProfileFragment extends Fragment {

    private static final String TAG = "ICA10-DPF";
    ToEditFromDisplay toEditFromDisplay;

    private TextView tv_name;
    private TextView tv_gender;
    private ImageView iv_profile;
    private String firstName;
    private String lastName;
    private String gender;


    public DisplayProfileFragment(String firstName, String lastName, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toEditFromDisplay = (ToEditFromDisplay) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_profile, container, false);

        tv_name = view.findViewById(R.id.tv_name);
        tv_gender = view.findViewById(R.id.tv_gender);
        iv_profile = view.findViewById(R.id.iv_profile);

        view.findViewById(R.id.button_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: toEdit");
                toEditFromDisplay.toEdit();
            }
        });

        tv_name.setText(String.format("Name: %s %s", firstName, lastName));
        tv_gender.setText(gender);

        if (this.gender.equals("male")) {
            iv_profile.setImageResource(R.drawable.male);
        } else if (this.gender.equals("female")){
            iv_profile.setImageResource(R.drawable.female);
        }


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public interface ToEditFromDisplay{
        void toEdit();
    }
}
