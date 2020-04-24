package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class RulesActivity extends SingleFragmentFullScreenActivity {

    @Override
    protected Fragment createFragment() {
        return RulesFragment.newInstance();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, RulesActivity.class);
    }


}
