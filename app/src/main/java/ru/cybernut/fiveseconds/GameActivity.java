package ru.cybernut.fiveseconds;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;


import java.util.ArrayList;
import java.util.HashMap;

import ru.cybernut.fiveseconds.databinding.GameActivityBinding;
import ru.cybernut.fiveseconds.model.Game;
import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.view.GameViewModel;

public class GameActivity extends AppCompatActivity implements Game.GUIUpdatable, Game.Initializable {

    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";
    private static final String QUESTION_SET_IDS_KEY = "QUESTION_SET_IDS_KEY";
    private static final int ID_START_VALUE = 770070;

    private int numberOfQuestions;
    private ArrayList<Integer> setIds;
    private GameViewModel viewModel;
    private ConstraintLayout mainContainer;

    public static Intent newIntent(Context context, int numberOfQuestions, ArrayList<Integer> setsIds) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        intent.putIntegerArrayListExtra(QUESTION_SET_IDS_KEY, setsIds);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        numberOfQuestions = getIntent().getExtras().getInt(NUMBER_OF_QUESTIONS_KEY);
        setIds = getIntent().getExtras().getIntegerArrayList(QUESTION_SET_IDS_KEY);
        super.onCreate(savedInstanceState);

        GameActivityBinding bindings = DataBindingUtil.setContentView(this, R.layout.game_activity);
        mainContainer = (ConstraintLayout) bindings.getRoot();
        viewModel = new GameViewModel(getApplication(), numberOfQuestions, setIds);
        bindings.setGameViewModel(viewModel);
        prepareUI();
    }

    private void prepareUI() {

        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < viewModel.getNumberOfPlayers(); i++) {
            FrameLayout frameLayout = new FrameLayout(this);
            setLayoutParamsById(frameLayout, i, viewModel.getNumberOfPlayers());
            frameLayout.setId(ID_START_VALUE + i);
            mainContainer.addView(frameLayout);

            PlayerCardFragment playerCardFragment = PlayerCardFragment.newInstance(null);
            Fragment fragment = fm.findFragmentById(ID_START_VALUE + i);
            if(fragment == null) {
                fm.beginTransaction()
                        .replace(ID_START_VALUE + i, playerCardFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                //playerCardFragmentMap.put(playerId, playerCardFragment);
            }
        }
//
//
//        if(numberOfPlayers == 0) {
//            return;
//        }
//
//        questionContainer = findViewById(R.id.question_container);
//
//        alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setTitle(R.string.answer_dialog_title);
//        alertDialogBuilder.setMessage(R.string.answer_dialog_message);
//        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                playerCardFragment = playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer()));
//                if(playerCardFragment != null) {
//                    playerCardFragment.setProgress(0.01);
//                    playerCardFragment.setCurrentLabel(false);
//                    game.nextTurn(true);
//                    updateQuestionTextPosition();
//                    gameTimer.start();
//
//                }
//            }
//        });
//        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                playerCardFragment = playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer()));
//                if(playerCardFragment != null) {
//                    playerCardFragment.setProgress(0.01);
//                    playerCardFragment.setCurrentLabel(false);
//                    game.nextTurn(false);
//                    updateQuestionTextPosition();
//                    gameTimer.start();
//                }
//            }
//        });
//        alertDialogBuilder.setCancelable(false);


    }

    private void setLayoutParamsById(FrameLayout frameLayout, int playerIndex, int numberOfPlayers) {

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 8,8,8);
        switch(playerIndex) {
            case 0:
                layoutParams.startToStart = 0;
                layoutParams.endToEnd = 0;
                layoutParams.bottomToBottom = 0;
                if(numberOfPlayers <= 4) {
                    layoutParams.horizontalBias = 0.7f;
                } else {
                    layoutParams.horizontalBias = 0.25f;
                }
                break;
            case 1:
                if(numberOfPlayers <= 4) {
                    layoutParams.endToEnd = 0;
                    layoutParams.topToTop = 0;
                    layoutParams.bottomToBottom = 0;
                    layoutParams.verticalBias = 0.3f;
                    frameLayout.setRotation(-90);
                } else {
                    layoutParams.startToStart = 0;
                    layoutParams.endToEnd = 0;
                    layoutParams.bottomToBottom = 0;
                    layoutParams.horizontalBias = 0.75f;
                }
                break;
            case 2:
                if(numberOfPlayers <= 4) {
                    layoutParams.startToStart = 0;
                    layoutParams.endToEnd = 0;
                    layoutParams.topToTop = 0;
                    layoutParams.horizontalBias = 0.3f;
                    frameLayout.setRotation(-180);
                } else {
                    layoutParams.endToEnd = 0;
                    layoutParams.topToTop = 0;
                    layoutParams.bottomToBottom = 0;
                    layoutParams.horizontalBias = 0.75f;
                    frameLayout.setRotation(-90);
                }
                break;
            case 3:
                if(numberOfPlayers <= 4) {
                    layoutParams.startToStart = 0;
                    layoutParams.bottomToBottom = 0;
                    layoutParams.topToTop = 0;
                    layoutParams.verticalBias = 0.7f;
                    frameLayout.setRotation(-270);
                } else {
                    layoutParams.endToEnd = 0;
                    layoutParams.topToTop = 0;
                    layoutParams.startToStart = 0;
                    layoutParams.horizontalBias = 0.75f;
                    frameLayout.setRotation(-180);
                }
                break;
            case 4:
                layoutParams.endToEnd = 0;
                layoutParams.topToTop = 0;
                layoutParams.startToStart = 0;
                layoutParams.horizontalBias = 0.25f;
                frameLayout.setRotation(-180);
                break;
            case 5:
                layoutParams.bottomToBottom = 0;
                layoutParams.topToTop = 0;
                layoutParams.startToStart = 0;
                layoutParams.verticalBias = 0.5f;
                frameLayout.setRotation(-270);
                break;
        }
        frameLayout.setLayoutParams(layoutParams);
    }





    @Override
    public void update() {

    }

    @Override
    public void initDone() {

    }

    public void onNextQuestionButton(View view) {
        viewModel.nextTurn(true);
    }
}
