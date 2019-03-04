package ru.cybernut.fiveseconds.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private static final String TAG = "Game";
    public static final int MAX_PLAYERS = 6;
    private static final int MAX_SOUND = 2;

    private Context context;
    private PlayersList playerList;
    private Player currentPlayer;
    private Question currentQuestion;
    private Question nextQuestion;
    private int numberOfQuestions;
    private int numberOfPlayers;
    private List<String> uuidList;
    private GUIUpdatable mainGameActivity;
    private SoundPool soundPool;
    private AssetManager assetManager;

    public Game(Context context, int numberOfQuestions, ArrayList<Integer> setIds, GUIUpdatable guiUpdatable)  {
        this.context = context;
        mainGameActivity = guiUpdatable;
        this.playerList = PlayersList.getInstance();
        this.numberOfQuestions = numberOfQuestions;
        this.numberOfPlayers = playerList.getNumberOfPlayers();
        this.uuidList = QuestionList.getInstance(context).getRandomIdList(numberOfQuestions, setIds);
        this.currentQuestion = getNextQuestion();
        this.nextQuestion = getNextQuestion();
        this.currentPlayer = playerList.getPlayer(0);
        assetManager = context.getAssets();
        soundPool = new SoundPool(MAX_SOUND, AudioManager.STREAM_MUSIC, 0);

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
            playSound(nextQuestion.getId().toString());
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

    private void playSound(String uuid) {
        String path = "en/" + uuid + ".mp3";
        AssetFileDescriptor assetFileDescriptor;
        try {
            assetFileDescriptor = assetManager.openFd(path);
        } catch (IOException e) {
            Log.e(TAG, "playSound: ", e);
            return;
        }
        Integer soundId = soundPool.load(assetFileDescriptor, 1);
        if (soundId!= null) {
            Log.i(TAG, "playSound: soundId=" + soundId);
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }
}
