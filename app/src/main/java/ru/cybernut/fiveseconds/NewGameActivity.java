package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class NewGameActivity extends SingleFragmentFullScreenActivity  implements NewGameSettingsFragment.OnGamePreparedListener {

    private static final String GAME_SETTINGS_FRAGMENT = "GAME_SETTINGS_FRAGMENT";
    private NewGameAddPlayersFragment newGameAddPlayersFragment;

    @Override
    protected Fragment createFragment() {
        newGameAddPlayersFragment = NewGameAddPlayersFragment.newInstance();
        return newGameAddPlayersFragment;
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NewGameActivity.class);
        return intent;
    }

    public void onNextGameSettingsButtonClick(View view) {
        newGameAddPlayersFragment.saveSettings();
        replaceFragment(NewGameSettingsFragment.newInstance(), GAME_SETTINGS_FRAGMENT);
    }

    @Override
    public void onGamePrepared(int numberOfQuestions, ArrayList<Integer> sets) {
        Toast.makeText(this, "Number of questions :" + numberOfQuestions, Toast.LENGTH_SHORT).show();
        //Intent intent = MainGameActivity.newIntent(this, numberOfQuestions, sets);
        Intent intent = GameActivity.newIntent(this, numberOfQuestions, sets);
        startActivity(intent);
    }
}
