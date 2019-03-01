package ru.cybernut.fiveseconds;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import ru.cybernut.fiveseconds.model.Question;
import ru.cybernut.fiveseconds.model.QuestionSet;

public class StartActivity extends SingleFragmentFullScreenActivity {

    @Override
    protected Fragment createFragment() {
        return StartFragment.newInstance();
    }

}
