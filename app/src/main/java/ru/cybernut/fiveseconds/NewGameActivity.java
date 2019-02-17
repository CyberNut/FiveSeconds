package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class NewGameActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return NewGameFragment.newInstance();
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NewGameActivity.class);
        return intent;
    }
}
