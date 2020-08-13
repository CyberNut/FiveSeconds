package ru.cybernut.fiveseconds.model;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
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
    private static final long TICK_DURATION = 100; //ms

    private int gameType;
    private int numberOfQuestions;
    private boolean isNeedPlaySound;
    private Question currentQuestion;
    private Integer playableNowSoundId;
    private Integer currentSoundId;
    private Integer timerFinishedSoundId;
    private int currentSoundDuration = 0;
    private List<String> uuidList;
    private SoundPool soundPool;
    private List<Question> questions;
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

    public GameEngine(Updatable viewModel, int gameType, int numberOfQuestions, boolean isNeedPlaySound) {
        this.viewModel = viewModel;
        this.gameType = gameType;
        this.isNeedPlaySound = isNeedPlaySound;
        this.numberOfQuestions = numberOfQuestions;
        this.soundPool = new SoundPool(MAX_SOUND, AudioManager.STREAM_MUSIC, 0);
        this.mediaPlayer = new MediaPlayer();
        questions = new ArrayList<>();
    }

    public void initialize(ArrayList<Integer> setIds) {
        this.setIds = setIds;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FiveSecondsApplication.getAppContext());
        roundDuration = sharedPreferences.getInt(FiveSecondsApplication.PREF_TIMER_DURATION, FiveSecondsApplication.DEFAULT_TIMER_DURATION) * 1000;
        soundPool.setOnLoadCompleteListener(this);
        mediaPlayer.setOnPreparedListener(this);
        new GameInitTask().execute();
        if (gameType != GAME_TYPE_AUTO_PLAY_SOUND) {
            gameTimer = initializeGameTimer();
        }
        if (gameType == GAME_TYPE_ADDITION_TIME_FOR_READING) {
            additionalTime = sharedPreferences.getInt(FiveSecondsApplication.PREF_ADD_TIME_VALUE, FiveSecondsApplication.DEFAULT_ADDITIONAL_TIME_VALUE) * 1000;
            roundDuration = roundDuration + additionalTime;
        }
    }

    public void destroy() {
        if(gameTimer != null) {
            gameTimer.cancel();
            gameTimer = null;
        }
        soundPool.release();
        mediaPlayer.release();
        viewModel = null;
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
            QuestionList.getInstance().increaseNumberOfUsage(uuid);
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
            playableNowSoundId = currentSoundId;
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
            if(soundPool != null && playableNowSoundId != null && playableNowSoundId != 0) {
                soundPool.stop(playableNowSoundId);
            }
        }
    }

    public void resume() {
        isPaused = false;
        if(gameTimer != null) {
            gameTimer.resume();
        }
        if(soundPool !=null && playableNowSoundId != null && playableNowSoundId != 0) {
            soundPool.resume(playableNowSoundId);
        }
    }

    private void initStaticSounds() {
        if (!isNeedPlaySound) {
            return;
        }
        AssetFileDescriptor fileDescriptor = FiveSecondsApplication.getTimerFinishedAssetFileDescriptor();
        if (fileDescriptor != null) {
            try {
                timerFinishedSoundId = soundPool.load(fileDescriptor, 1);
            } catch (RuntimeException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void playTimerFinishedSound() {
        if(timerFinishedSoundId != null) {
            soundPool.play(timerFinishedSoundId, 0.75f, 0.75f, 1, 0, 1.0f);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GameInitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            uuidList = QuestionList.getInstance().getRandomIdList(numberOfQuestions, setIds);
            questions = QuestionList.getInstance().getQuestions();
            currentQuestion = getNextQuestion();
            if (isGameOver) {
                viewModel.gameOver();
            }
            initStaticSounds();
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
                    playableNowSoundId = 0;
                    if(viewModel != null) {
                        viewModel.progressUpdate(100 - ((millisUntilFinished * 100 / roundDuration)));
                    }
                }
            }
        }

        @Override
        public void onFinish() {
            if(viewModel != null) {
                viewModel.timerFinished();
            }
            playTimerFinishedSound();
        }
    }

}
