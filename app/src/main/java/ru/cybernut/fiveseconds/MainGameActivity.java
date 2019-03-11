package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ru.cybernut.fiveseconds.model.Game;
import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.model.Question;

public class MainGameActivity extends AppCompatActivity  implements Game.GUIUpdatable, Game.Initializable {

    private static final String TAG = "MainGameActivity";
    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";
    private static final String QUESTION_SET_IDS_KEY = "QUESTION_SET_IDS_KEY";
    private static final int ID_START_VALUE = 770070;

    private int numberOfQuestions;
    private PlayerCardFragment playerCardFragment;
    private PlayersList playersList;
    private ConstraintLayout mainContainer;
    private Map<Integer, PlayerCardFragment> playerCardFragmentMap;
    private Game game;
    private TextView questionTextView;
    private ProgressBar gameInitProgressBar;
    private AlertDialog.Builder alertDialogBuilder;
    private CountDownTimer gameTimer;
    private FrameLayout questionContainer;
    private boolean isPaused = false;
    private ArrayList<Integer> setIds;
    private boolean isGameReady = false;

    public static Intent newIntent(Context context, int numberOfQuestions, ArrayList<Integer> setsIds) {
        Intent intent = new Intent(context, MainGameActivity.class);
        intent.putExtra(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        intent.putIntegerArrayListExtra(QUESTION_SET_IDS_KEY, setsIds);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        numberOfQuestions = getIntent().getExtras().getInt(NUMBER_OF_QUESTIONS_KEY);
        setIds = getIntent().getExtras().getIntegerArrayList(QUESTION_SET_IDS_KEY);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_game_activity);
        mainContainer = findViewById(R.id.main_game_container);
        questionTextView = (TextView) findViewById(R.id.question_text_view);
        gameInitProgressBar = (ProgressBar) findViewById(R.id.init_game_progressbar);
        initialize();
        prepareUI();
    }

    private void initialize() {
        game = new Game(this, null,  Game.GameType.AUTO_PLAY_SOUND, numberOfQuestions);
        game.init(setIds);


        gameTimer = new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isPaused) {
                    playerCardFragment = playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer()));
                    playerCardFragment.setProgress(playerCardFragment.getProgress() + 2);
                }
            }

            @Override
            public void onFinish() {
                playerCardFragment.setProgress(100);
                alertDialogBuilder.show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerCardFragment = playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer()));
        if(playerCardFragment != null) {
            playerCardFragment.setCurrentLabel(true);
            playerCardFragment.setProgress(0.01);
        }
        if(game.getCurrentQuestion()!= null) {
            questionTextView.setText(game.getCurrentQuestion().getText());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        if(isGameReady) {
            gameInitProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void prepareUI() {

        playersList = PlayersList.getInstance();
        int numberOfPlayers = playersList.getNumberOfPlayers();
        playerCardFragmentMap = new HashMap<>();

        if(numberOfPlayers == 0) {
            return;
        }

        questionContainer = findViewById(R.id.question_container);

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.answer_dialog_title);
        alertDialogBuilder.setMessage(R.string.answer_dialog_message);
        alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playerCardFragment = playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer()));
                if(playerCardFragment != null) {
                    playerCardFragment.setProgress(0.01);
                    playerCardFragment.setCurrentLabel(false);
                    game.nextTurn(true);
                    updateQuestionTextPosition();
                    gameTimer.start();

                }
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playerCardFragment = playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer()));
                if(playerCardFragment != null) {
                    playerCardFragment.setProgress(0.01);
                    playerCardFragment.setCurrentLabel(false);
                    game.nextTurn(false);
                    updateQuestionTextPosition();
                    gameTimer.start();
                }
            }
        });
        alertDialogBuilder.setCancelable(false);

        FragmentManager fm = getSupportFragmentManager();

        for (Player player : playersList.getList()) {

            int playerId = playersList.getId(player);
            if(playerId < 0) {
                continue;
            }
            FrameLayout frameLayout = new FrameLayout(this);
            setLayoutParamsById(frameLayout, playerId, numberOfPlayers);
            frameLayout.setId(ID_START_VALUE + playerId);
            mainContainer.addView(frameLayout);

            playerCardFragment = PlayerCardFragment.newInstance(player);
            Fragment fragment = fm.findFragmentById(ID_START_VALUE + playerId);
            if(fragment == null) {
                fm.beginTransaction()
                        .replace(ID_START_VALUE + playerId, playerCardFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                playerCardFragmentMap.put(playerId, playerCardFragment);
            }
        }

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

    private void updateQuestionTextPosition() {
        int currentPlayerId = playersList.getId(game.getCurrentPlayer());
        int numberOfPlayers = playersList.getNumberOfPlayers();
        switch (currentPlayerId) {
            case 0:
                questionContainer.setRotation(0);
                break;
            case 1:
                if(numberOfPlayers <= 4) {
                    questionContainer.setRotation(-90);
                } else {
                    questionContainer.setRotation(0);
                }
                break;
            case 2:
                if(numberOfPlayers <= 4) {
                    questionContainer.setRotation(-180);
                } else {
                    questionContainer.setRotation(-90);
                }
                break;
            case 3:
                if(numberOfPlayers <= 4) {
                    questionContainer.setRotation(-270);
                } else {
                    questionContainer.setRotation(-180);
                }
                break;
            case 4:
                questionContainer.setRotation(-180);
                break;
            case 5:
                questionContainer.setRotation(-270);
                break;
        }
    }

    public void onStartButtonClick(View view) {
        playerCardFragment = playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer()));
        
        if(game.isGameReady() && gameTimer != null) {
            game.playCurrentSound();
            gameTimer.start();
        } else {
            Log.i(TAG, "onStartButtonClick: Game is not ready or gameTimer == null");
        }
    }

    private void updateUI() {

        playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer())).setCurrentLabel(true);
        for (PlayerCardFragment cardFragment : playerCardFragmentMap.values()) {
            cardFragment.updateUI();
        }
        Question question = game.getCurrentQuestion();
        if(question != null) {
            questionTextView.setText(question.getText());
        }
    }

    @Override
    public void update() {
        Log.i(TAG, "update: ");
        updateUI();
    }

    @Override
    public void initDone() {
        isGameReady = true;
        gameInitProgressBar.setVisibility(View.INVISIBLE);
    }
}
