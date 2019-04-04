package ru.cybernut.fiveseconds;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.Locale;

import ru.cybernut.fiveseconds.databinding.StartFragmentBinding;

public class StartFragment extends Fragment {

    private static final String TAG = "StartFragment";
    private static final String MAIN_GAME_SETTINGS_FRAGMENT = "MAIN_GAME_SETTINGS_FRAGMENT";

    private StartFragmentBinding binding;
    private ImageButton newGameButton;
    private ImageButton helpButton;
    private ImageButton settingsButton;

    public static StartFragment newInstance() {
        return new StartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.start_fragment, container, false);
        View v = binding.getRoot();
        prepareUI(v);
        return v;
    }

    private void prepareUI(View view) {
        newGameButton = binding.newGameButton;
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = NewGameActivity.newIntent(getActivity());
                Log.i(TAG, Locale.getDefault().getLanguage());
                startActivity(intent);
            }
        });

        helpButton = binding.helpButton;
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        settingsButton = binding.settingsButton;
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity activity = (StartActivity) getActivity();
                if ( activity!= null) {
                    activity.replaceFragment(new ru.cybernut.fiveseconds.MainGameSettingsFragment(), MAIN_GAME_SETTINGS_FRAGMENT);
                }
            }
        });
    }
}
