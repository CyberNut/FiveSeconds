package ru.cybernut.fiveseconds;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends SingleFragmentFullScreenActivity {

    @Override
    protected Fragment createFragment() {
        return GameFragment.newInstance();
    }
}
