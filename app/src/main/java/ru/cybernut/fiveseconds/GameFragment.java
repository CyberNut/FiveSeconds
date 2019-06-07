package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.cybernut.fiveseconds.databinding.GameActivity2playersBinding;
import ru.cybernut.fiveseconds.databinding.GameActivity4playersBinding;
import ru.cybernut.fiveseconds.databinding.GameActivity6playersBinding;
import ru.cybernut.fiveseconds.databinding.GameOverActivityBinding;
import ru.cybernut.fiveseconds.databinding.PlayersScoresListItemBinding;
import ru.cybernut.fiveseconds.view.GameViewModel;
import ru.cybernut.fiveseconds.view.PlayerModel;

public class GameFragment extends Fragment implements GameViewModel.GameOverable {

    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";
    private static final String QUESTION_SET_IDS_KEY = "QUESTION_SET_IDS_KEY";
    private static final String GAME_TYPE_KEY = "GAME_TYPE_KEY";

    private GameViewModel viewModel;

    public static GameFragment newInstance(int numberOfQuestions, ArrayList<Integer> setsIds, int gameType) {

        Bundle args = new Bundle();
        args.putInt(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        args.putInt(GAME_TYPE_KEY, gameType);
        args.putIntegerArrayList(QUESTION_SET_IDS_KEY, setsIds);

        GameFragment gameFragment = new GameFragment();
        gameFragment.setArguments(args);
        return gameFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        int numberOfQuestions = getArguments().getInt(NUMBER_OF_QUESTIONS_KEY);
        int gameType = getArguments().getInt(GAME_TYPE_KEY);
        ArrayList<Integer> setIds = getArguments().getIntegerArrayList(QUESTION_SET_IDS_KEY);

        viewModel = new GameViewModel(gameType, numberOfQuestions, setIds);
        //viewModel.initialize(this);
        int numberOfPlayers = viewModel.getNumberOfPlayers();
        if( numberOfPlayers == 2) {
            GameActivity2playersBinding binding = DataBindingUtil.inflate(inflater, R.layout.game_activity_2players, container, false);
            binding.setViewModel(viewModel);
            return binding.getRoot();
        }
        else if( numberOfPlayers <= 4) {
            GameActivity4playersBinding binding = DataBindingUtil.inflate(inflater, R.layout.game_activity_4players, container, false);
            binding.setViewModel(viewModel);
            return binding.getRoot();
        } else {
            GameActivity6playersBinding binding = DataBindingUtil.inflate(inflater, R.layout.game_activity_6players, container, false);
            binding.setViewModel(viewModel);
            return binding.getRoot();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.initialize(this);
    }

    @Override
    public void gameOver(ArrayList<PlayerModel> playerModelList) {
        Intent intent = GameOverActivity.newIntent(getActivity(), playerModelList);
        startActivity(intent);
    }
}