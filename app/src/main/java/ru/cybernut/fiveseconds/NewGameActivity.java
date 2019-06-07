package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import ru.cybernut.fiveseconds.view.AlertDialogFragment;

public class NewGameActivity extends SingleFragmentFullScreenActivity  implements NewGameSettingsFragment.OnGamePreparedListener, NewGameAddPlayersFragment.NewGameAddPlayersFragmentListener, AlertDialogFragment.NoticeDialogListener {

    private static final String GAME_SETTINGS_FRAGMENT = "GAME_SETTINGS_FRAGMENT";

    @Override
    protected Fragment createFragment() {
        return NewGameAddPlayersFragment.newInstance();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, NewGameActivity.class);
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

    @Override
    public void playersAdded() {
        replaceFragment(NewGameSettingsFragment.newInstance(), GAME_SETTINGS_FRAGMENT);
    }
}
