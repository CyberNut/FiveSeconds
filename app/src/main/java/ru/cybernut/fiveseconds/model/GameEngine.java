package ru.cybernut.fiveseconds.model;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.FiveSecondsApplication;

public class GameEngine implements SoundPool.OnLoadCompleteListener, MediaPlayer.OnPreparedListener {

    public static int GAME_TYPE_AUTO_PLAY_SOUND = 0;
    public static int GAME_TYPE_ADDITION_TIME_FOR_READING = 1;
    public static int GAME_TYPE_MANUAL = 2;

    public static final int MAX_PLAYERS = 6;
    private static final String TAG = "GameEngine";
    private static final int MAX_SOUND = 5;
    private static final long STANDART_ROUND_DURATION = 5000; //ms
    private static final long TICK_DURATION = 100; //ms

    private int gameType;
    private int numberOfQuestions;
    private Question currentQuestion;
    private Integer currentSoundId;
    private int currentSoundDuration = 0;
    private List<String> uuidList;
    private SoundPool soundPool;
    private ArrayList<Integer> setIds;
    private volatile boolean isGameOver = false;
    private volatile boolean isGameReady = false;
    private volatile boolean isPaused = false;
    private boolean isSoundPoolReady = false;
    private boolean isMediaPlayerReady = false;
    private long roundDuration;
    private long additionalTime;
    private MediaPlayer mediaPlayer;
    private GameTimer gameTimer;

    private Updatable viewModel;

    public GameEngine(Updatable viewModel, int gameType, int numberOfQuestions) {
        this.viewModel = viewModel;
        this.gameType = gameType;
        this.numberOfQuestions = numberOfQuestions;
        this.roundDuration = STANDART_ROUND_DURATION;
        this.soundPool = new SoundPool(MAX_SOUND, AudioManager.STREAM_MUSIC, 0);
        this.mediaPlayer = new MediaPlayer();

    }

    public void initialize(ArrayList<Integer> setIds) {
        this.setIds = setIds;
        soundPool.setOnLoadCompleteListener(this);
        mediaPlayer.setOnPreparedListener(this);
        new GameInitTask().execute();
        if (gameType != GAME_TYPE_AUTO_PLAY_SOUND) {
            gameTimer = initializeGameTimer();
        }
        if (gameType == GAME_TYPE_ADDITION_TIME_FOR_READING) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FiveSecondsApplication.getAppContext());
            additionalTime = sharedPreferences.getInt(FiveSecondsApplication.PREF_ADD_TIME_VALUE, FiveSecondsApplication.DEFAULT_ADDITIONAL_TIME_VALUE) * 1000;
            roundDuration = roundDuration + additionalTime;
        }
    }

    public String getCurrentQuestionText() {
        if (currentQuestion != null) {
            return currentQuestion.getText();
        } else {
            return null;
        }
    }

    private Question getNextQuestion() {
        if(uuidList.size() > 0) {
            String uuid = uuidList.get(0);
            uuidList.remove(0);
            return QuestionList.getInstance().getQuestion(uuid);
        } else {
            isGameOver = true;
        }
        return null;
    }

    public int getGameType() {
        return gameType;
    }

    private Integer prepareNextSound(String uuid) {
        Integer id;
        isSoundPoolReady = false;
        isMediaPlayerReady = false;
        final String path = FiveSecondsApplication.getSoundFolderPath() + uuid + ".mp3";
        try {
            id = soundPool.load(path, 1);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            currentSoundDuration = FiveSecondsApplication.DEFAULT_ADDITIONAL_TIME_VALUE;
            id = null;
            isSoundPoolReady = true;
            isMediaPlayerReady = true;
            isGameReady = true;
            viewModel.initDone();
        }
        return id;
    }

    private void playCurrentSound() {
        if (currentSoundId != null) {
            soundPool.play(currentSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void nextTurn() {
        if (gameType == GAME_TYPE_AUTO_PLAY_SOUND) {
            playCurrentSound();
        }
        gameTimer = initializeGameTimer();
        gameTimer.start();
        nextTurnTaskStart();
    }

    private void nextTurnTaskStart() {
        new GameTurnTask().execute();
    }

    private boolean getGameReady() {
        return isMediaPlayerReady && isSoundPoolReady;
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if (!isSoundPoolReady) {
            isSoundPoolReady = true;
        }
        isGameReady = getGameReady();
        if(isGameReady) {
            viewModel.initDone();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        currentSoundDuration = mediaPlayer.getDuration();
        mediaPlayer.reset();

        if (!isMediaPlayerReady) {
            isMediaPlayerReady = true;
        }
        isGameReady = getGameReady();
        if(isGameReady) {
            viewModel.initDone();
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void pause() {
        isPaused = true;
        if(gameTimer!= null) {
            gameTimer.pause();
        }
    }

    public void resume() {
        isPaused = false;
        gameTimer.resume();
    }

    @SuppressLint("StaticFieldLeak")
    private class GameInitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            uuidList = QuestionList.getInstance().getRandomIdList(numberOfQuestions, setIds);
            currentQuestion = getNextQuestion();
            if (isGameOver) {
                viewModel.gameOver();
            }
            if (gameType == GAME_TYPE_AUTO_PLAY_SOUND) {
                currentSoundId = prepareNextSound(currentQuestion.getId().toString());
            } else {
                viewModel.initDone();
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GameTurnTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            currentQuestion = getNextQuestion();
            if (isGameOver) {
                viewModel.gameOver();
            }
            if (gameType == GAME_TYPE_AUTO_PLAY_SOUND) {
                if(currentQuestion!=null) {
                    currentSoundId = prepareNextSound(currentQuestion.getId().toString());
                }
            }
            return null;
        }
    }

    public interface Updatable {
        void initDone();
        void gameOver();
        void progressUpdate(long value);
        void timerFinished();
    }

    private GameTimer initializeGameTimer() {
        long duration = roundDuration;
        if(gameType == GAME_TYPE_AUTO_PLAY_SOUND) {
            duration = duration + currentSoundDuration;
        }
        return new GameTimer(duration, TICK_DURATION);
    }

    private class GameTimer extends ru.cybernut.fiveseconds.utils.CountDownTimer {

        GameTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(!isPaused) {
                if (gameType == GAME_TYPE_AUTO_PLAY_SOUND && millisUntilFinished >= roundDuration) {
                } else {
                    viewModel.progressUpdate( 100 - ((millisUntilFinished * 100 /roundDuration)));
                }
            }
        }

        @Override
        public void onFinish() {
            viewModel.timerFinished();
        }
    }
}
