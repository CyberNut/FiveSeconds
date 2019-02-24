package ru.cybernut.fiveseconds.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    public static final int MAX_PLAYERS = 6;

    private Context context;
    private List<Player> playerList;
    private Player currentPlayer;
    private Question currentQuestion;
    private int numberOfQuestions;
    List<String> uuidList;

    public Game(Context context, List<Player> playerList, int numberOfQuestions) {
        this.context = context;
        this.playerList = playerList;
        this.numberOfQuestions = numberOfQuestions;
        this.uuidList = QuestionSet.getInstance(context).getRandomIdList(numberOfQuestions);
        PlayersList.getInstance().getPlayer(0).setCurrentPlayer(true);
    }

    public Question getNextQuestion() {
        if(uuidList.size() > 0) {
            String uuid = uuidList.get(0);
            uuidList.remove(0);
            currentQuestion = QuestionSet.getInstance(context).getQuestion(uuid);

            return currentQuestion;
        }
        return null;
    }
}
