package ru.cybernut.fiveseconds.model;

import java.util.List;

public class Game {
    public static final int MAX_PLAYERS = 6;

    private List<Player> playerList;
    private Player currentPlayer;
    private int numberOfQuestions;

    public Game(List<Player> playerList, int numberOfQuestions) {
        this.playerList = playerList;
        this.numberOfQuestions = numberOfQuestions;
    }


}
