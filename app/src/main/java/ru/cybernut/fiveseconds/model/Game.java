package ru.cybernut.fiveseconds.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import ru.cybernut.fiveseconds.MainGameActivity;

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

    public Game(Context context, int numberOfQuestions, GUIUpdatable guiUpdatable)  {
        this.context = context;
        mainGameActivity = guiUpdatable;
        this.playerList = PlayersList.getInstance();
        this.numberOfQuestions = numberOfQuestions;
        this.numberOfPlayers = playerList.getNumberOfPlayers();
        this.uuidList = QuestionSet.getInstance(context).getRandomIdList(numberOfQuestions);
        this.currentQuestion = getNextQuestion();
        this.currentPlayer = playerList.getPlayer(0);
    }

    public Question getNextQuestion() {
        if(uuidList.size() > 0) {
            String uuid = uuidList.get(0);
            uuidList.remove(0);
            return QuestionSet.getInstance(context).getQuestion(uuid);
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
        mainGameActivity.update();
    }

    private int getNextId(int id) {
        if(id == numberOfPlayers) {
            return 1;
        } else {
            return id++;
        }
    }

    public interface GUIUpdatable {
        public void update();
    }
}
