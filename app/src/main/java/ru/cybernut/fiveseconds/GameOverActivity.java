package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

import ru.cybernut.fiveseconds.view.OnBackPressedListener;
import ru.cybernut.fiveseconds.view.PlayerModel;

public class GameOverActivity extends SingleFragmentFullScreenActivity {

    private static final String TAG = "GameOverActivity";
    private static final String EXTRA_WINNERS_LIST = "EXTRA_WINNERS_LIST";
    private static final int NUMBER_OF_PLAYERS_LIST_COLUMNS = 2;

    private GameOverFragment gameOverFragment;

    @Override
    protected Fragment createFragment() {
        ArrayList<PlayerModel> playerModels = (ArrayList<PlayerModel>) getIntent().getExtras().getSerializable(EXTRA_WINNERS_LIST);
        gameOverFragment = GameOverFragment.newInstance(playerModels);
        return gameOverFragment;
    }

    public static Intent newIntent(Context context, ArrayList<PlayerModel> playerModels) {
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra(EXTRA_WINNERS_LIST, playerModels);
        return intent;
    }

    private void handleBackOrLeave() {
        FragmentManager fm = getSupportFragmentManager();
        OnBackPressedListener backPressedListener = null;
        for (Fragment fragment: fm.getFragments()) {
            if (fragment instanceof OnBackPressedListener) {
                backPressedListener = (OnBackPressedListener) fragment;
                break;
            }
        }

        if (backPressedListener != null) {
            backPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        handleBackOrLeave();
    }
}
