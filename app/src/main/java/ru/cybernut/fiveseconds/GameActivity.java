package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends SingleFragmentFullScreenActivity {

    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";

    private int numberOfQuestions;

    public static Intent newIntent(Context context, int numberOfQuestions) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        numberOfQuestions = getIntent().getExtras().getInt(NUMBER_OF_QUESTIONS_KEY);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return GameFragment.newInstance(numberOfQuestions);
    }
}
