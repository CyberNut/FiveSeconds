package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;

public class MainGameActivity extends AppCompatActivity {

    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";

    private int numberOfQuestions;
    private PlayerCardFragment playerCardFragment;

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
    }

    private void prepareUI() {


        PlayersList playersList = PlayersList.getInstance();
        if(playersList.getNumberOfPlayers() == 0) {
            return;
        }

        Player firstPlayer = playersList.getPlayer(0);
        playerCardFragment = PlayerCardFragment.newInstance(firstPlayer);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.player_card_test);
        if(fragment == null) {
            fm.beginTransaction()
                    .replace(R.id.player_card_test, playerCardFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    public void onTestCardClick(View view) {
        prepareUI();
    }

    public void onIncreaseScoreButtonClick(View view) {
        PlayersList playersList = PlayersList.getInstance();
        Player firstPlayer = playersList.getPlayer(0);
        firstPlayer.increaseScore();
        playerCardFragment.updateUI();
    }
}
