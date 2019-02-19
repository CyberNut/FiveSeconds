package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

public class NewGameActivity extends SingleFragmentFullScreenActivity {

    private static final String GAME_SETTINGS_FRAGMENT = "GAME_SETTINGS_FRAGMENT";

    @Override
    protected Fragment createFragment() {
        return NewGameAddPlayersFragment.newInstance();
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NewGameActivity.class);
        return intent;
    }

    public void onNextGameSettingsButtonClick(View view) {
        replaceFragment(NewGameSettingsFragment.newInstance(), GAME_SETTINGS_FRAGMENT);
    }
}
