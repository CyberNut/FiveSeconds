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

    public enum GameType {AUTO_PLAY_SOUND, ADDITION_TIME_FOR_READING, MANUAL}

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
    private int currentSoundId;
    private int nextSoundId;
    private AssetManager assetManager;
    private GameType gameType;
    private boolean isGameOver = false;
    private boolean isGameReady = false;

    public Game(Context context, GUIUpdatable guiUpdatable, GameType gameType)  {
        this.context = context;
        this.gameType = gameType;
        this.mainGameActivity = guiUpdatable;
    }

    public boolean init(int numberOfQuestions, ArrayList<Integer> setIds) {
        this.numberOfQuestions = numberOfQuestions;
        this.playerList = PlayersList.getInstance();
        this.numberOfPlayers = playerList.getNumberOfPlayers();
        this.uuidList = QuestionList.getInstance(context).getRandomIdList(numberOfQuestions, setIds);
        this.currentQuestion = getNextQuestion();
        this.nextQuestion = getNextQuestion();
        this.currentPlayer = playerList.getPlayer(0);
        assetManager = context.getAssets();
        soundPool = new SoundPool(MAX_SOUND, AudioManager.STREAM_MUSIC, 0);

        if(gameType == GameType.AUTO_PLAY_SOUND) {

        } else {
            isGameReady = true;
        }

        if(uuidList.size() == 0 || currentQuestion == null || currentPlayer == null) {
            return false;
        }

        return true;
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

    private void prepareNextSound(String uuid) {
        //TODO need use language setting
        String path = "en/" + uuid + ".mp3";
        AssetFileDescriptor assetFileDescriptor;
        try {
            assetFileDescriptor = assetManager.openFd(path);
            Integer soundId = soundPool.load(assetFileDescriptor, 1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if(status == 0) {
                        Log.i(TAG, "onLoadComplete: " + sampleId);
                        nextSoundId = sampleId;
                        soundPool.unload(currentSoundId);
                        currentSoundId = 0;
                    }
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "playSound: ", e);
            return;
        }
    }

    private void playCurrentSound() {
            if(currentSoundId!=0) {
                soundPool.play(currentSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
                prepareNextSound(nextQuestion.getId().toString());
            }
        }
    }
}
