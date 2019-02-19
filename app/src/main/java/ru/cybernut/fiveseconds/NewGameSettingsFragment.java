package ru.cybernut.fiveseconds;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class NewGameSettingsFragment extends Fragment {

    public static NewGameSettingsFragment newInstance() {
        return new NewGameSettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_game_settings_fragment, container, false);
    }

}
