package ru.cybernut.fiveseconds.view;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;

import ru.cybernut.fiveseconds.BR;
import ru.cybernut.fiveseconds.model.Player;

public class PlayerModel extends BaseObservable implements Serializable {
    private static String TAG = "PlayerModel";

    private Player player;
    private boolean isCurrentPlayer;
    private double progressbarValue;
    private int score;

    public PlayerModel(Player player) {
        this.player = player;
        this.isCurrentPlayer = false;
        this.progressbarValue = 0;
        this.score = 0;
    }

    @Bindable
    public int getScore() {
        return score;
    }

    @Bindable
    public String getName() {
        return player.getName();
    }

    public void setScore() {
        increaseScore();
        notifyPropertyChanged(BR.score);
    }

    private void increaseScore() {
        score++;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        notifyPropertyChanged(BR.player);
    }

    @Bindable
    public boolean isCurrentPlayer() {
        return isCurrentPlayer;
    }

    public void setCurrentPlayer(boolean currentPlayer) {
        isCurrentPlayer = currentPlayer;
        notifyPropertyChanged(BR.currentPlayer);
    }

    @Bindable
    public double getProgressbarValue() {
        return progressbarValue;
    }

    public void setProgressbarValue(double progressbarValue) {
        this.progressbarValue = progressbarValue;
        notifyPropertyChanged(BR.progressbarValue);
    }

    public void increaseProgressbarValue(double addValue) {
        this.progressbarValue = this.progressbarValue + addValue;
        notifyPropertyChanged(BR.progressbarValue);
    }
}
