package ru.cybernut.fiveseconds.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.model.Player;

public class SharedPreferencesHelper {

    public static final String SHARED_PREF_NAME = "PLAYER_NAMES";
    public static final String PLAYERS_KEY = "PLAYERS_KEY";
    public static final Type PLAYERS_TYPE = new TypeToken<List<Player>>() {}.getType();

    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<Player> getPlayers() {
        List<Player> players = gson.fromJson(sharedPreferences.getString(PLAYERS_KEY, ""), PLAYERS_TYPE);
        return players == null ? new ArrayList<Player>() : players;
    }

    public void savePlayers(List<Player> playerList) {
        sharedPreferences.edit().putString(PLAYERS_KEY,
                gson.toJson(playerList, PLAYERS_TYPE)).apply();
    }
}
