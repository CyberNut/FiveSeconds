package ru.cybernut.fiveseconds;


import androidx.fragment.app.Fragment;

public class StartActivity extends SingleFragmentFullScreenActivity {

    @Override
    protected Fragment createFragment() {
        return StartFragment.newInstance();
    }

}
