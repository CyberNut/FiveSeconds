package ru.cybernut.fiveseconds.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private static final String TAG = "Game";
    public static final int MAX_PLAYERS = 6;

    private Context context;
    private PlayersList playerList;
    private Player currentPlayer;
    private Question currentQuestion;
    private Question nextQuestion;
    private int numberOfQuestions;
    private int numberOfPlayers;
    List<String> uuidList;
    GUIUpdatable mainGameActivity;

    public Game(Context context, int numberOfQuestions, ArrayList<Integer> setIds, GUIUpdatable guiUpdatable)  {
        this.context = context;
        mainGameActivity = guiUpdatable;
        this.playerList = PlayersList.getInstance();
        this.numberOfQuestions = numberOfQuestions;
        this.numberOfPlayers = playerList.getNumberOfPlayers();
        this.uuidList = QuestionList.getInstance(context).getRandomIdList(numberOfQuestions, setIds);
        this.currentQuestion = getNextQuestion();
        this.currentPlayer = playerList.getPlayer(0);
    }

    public Question getNextQuestion() {
        if(uuidList.size() > 0) {
            String uuid = uuidList.get(0);
            uuidList.remove(0);
            return QuestionList.getInstance(context).getQuestion(uuid);
        }
        return null;
    }

    private class GameTask extends AsyncTask<Void, Void, Question> {

        @Override
        protected Question doInBackground(Void... voids) {
            return getNextQuestion();
        }

        @Override
        protected void onPostExecute(Question question) {
            currentQuestion = nextQuestion;
            nextQuestion = question;
            mainGameActivity.update();
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void nextTurn(boolean isCorrectAnswer) {

        if(isCorrectAnswer) {
            currentPlayer.increaseScore();
        }
        int currentPlayerId = playerList.getId(currentPlayer);
        currentPlayer = playerList.getPlayer(getNextId(currentPlayerId));
        Log.i(TAG, "nextTurn: ");        
        new GameTask().execute();
    }

    private int getNextId(int id) {
        if(id >= (numberOfPlayers - 1)) {
            return 0;
        } else {
            return ++id;
        }
    }

    public interface GUIUpdatable {
        public void update();
    }
}
