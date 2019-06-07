package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import ru.cybernut.fiveseconds.view.GameViewModel;

public class GameActivity extends SingleFragmentFullScreenActivity {

    private static final String TAG = "GameActivity";
    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";
    private static final String QUESTION_SET_IDS_KEY = "QUESTION_SET_IDS_KEY";
    private static final String GAME_TYPE_KEY = "GAME_TYPE_KEY";

    private GameViewModel viewModel;

    public static Intent newIntent(Context context, int numberOfQuestions, ArrayList<Integer> setsIds, int gameType) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        intent.putExtra(GAME_TYPE_KEY, gameType);
        intent.putIntegerArrayListExtra(QUESTION_SET_IDS_KEY, setsIds);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Bundle intentData = getIntent().getExtras();
        GameFragment gameFragment = GameFragment.newInstance(intentData.getInt(NUMBER_OF_QUESTIONS_KEY), intentData.getIntegerArrayList(QUESTION_SET_IDS_KEY), intentData.getInt(GAME_TYPE_KEY));
        return gameFragment;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        int numberOfQuestions = getIntent().getExtras().getInt(NUMBER_OF_QUESTIONS_KEY);
//        int gameType = getIntent().getExtras().getInt(GAME_TYPE_KEY);
//        ArrayList<Integer> setIds = getIntent().getExtras().getIntegerArrayList(QUESTION_SET_IDS_KEY);
//        super.onCreate(savedInstanceState);
//
//        viewModel = new GameViewModel(gameType, numberOfQuestions, setIds);
//        viewModel.initialize(this);
//        int numberOfPlayers = viewModel.getNumberOfPlayers();
//        if( numberOfPlayers == 2) {
//            GameActivity2playersBinding binding = DataBindingUtil.setContentView(this, R.layout.game_activity_2players);
//            binding.setViewModel(viewModel);
//        }
//        else if( numberOfPlayers <= 4) {
//            GameActivity4playersBinding binding = DataBindingUtil.setContentView(this, R.layout.game_activity_4players);
//            binding.setViewModel(viewModel);
//        } else {
//            GameActivity6playersBinding binding = DataBindingUtil.setContentView(this, R.layout.game_activity_6players);
//            binding.setViewModel(viewModel);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel = null;
    }

    @Override
    public void onBackPressed() {
        viewModel.pauseGame();
        openQuitDialog();
    }

    @Override
    protected void onUserLeaveHint() {
        viewModel.pauseGame();
        if (!viewModel.isGameOver()) {
            openQuitDialog();
        }
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                GameActivity.this);
        quitDialog.setTitle(R.string.quit_dialog_title);

        quitDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(GameActivity.this, StartActivity.class);
                finish();
                startActivity(intent);
            }
        });

        quitDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.resumeGame();
            }
        });

        quitDialog.show();
    }
}
