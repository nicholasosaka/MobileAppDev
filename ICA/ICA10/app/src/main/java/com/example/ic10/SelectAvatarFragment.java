package com.example.ic10;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class SelectAvatarFragment extends Fragment {

    private static final String TAG = "ICA10-SAF";
    private ImageView iv_male;
    private ImageView iv_female;
    ToEditFromSelect toEditFromSelect;

    public SelectAvatarFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toEditFromSelect = (ToEditFromSelect) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_avatar, container, false);

        iv_male = view.findViewById(R.id.iv_male);
        iv_female = view.findViewById(R.id.iv_female);

        iv_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: setting female");
                toEditFromSelect.updateEdit(true);
            }
        });

        iv_male.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: setting male");
                toEditFromSelect.updateEdit(false);
            }
        });

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

    public interface ToEditFromSelect{
        void updateEdit(boolean isFemale);
    }
}
