package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import ru.cybernut.fiveseconds.databinding.GameActivity4playersBinding;
import ru.cybernut.fiveseconds.databinding.GameActivity6playersBinding;
import ru.cybernut.fiveseconds.view.GameViewModel;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";
    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";
    private static final String QUESTION_SET_IDS_KEY = "QUESTION_SET_IDS_KEY";
    private static final int ID_START_VALUE = 770070;

    private GameViewModel viewModel;
    private int numberOfPlayers;


    public static Intent newIntent(Context context, int numberOfQuestions, ArrayList<Integer> setsIds) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        intent.putIntegerArrayListExtra(QUESTION_SET_IDS_KEY, setsIds);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int numberOfQuestions = getIntent().getExtras().getInt(NUMBER_OF_QUESTIONS_KEY);
        ArrayList<Integer> setIds = getIntent().getExtras().getIntegerArrayList(QUESTION_SET_IDS_KEY);
        super.onCreate(savedInstanceState);

        viewModel = new GameViewModel(numberOfQuestions, setIds);
        if(viewModel.getNumberOfPlayers() <= 4) {
            GameActivity4playersBinding binding = DataBindingUtil.setContentView(this, R.layout.game_activity_4players);
            binding.setViewModel(viewModel);
        } else {
            GameActivity6playersBinding binding = DataBindingUtil.setContentView(this, R.layout.game_activity_6players);
            binding.setViewModel(viewModel);
        }
    }
}
