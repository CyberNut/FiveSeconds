package ru.cybernut.fiveseconds.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Player implements Serializable {

    private static int count = 1;

    private String name;
    private Drawable photo;
    private boolean isGameMaster;
    private int score;
    private int id;
    private boolean isCurrentPlayer;

    public Player(String name) {
        this.name = name;
        this.isGameMaster = false;
        this.photo = null;
        this.id = count++;
        this.isCurrentPlayer = false;
    }

    public Player(String name, Drawable photo, boolean isGameMaster) {
        this.name = name;
        this.photo = photo;
        this.isGameMaster = isGameMaster;
        this.id = count++;
        this.isCurrentPlayer = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getScore() {
        return score;
    }

    public void increaseScore() {
        score++;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isCurrentPlayer() {
        return isCurrentPlayer;
    }

    public void setCurrentPlayer(boolean currentPlayer) {
        isCurrentPlayer = currentPlayer;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
