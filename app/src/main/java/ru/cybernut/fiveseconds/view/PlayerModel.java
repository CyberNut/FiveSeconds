package ru.cybernut.fiveseconds.view;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import ru.cybernut.fiveseconds.BR;
import ru.cybernut.fiveseconds.model.Player;

public class PlayerModel extends BaseObservable {

    private Player player;
    private boolean isCurrentPlayer;
    private double progressbarValue;

    public PlayerModel(Player player) {
        this.player = player;
        this.isCurrentPlayer = false;
        this.progressbarValue = 0;
    }

    @Bindable
    public int getScore() {
        return player.getScore();
    }

    @Bindable
    public String getName() {
        return player.getName();
    }


    public void setScore() {
        player.increaseScore();
        notifyPropertyChanged(BR.score);
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
}
