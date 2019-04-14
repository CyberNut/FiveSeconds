package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RulesActivity extends SingleFragmentFullScreenActivity {

    @Override
    protected Fragment createFragment() {
        return RulesFragment.newInstance();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, RulesActivity.class);
    }


}
