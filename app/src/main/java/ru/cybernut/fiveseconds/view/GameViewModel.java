package ru.cybernut.fiveseconds.view;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.BR;
import ru.cybernut.fiveseconds.model.GameEngine;
import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;

public class GameViewModel extends BaseObservable implements GameEngine.Updatable {

    private static final String TAG = "GameViewModel";
    
    private boolean isStarted = false;
    private boolean isGameReady = false;
    private GameEngine game;
    private String currentQuestionText;
    private ArrayList<Integer> setIds;
    private CountDownTimer gameTimer;
    private ArrayList<PlayerModel> players;

    public GameViewModel(Context appContext, int numberOfQuestions, ArrayList<Integer> setIds) {
        this.setIds = setIds;
        game = new GameEngine(appContext, this, 0, numberOfQuestions);
        initPlayers();
    }

    private void initPlayers() {
        players = new ArrayList<>();
        List<Player> list = PlayersList.getInstance().getList();
        for (Player player : list) {
            players.add(new PlayerModel(player));
        }
        players.get(0).setCurrentPlayer(true);
    }

    public void initialize() {
        game.initialize(setIds);
    }

    public PlayerModel getPlayer(int index) {
        if (index <= (players.size() - 1)) {
            return players.get(index);
        } else {
            return null;
        }
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    private void nextPlayer() {
        for (int i = 0; i < players.size(); i++) {
            PlayerModel tempPlayer = players.get(i);
            if (tempPlayer.isCurrentPlayer()) {
                tempPlayer.setCurrentPlayer(false);
                if ((i + 1) == players.size()) {
                    players.get(0).setCurrentPlayer(true);
                } else {
                    players.get(i + 1).setCurrentPlayer(true);
                }
            }
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
    public boolean getStarted() {
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

    public void onNextTurn(View v) {
        setStarted(true);
        notifyPropertyChanged(BR.started);
        setCurrentQuestionText(game.getCurrentQuestionText());
        notifyPropertyChanged(BR.currentQuestionText);
        game.playCurrentSound();
        Log.i(TAG, "onNextTurn: " + currentQuestionText);
        game.nextTurn();
    }
    
    @Override
    public void initDone() {
        setGameReady(true);
    }

    @Override
    public void gameOver() {
        Log.i(TAG, "gameOver: ");
    }

    @Override
    public void progressUpdate() {
        Log.i(TAG, "progressUpdate: ");

    }
}
