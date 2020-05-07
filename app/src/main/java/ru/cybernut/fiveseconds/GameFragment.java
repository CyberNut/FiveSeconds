package ru.cybernut.fiveseconds;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import ru.cybernut.fiveseconds.databinding.GameActivity2playersBinding;
import ru.cybernut.fiveseconds.databinding.GameActivity4playersBinding;
import ru.cybernut.fiveseconds.databinding.GameActivity6playersBinding;
import ru.cybernut.fiveseconds.view.GameViewModel;
import ru.cybernut.fiveseconds.view.OnBackPressedListener;
import ru.cybernut.fiveseconds.view.PlayerModel;

public class GameFragment extends Fragment implements GameViewModel.GameOverable, OnBackPressedListener {

    private static final String NUMBER_OF_ROUNDS_KEY = "NUMBER_OF_ROUNDS_KEY";
    private static final String QUESTION_SET_IDS_KEY = "QUESTION_SET_IDS_KEY";
    private static final String GAME_TYPE_KEY = "GAME_TYPE_KEY";

    private FirebaseAnalytics mFirebaseAnalytics;

    private GameViewModel viewModel;
    private boolean isQuitDialogVisible = false;

    public static GameFragment newInstance(int numberOfRounds, ArrayList<Integer> setsIds, int gameType) {

        Bundle args = new Bundle();
        args.putInt(NUMBER_OF_ROUNDS_KEY, numberOfRounds);
        args.putInt(GAME_TYPE_KEY, gameType);
        args.putIntegerArrayList(QUESTION_SET_IDS_KEY, setsIds);

        GameFragment gameFragment = new GameFragment();
        gameFragment.setArguments(args);
        return gameFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        int numberOfRounds = getArguments().getInt(NUMBER_OF_ROUNDS_KEY);
        int gameType = getArguments().getInt(GAME_TYPE_KEY);
        ArrayList<Integer> setIds = getArguments().getIntegerArrayList(QUESTION_SET_IDS_KEY);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FiveSecondsApplication.getAppContext());
        boolean isNeedPlaySound = sharedPreferences.getBoolean(FiveSecondsApplication.PREF_PLAY_SOUND_AFTER_TIMER_ENDS, FiveSecondsApplication.DEFAULT_IS_NEED_PLAY_SOUND_AFTER_TIMER_ENDS);

        viewModel = new GameViewModel(gameType, numberOfRounds, setIds, isNeedPlaySound);
        int numberOfPlayers = viewModel.getNumberOfPlayers();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        if ( mFirebaseAnalytics!= null) {
            Bundle eventParams = new Bundle();
            eventParams.putInt("number_of_players", numberOfPlayers);
            eventParams.putInt("number_of_rounds", numberOfRounds);
            mFirebaseAnalytics.logEvent("start_game", eventParams);
        }

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
        if(viewModel!= null) {
            viewModel.cleanViewModel();
        }
        Intent intent = GameOverActivity.newIntent(getActivity(), playerModelList);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        viewModel.pauseGame();
        if(!viewModel.isGameOver()) {
            openQuitDialog();
        }
    }

    private void openQuitDialog() {

        if(isQuitDialogVisible) {
            return;
        }

        AlertDialog.Builder quitDialog = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        quitDialog.setTitle(R.string.quit_dialog_title);

        quitDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                viewModel.cleanViewModel();
                Intent intent = new Intent(getActivity(), StartActivity.class);
                getActivity().finish();
                startActivity(intent);
                isQuitDialogVisible = false;
            }
        });

        quitDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.resumeGame();
                isQuitDialogVisible = false;
            }
        });
        isQuitDialogVisible = true;
        quitDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                viewModel.resumeGame();
                isQuitDialogVisible = false;
            }
        });
        quitDialog.show();
    }
}