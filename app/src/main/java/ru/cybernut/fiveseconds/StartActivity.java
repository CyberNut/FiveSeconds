package ru.cybernut.fiveseconds;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

public class StartActivity extends SingleFragmentFullScreenActivity {

    @Override
    protected Fragment createFragment() {
        return StartFragment.newInstance();
    }

    public void onPlayButtonClick(View view) {
        Intent intent = NewGameActivity.newIntent(this);
        startActivity(intent);
    }
}
