package ru.cybernut.fiveseconds.view;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;
import android.view.View;

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
    private boolean isNeedShowAnswer = false;
    private boolean isNeedShowQuestion = false;
    private GameEngine game;
    private int numberOfPlayers;
    private String currentQuestionText;
    private ArrayList<Integer> setIds;
    private ArrayList<PlayerModel> players;
    private PlayerModel currentPlayer;
    private int currentRotationValue;

    public GameViewModel(Context appContext, int numberOfQuestions, ArrayList<Integer> setIds) {
        this.setIds = setIds;
        game = new GameEngine(appContext, this, 0, numberOfQuestions);
        initPlayers();
    }

    private void initPlayers() {
        players = new ArrayList<>();
        List<Player> list = PlayersList.getInstance().getList();
        numberOfPlayers = list.size();
        for (Player player : list) {
            players.add(new PlayerModel(player));
        }
        nextPlayer(); //set a current player
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
        if(currentPlayer != null) {
            currentPlayer.setProgressbarValue(0);
            currentPlayer.setCurrentPlayer(false);
        }
        int currentIndex = players.indexOf(currentPlayer);
        if ((currentIndex + 1) == players.size()) {
            currentPlayer = players.get(0);
        } else {
            currentPlayer = players.get(currentIndex + 1);
        }
        currentPlayer.setCurrentPlayer(true);

        calculateRotation();
    }

    @Bindable
    public boolean isNeedShowAnswer() {
        return isNeedShowAnswer;
    }

    public void setNeedShowAnswer(boolean needShowAnswer) {
        isNeedShowAnswer = needShowAnswer;
        notifyPropertyChanged(BR.needShowAnswer);
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

    public void handleAnswer(boolean isCorrectAnswer) {
        Log.i(TAG, "handleAnswer: " + isCorrectAnswer);
        if(isCorrectAnswer) {
            currentPlayer.setScore();
        }
        setNeedShowAnswer(false);
        nextPlayer();
        onNextTurn(null);
    }

    public void onNextTurn(View v) {
        setStarted(true);
        notifyPropertyChanged(BR.started);
        setCurrentQuestionText(game.getCurrentQuestionText());
        notifyPropertyChanged(BR.currentQuestionText);
        Log.i(TAG, "onNextTurn: " + currentQuestionText);
        game.nextTurn();
    }

    @Bindable
    public int getCurrentRotationValue() {
        return currentRotationValue;
    }

    public void setCurrentRotationValue(int currentRotationValue) {
        this.currentRotationValue = currentRotationValue;
        notifyPropertyChanged(BR.currentRotationValue);
    }

    public void calculateRotation() {
        int currentIndex = players.indexOf(currentPlayer);
        if(numberOfPlayers <= 4) {
            switch (currentIndex) {
                case 0:
                    currentRotationValue = 0;
                    break;
                case 1:
                    currentRotationValue = -90;
                    break;
                case 2:
                    currentRotationValue = -180;
                    break;
                case 3:
                    currentRotationValue = -270;
                    break;
            }
        } else {
            switch (currentIndex) {
                case 0:
                    currentRotationValue = 0;
                    break;
                case 1:
                    currentRotationValue = 0;
                    break;
                case 2:
                    currentRotationValue = -90;
                    break;
                case 3:
                    currentRotationValue = -180;
                    break;
                case 4:
                    currentRotationValue = -180;
                    break;
                case 5:
                    currentRotationValue = -270;
                    break;
            }
        }
        notifyPropertyChanged(BR.currentRotationValue);
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
        currentPlayer.increaseProgressbarValue(5);
    }

    @Override
    public void timerFinished() {
        currentPlayer.setProgressbarValue(100);
        //setStarted(false);
        setGameReady(false);
        setNeedShowAnswer(true);
    }
}
