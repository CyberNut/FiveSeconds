package ru.cybernut.fiveseconds;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return StartFragment.newInstance();
    }

    public void onPlayButtonClick(View view) {
        Intent intent = NewGameActivity.newIntent(this);
        startActivity(intent);
    }
}
