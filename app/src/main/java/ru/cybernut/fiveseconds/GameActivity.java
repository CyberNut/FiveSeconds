package ru.cybernut.fiveseconds;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.ArrayList;

import ru.cybernut.fiveseconds.databinding.GameActivityBinding;
import ru.cybernut.fiveseconds.view.GameViewModel;

public class GameActivity extends AppCompatActivity {

    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";
    private static final String QUESTION_SET_IDS_KEY = "QUESTION_SET_IDS_KEY";

    public static Intent newIntent(Context context, int numberOfQuestions, ArrayList<Integer> setsIds) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        intent.putIntegerArrayListExtra(QUESTION_SET_IDS_KEY, setsIds);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.game_activity);
        GameActivityBinding bindings = DataBindingUtil.setContentView(this, R.layout.game_activity);
        //GameActivityBinding bindings = new ViewModelProvider(this).get(GameViewModel.class);

    }

}
