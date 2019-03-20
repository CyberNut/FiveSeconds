package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.databinding.GameActivity4playersBinding;
import ru.cybernut.fiveseconds.databinding.GameActivity6playersBinding;
import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.view.GameViewModel;
import ru.cybernut.fiveseconds.view.PlayerModel;

public class GameActivity extends AppCompatActivity implements GameViewModel.GameOverable {

    private static final String TAG = "GameActivity";
    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";
    private static final String QUESTION_SET_IDS_KEY = "QUESTION_SET_IDS_KEY";
    private static final String GAME_TYPE_KEY = "GAME_TYPE_KEY";

    private GameViewModel viewModel;
    private int numberOfPlayers;

    public static Intent newIntent(Context context, int numberOfQuestions, ArrayList<Integer> setsIds, String gameType) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        intent.putExtra(GAME_TYPE_KEY, gameType);
        intent.putIntegerArrayListExtra(QUESTION_SET_IDS_KEY, setsIds);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int numberOfQuestions = getIntent().getExtras().getInt(NUMBER_OF_QUESTIONS_KEY);
        String gameType = getIntent().getExtras().getString(GAME_TYPE_KEY);
        ArrayList<Integer> setIds = getIntent().getExtras().getIntegerArrayList(QUESTION_SET_IDS_KEY);
        super.onCreate(savedInstanceState);

        viewModel = new GameViewModel(getApplicationContext(), numberOfQuestions, setIds);
        viewModel.initialize(this);
        numberOfPlayers = viewModel.getNumberOfPlayers();
        if( numberOfPlayers <= 4) {
            GameActivity4playersBinding binding = DataBindingUtil.setContentView(this, R.layout.game_activity_4players);
            binding.setViewModel(viewModel);
        } else {
            GameActivity6playersBinding binding = DataBindingUtil.setContentView(this, R.layout.game_activity_6players);
            binding.setViewModel(viewModel);
        }
    }

    @Override
    public void gameOver(ArrayList<PlayerModel> playerModelList) {
        Intent intent = GameOverActivity.newIntent(this, playerModelList);
        startActivity(intent);
    }
}
