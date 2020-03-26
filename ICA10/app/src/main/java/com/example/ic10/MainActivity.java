package com.example.ic10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements EditProfileFragment.ToAvatarFromEdit, EditProfileFragment.ToDisplayFromEdit, SelectAvatarFragment.ToEditFromSelect, DisplayProfileFragment.ToEditFromDisplay {
    public static final String EDIT_PROFILE = "EDIT_PROFILE_TAG";
    public static final String TAG = "ICA10-MA";
    public static final String SELECT_AVATAR = "SELECT_AVATAR_TAG";
    private static final String DISPLAY_PROFILE = "DISPLAY_PROFILE_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new EditProfileFragment(), EDIT_PROFILE)
                .commit();


    }

    @Override
    public void startSelectAvatar() {
        Log.d(TAG, "startSelectAvatar");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SelectAvatarFragment(), SELECT_AVATAR)
                .addToBackStack(SELECT_AVATAR)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void updateEdit(boolean isFemale) {
        getSupportFragmentManager().popBackStack();
        EditProfileFragment newEditFragment = (EditProfileFragment) getSupportFragmentManager().findFragmentByTag(EDIT_PROFILE);

        assert newEditFragment != null;
        newEditFragment.setGender(isFemale ? "female" : "male");

    }

    @Override
    public void startDisplay(String first, String last, String gender) {
        Log.d(TAG, "startDisplay");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new DisplayProfileFragment(first, last, gender), DISPLAY_PROFILE)
                .addToBackStack(DISPLAY_PROFILE)
                .commit();
    }

    @Override
    public void toEdit() {
        Log.d(TAG, "toEdit");
        getSupportFragmentManager().popBackStack();
    }

}
