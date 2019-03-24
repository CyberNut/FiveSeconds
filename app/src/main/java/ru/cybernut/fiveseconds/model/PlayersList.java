package ru.cybernut.fiveseconds.model;

import java.util.ArrayList;
import java.util.Collections;
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

    public boolean addPlayer(Player player) {
        if(list.size() < GameEngine.MAX_PLAYERS) {
            list.add(player);
            return true;
        }
        return false;
    }

    public void putPlayer(Player player, int position) {
        if(position >= 0) {
            list.add(position, player);
        }
    }

    public Player getPlayerById(int id) {
        if(list.size() > id && id > 0) {
            return list.get(id);
        }
        return null;
    }

    public void deletePlayer(int position) {
        if(position <= getNumberOfPlayers()) {
            list.remove(position);
        }
    }

    public int getId(Player player) {
        return list.indexOf(player);
    }

    public Player getPlayer(int number) {
        return list.get(number);
    }

    private PlayersList() {
        list = new ArrayList<>();
    }
}
