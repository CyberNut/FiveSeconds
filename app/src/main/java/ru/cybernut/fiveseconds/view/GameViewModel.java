package ru.cybernut.fiveseconds.view;

import java.util.ArrayList;

import ru.cybernut.fiveseconds.model.Game;
import ru.cybernut.fiveseconds.model.PlayersList;

public class GameViewModel {

    private boolean isGameReady = false;
    private Game game;
    private String currentQuestionText;

    public GameViewModel(int numberOfQuestions, ArrayList<Integer> setIds) {



    }

    public int getNumberOfPlayers() {
        return PlayersList.getInstance().getNumberOfPlayers();
    }


}
