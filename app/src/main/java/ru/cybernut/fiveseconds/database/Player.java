package ru.cybernut.fiveseconds.database;

import android.graphics.drawable.Drawable;

public class Player {

    private String name;
    private Drawable photo;
    private boolean isGameMaster;

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
}
