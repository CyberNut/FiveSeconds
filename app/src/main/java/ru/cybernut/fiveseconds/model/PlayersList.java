package ru.cybernut.fiveseconds.model;

import java.util.ArrayList;
import java.util.List;

public class PlayersList {

    private static PlayersList instance;
    private List<Player> list;

    public static PlayersList getInstance() {
        if(instance == null) {
            instance = new PlayersList();
        }
        return instance;
    }

    public List<Player> getList() {
        return list;
    }

    public int getNumberOfPlayers() {
        return list.size();
    }

    public void addPlayer(Player player) {
        list.add(player);
    }

    public void putPlayer(Player player, int position) {
        if(position >= 0) {
            list.add(position, player);
        }
    }

    public void deletePlayer(int position) {
        if(position <= getNumberOfPlayers()) {
            list.remove(position);
        }
    }

    public Player getPlayer(int number) {
        return list.get(number);
    }

    private PlayersList() {
        list = new ArrayList<>();
    }
}