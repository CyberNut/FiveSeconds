package ru.cybernut.fiveseconds.view;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import ru.cybernut.fiveseconds.BR;
import ru.cybernut.fiveseconds.model.Game;
import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.model.Question;

public class GameViewModel extends BaseObservable implements Game.Initializable {

    private static final String TAG = "GameViewModel";
    
    private boolean isStarted = false;
    private boolean isGameReady = false;
    private Game game;
    private String currentQuestionText;
    private PlayersList playersList;
    private ArrayList<Integer> setIds;
    private Question currentQuestion;
    private Question nextQuestion;
    private Player currentPlayer;
    private CountDownTimer gameTimer;

    public GameViewModel(Context appContext, int numberOfQuestions, ArrayList<Integer> setIds) {
        playersList = PlayersList.getInstance();
        this.currentPlayer = playersList.getPlayer(0);
        this.setIds = setIds;
        game = new Game(appContext, this, Game.GameType.AUTO_PLAY_SOUND, numberOfQuestions);
    }

    public void initialize() {
        new GameInitTask().execute();
//        gameTimer = new CountDownTimer(5000, 100) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                if(!isPaused) {
//                    playerCardFragment = playerCardFragmentMap.get(playersList.getId(game.getCurrentPlayer()));
//                    playerCardFragment.setProgress(playerCardFragment.getProgress() + 2);
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                playerCardFragment.setProgress(100);
//                alertDialogBuilder.show();
//            }
//        };


    }

    public Player getPlayer(int index) {
        if (index <= (playersList.getNumberOfPlayers() - 1)) {
            return playersList.getPlayer(index);
        } else {
            return null;
        }
    }

    public int getNumberOfPlayers() {
        return PlayersList.getInstance().getNumberOfPlayers();
    }

    private void nextPlayer() {
        int currentPlayerId = playersList.getId(currentPlayer);
        currentPlayerId++;
        if (currentPlayerId > (playersList.getNumberOfPlayers() - 1)) {
            currentPlayer = playersList.getPlayer(0);
        } else {
            currentPlayer = playersList.getPlayer(currentPlayerId);
        }
    }

    @Bindable
    public boolean isGameReady() {
        return isGameReady;
    }

    private void setGameReady(boolean gameReady) {
        isGameReady = gameReady;
        notifyPropertyChanged(BR.gameReady);
        Log.i(TAG, "setGameReady: ");
    }

    @Bindable
    public boolean isStarted() {
        return isStarted;
    }

    private void setStarted(boolean started) {
        isStarted = started;
        notifyPropertyChanged(BR.started);
    }

    @Bindable
    public String getCurrentQuestionText() {
        return currentQuestionText;
    }

    private void setCurrentQuestionText(String currentQuestionText) {
        this.currentQuestionText = currentQuestionText;
        notifyPropertyChanged(BR.currentQuestionText);
    }

    @Bindable
    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void onNextTurn(View v) {

        currentQuestion = game.getCurrentQuestion();
        setStarted(true);
        new GameInitTask().execute();
        game.playCurrentSound();
        Log.i(TAG, "onNextTurn: " + currentQuestionText);
        notifyPropertyChanged(BR.currentQuestion);
        notifyPropertyChanged(BR.started);
    }
    
    private class GameInitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            game.init(setIds);
            Log.i(TAG, "doInBackground: ");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "doInBackground: ", e);
            }
            return null;
        }
    }

    private class GameTurnTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            currentQuestion = nextQuestion;
            nextQuestion = game.getNextQuestion();
            return null;
        }
    }

    @Override
    public void initDone() {
        currentQuestion = game.getCurrentQuestion();
        setGameReady(true);
        setCurrentQuestionText(game.getCurrentQuestion().getText());
    }
}
