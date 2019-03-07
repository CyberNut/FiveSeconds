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

public class Game implements SoundPool.OnLoadCompleteListener {

    public enum GameType {AUTO_PLAY_SOUND, ADDITION_TIME_FOR_READING, MANUAL}

    private static final String TAG = "Game";
    public static final int MAX_PLAYERS = 6;
    private static final int MAX_SOUND = 5;

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
    private int currentSoundId;
    private int nextSoundId;
    private AssetManager assetManager;
    private GameType gameType;
    private boolean isGameOver = false;
    private volatile boolean isGameReady = false;

    public Game(Context context, GameType gameType, int numberOfQuestions)  {
        this.context = context;
        this.gameType = gameType;
        this.numberOfQuestions = numberOfQuestions;
        this.mainGameActivity = (GUIUpdatable) context;
        this.assetManager = context.getAssets();
        soundPool = new SoundPool(MAX_SOUND, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(this);
    }

    public void init(ArrayList<Integer> setIds) {
        GameInitTask gameInitTask = new GameInitTask();
        gameInitTask.execute(setIds);
    }

    public boolean isGameReady() {
        return isGameReady;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Question getNextQuestion() {
        if(uuidList.size() > 0) {
            String uuid = uuidList.get(0);
            uuidList.remove(0);
            return QuestionList.getInstance(context).getQuestion(uuid);
        }
        return null;
    }

    private class GameInitTask extends AsyncTask<ArrayList<Integer>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<Integer>... arrayLists) {
            ArrayList<Integer> setIds = arrayLists[0];
            playerList = PlayersList.getInstance();
            numberOfPlayers = playerList.getNumberOfPlayers();
            uuidList = QuestionList.getInstance(context).getRandomIdList(numberOfQuestions, setIds);
            currentQuestion = getNextQuestion();
            nextQuestion = getNextQuestion();
            currentPlayer = playerList.getPlayer(0);
            if(gameType == GameType.AUTO_PLAY_SOUND) {
                prepareNextSound(currentQuestion.getId().toString());
            } else {
                isGameReady = true;
            }

            if(uuidList.size() == 0 || currentQuestion == null || currentPlayer == null) {
                isGameReady = false;
            }

            return isGameReady;
        }
    }

    private class GameTurnTask extends AsyncTask<Void, Void, Question> {

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
        new GameTurnTask().execute();
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

    public interface Initializable {
        public void initDone();
    }

    private void prepareNextSound(String uuid) {
        //TODO need use language setting
        final String path = "en/" + uuid + ".mp3";
        AssetFileDescriptor assetFileDescriptor;
        Log.i(TAG, "prepareNextSound: isGameReady = " + isGameReady);
        try {
            assetFileDescriptor = assetManager.openFd(path);
            currentSoundId = soundPool.load(assetFileDescriptor, 1);
        } catch (IOException e) {
            Log.e(TAG, "playSound: ", e);
            isGameReady = true;
        }
        Log.i(TAG, "prepareNextSound: EXIT isGameReady = " + isGameReady);
    }

    public void playCurrentSound() {

        if (currentSoundId != 0) {
            soundPool.play(currentSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
            Log.i(TAG, "playCurrentSound: id=" + currentSoundId);
            prepareNextSound(nextQuestion.getId().toString());
        } else {
            Log.i(TAG, "playCurrentSound: FAIL id=" + currentSoundId);
        }

    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Log.i(TAG, "onLoadComplete: sampleId =" + sampleId);
        isGameReady = true;
        ((Initializable)context).initDone();
    }
}
