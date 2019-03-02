package ru.cybernut.fiveseconds;

import android.support.v4.app.Fragment;

public class StartActivity extends SingleFragmentFullScreenActivity {

    @Override
    protected Fragment createFragment() {
        return StartFragment.newInstance();
    }

}
