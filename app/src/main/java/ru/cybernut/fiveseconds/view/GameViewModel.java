package ru.cybernut.fiveseconds.view;

import android.util.Log;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.BR;
import ru.cybernut.fiveseconds.model.GameEngine;
import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;

public class GameViewModel extends BaseObservable implements GameEngine.Updatable, Serializable {

    private static final String TAG = "GameViewModel";
    
    private boolean isStarted = false;
    private boolean isGameReady = false;
    private boolean isNeedShowAnswer = false;
    private boolean isGameOver = false;
    private boolean isManualStartTimer = false;
    private GameEngine game;
    private int numberOfPlayers;
    private String currentQuestionText;
    private ArrayList<Integer> setIds;
    private ArrayList<PlayerModel> players;
    private PlayerModel currentPlayer;
    private int currentRotationValue;
    private int numberOfRounds;
    private int currentRound = 1;
    private int currentQuestionNumber = 1;
    private GameOverable gameActivity;

    public GameViewModel(int gameType, int numberOfRounds, ArrayList<Integer> setIds, boolean isNeedPlaySound) {
        this.setIds = setIds;
        this.numberOfRounds = numberOfRounds;
        initPlayers();
        game = new GameEngine(this, gameType, players.size() * numberOfRounds, isNeedPlaySound);
        setManualStartButtonVisible();
    }

    private void setManualStartButtonVisible() {
        if (game.getGameType() == GameEngine.GAME_TYPE_MANUAL) {
            setManualStartTimer(true);
        }
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

    public void initialize(GameOverable gameActivity) {
        this.gameActivity = gameActivity;
        game.initialize(setIds);
    }

    public void cleanViewModel() {
        game.destroy();
        gameActivity = null;

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

    public void updateCurrentRound() {
        if((currentQuestionNumber++ % numberOfPlayers) == 0) {
            if(currentRound<numberOfRounds) {
                currentRound++;
                notifyPropertyChanged(BR.currentRound);
            }
        }
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
    public boolean isManualStartTimer() {
        return isManualStartTimer;
    }

    public void setManualStartTimer(boolean manualStartTimer) {
        isManualStartTimer = manualStartTimer;
        notifyPropertyChanged(BR.manualStartTimer);
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

    public boolean isGameOver() {
        return isGameOver;
    }

    @Bindable
    public String getCurrentQuestionText() {
        return currentQuestionText;
    }

    @Bindable
    public String getCurrentRound() {
        return String.valueOf(currentRound);
    }

    @Bindable
    public String getNumberOfRounds() {
        return String.valueOf(numberOfRounds);
    }

    private void setCurrentQuestionText(String currentQuestionText) {
        this.currentQuestionText = currentQuestionText;
        notifyPropertyChanged(BR.currentQuestionText);
    }

    public void handleAnswer(boolean isCorrectAnswer) {
        if (isCorrectAnswer) {
            currentPlayer.setScore();
        }
        if (isGameOver) {
            gameActivity.gameOver(players);
        }
        if (isNeedShowAnswer) {
            setNeedShowAnswer(false);
            nextPlayer();
            onNextTurn(null);
        }
        updateCurrentRound();
    }

    public void onNextTurn(View v) {
        if(isGameOver) { return;}
        setStarted(true);
        setCurrentQuestionText(game.getCurrentQuestionText());
        setManualStartButtonVisible();

        if (!isManualStartTimer) {
            game.nextTurn();
        }
    }

    public void manualStartTimer() {
        setManualStartTimer(false);
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
        if(numberOfPlayers == 2) {
            switch (currentIndex) {
                case 0:
                    currentRotationValue = 0;
                    break;
                case 1:
                    currentRotationValue = -180;
                    break;
            }
        } else if(numberOfPlayers <= 4) {
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
        isGameOver = true;
    }

    @Override
    public void progressUpdate(long value) {
        currentPlayer.setProgressbarValue(value);
    }

    @Override
    public void timerFinished() {
        currentPlayer.setProgressbarValue(100);
        if(isStarted) {
            setNeedShowAnswer(true);
        }
    }

    public void pauseGame() {
        Log.i(TAG, "pauseGame");
        if(game == null) {return;}

        if(isStarted && !game.isPaused()) {
            game.pause();
        }
    }

    public void resumeGame() {
        if(game == null) {return;}

        if(game.isPaused()) {
            game.resume();
        }
    }

    public interface GameOverable {
        void gameOver(ArrayList<PlayerModel> playerModelList);
    }
}
