package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

import ru.cybernut.fiveseconds.view.GameViewModel;
import ru.cybernut.fiveseconds.view.OnBackPressedListener;

public class GameActivity extends SingleFragmentFullScreenActivity {

    private static final String TAG = "GameActivity";
    private static final String NUMBER_OF_ROUNDS_KEY = "NUMBER_OF_ROUNDS_KEY";
    private static final String QUESTION_SET_IDS_KEY = "QUESTION_SET_IDS_KEY";
    private static final String GAME_TYPE_KEY = "GAME_TYPE_KEY";

    private GameViewModel viewModel;

    public static Intent newIntent(Context context, int numberOfRounds, ArrayList<Integer> setsIds, int gameType) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(NUMBER_OF_ROUNDS_KEY, numberOfRounds);
        intent.putExtra(GAME_TYPE_KEY, gameType);
        intent.putIntegerArrayListExtra(QUESTION_SET_IDS_KEY, setsIds);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Bundle intentData = getIntent().getExtras();
        GameFragment gameFragment = GameFragment.newInstance(intentData.getInt(NUMBER_OF_ROUNDS_KEY), intentData.getIntegerArrayList(QUESTION_SET_IDS_KEY), intentData.getInt(GAME_TYPE_KEY));
        return gameFragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel = null;
    }

    @Override
    public void onBackPressed() {
        handleBackOrLeave();
    }
    @Override
    protected void onUserLeaveHint() {
        handleBackOrLeave();
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
}
