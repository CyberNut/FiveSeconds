package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ru.cybernut.fiveseconds.model.Game;
import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.model.Question;

public class MainGameActivity extends AppCompatActivity  implements Game.GUIUpdatable {

    private static final String TAG = "MainGameActivity";
    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";
    private static final int ID_START_VALUE = 770070;

    private int numberOfQuestions;
    private PlayerCardFragment playerCardFragment;
    private PlayersList playersList;
    private ConstraintLayout mainContainer;
    private Map<Integer, PlayerCardFragment> playerCardFragmentMap;
    private Game game;
    private TextView questionTextView;

    public static Intent newIntent(Context context, int numberOfQuestions) {
        Intent intent = new Intent(context, MainGameActivity.class);
        intent.putExtra(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        numberOfQuestions = getIntent().getExtras().getInt(NUMBER_OF_QUESTIONS_KEY);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_game_activity);
        mainContainer = findViewById(R.id.main_game_container);
        questionTextView = (TextView) findViewById(R.id.question_text_view);
        initialize();
        prepareUI();
    }

    private void initialize() {
        game = new Game(this, numberOfQuestions, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerCardFragment = playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer()));
        if(playerCardFragment != null) {
            playerCardFragment.setCurrentLabel(true);
        }
    }

    private void prepareUI() {

        playersList = PlayersList.getInstance();
        int numberOfPlayers = playersList.getNumberOfPlayers();
        playerCardFragmentMap = new HashMap<>();

        if(numberOfPlayers == 0) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();

        for (Player player : playersList.getList()) {

            int playerId = playersList.getId(player);
            if(playerId <= 0) {
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

    private void setLayoutParamsById(FrameLayout frameLayout, int id, int numberOfPlayers) {

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 8,8,8);
        switch(id) {
            case 1:
                layoutParams.startToStart = 0;
                layoutParams.endToEnd = 0;
                layoutParams.bottomToBottom = 0;
                if(numberOfPlayers <= 4) {
                    layoutParams.horizontalBias = 0.7f;
                } else {
                    layoutParams.horizontalBias = 0.25f;
                }
                break;
            case 2:
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
            case 3:
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
            case 4:
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
            case 5:
                layoutParams.endToEnd = 0;
                layoutParams.topToTop = 0;
                layoutParams.startToStart = 0;
                layoutParams.horizontalBias = 0.25f;
                frameLayout.setRotation(-180);
                break;
            case 6:
                layoutParams.bottomToBottom = 0;
                layoutParams.topToTop = 0;
                layoutParams.startToStart = 0;
                layoutParams.verticalBias = 0.5f;
                frameLayout.setRotation(-270);
                break;
        }
        frameLayout.setLayoutParams(layoutParams);
    }

    public void onStartButtonClick(View view) {
        playerCardFragment = playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer()));
        if(playerCardFragment != null) {
            playerCardFragment.setCurrentLabel(false);
        }
        game.nextTurn(true);
    }

    private void updateUI() {

        playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer())).setCurrentLabel(true);
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
}