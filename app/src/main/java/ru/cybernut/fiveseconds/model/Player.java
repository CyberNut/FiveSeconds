package ru.cybernut.fiveseconds.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import ru.cybernut.fiveseconds.BR;

public class Player extends BaseObservable implements Serializable {

    @Expose
    private String name;
    @Expose(serialize = false)
    private Drawable photo;
    @Expose
    private boolean isGameMaster;
    @Expose(serialize = false)
    private int score;

    public Player(String name) {
        this.name = name;
        this.isGameMaster = false;
        this.photo = null;
    }

    public Player(String name, Drawable photo, boolean isGameMaster) {
        this.name = name;
        this.photo = photo;
        this.isGameMaster = isGameMaster;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public Drawable getPhoto() {
        return photo;
    }

    public void setPhoto(Drawable photo) {
        this.photo = photo;
    }

    public boolean isGameMaster() {
        return isGameMaster;
    }

    public void setGameMaster(boolean gameMaster) {
        isGameMaster = gameMaster;
    }

    @Bindable
    public int getScore() {
        return score;
    }

    public void increaseScore() {
        score++;
        notifyPropertyChanged(BR.score);
    }

    public void setScore(int score) {
        this.score = score;
        notifyPropertyChanged(BR.score);
    }

    @Override
    public String toString() {
        return name;
    }
}
