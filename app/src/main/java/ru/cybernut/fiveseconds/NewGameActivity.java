package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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
    public void onGamePrepared(int numberOfRounds, ArrayList<Integer> sets, int gameType) {
        Intent intent = GameActivity.newIntent(this, numberOfRounds, sets, gameType);
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
