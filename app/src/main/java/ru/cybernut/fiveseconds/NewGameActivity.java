package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class NewGameActivity extends SingleFragmentFullScreenActivity {
    @Override
    protected Fragment createFragment() {
        return NewGameFragment.newInstance();
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NewGameActivity.class);
        return intent;
    }
}
