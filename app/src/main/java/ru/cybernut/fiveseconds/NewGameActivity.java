package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;

import ru.cybernut.fiveseconds.view.AlertDialogFragment;

public class NewGameActivity extends SingleFragmentFullScreenActivity  implements NewGameSettingsFragment.OnGamePreparedListener, AlertDialogFragment.NoticeDialogListener {

    private static final String GAME_SETTINGS_FRAGMENT = "GAME_SETTINGS_FRAGMENT";
    private NewGameAddPlayersFragment newGameAddPlayersFragment;

    @Override
    protected Fragment createFragment() {
        newGameAddPlayersFragment = NewGameAddPlayersFragment.newInstance();
        return newGameAddPlayersFragment;
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, NewGameActivity.class);
    }

    public void onNextGameSettingsButtonClick(View view) {
        newGameAddPlayersFragment.saveSettings();
        replaceFragment(NewGameSettingsFragment.newInstance(), GAME_SETTINGS_FRAGMENT);
    }

    @Override
    public void onGamePrepared(int numberOfQuestions, ArrayList<Integer> sets, int gameType) {
        Intent intent = GameActivity.newIntent(this, numberOfQuestions, sets, gameType);
        startActivity(intent);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
