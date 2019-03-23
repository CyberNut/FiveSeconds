package ru.cybernut.fiveseconds.model;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

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
    private static final long ADDITION_TIME_DURATION = 2000; //ms
    private static final long TICK_DURATION = 200; //ms

    private Context appContext;
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
    private CountDownTimer gameTimer;
    private MediaPlayer mediaPlayer;

    private Updatable viewModel;

    public GameEngine(Context context, Updatable viewModel, int gameType, int numberOfQuestions) {
        this.appContext = context;
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
            return QuestionList.getInstance(appContext).getQuestion(uuid);
        } else {
            isGameOver = true;
        }
        return null;
    }

    private Integer prepareNextSound(String uuid) {
        Integer id = null;
        final String path = FiveSecondsApplication.getSoundFolderPath() + uuid + ".mp3";
        Log.i(TAG, "prepareNextSound: getGameReady = " + isGameReady);
        try {
            id = soundPool.load(path, 1);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "playSound: ", e);
            isGameReady = true;
            viewModel.initDone();
        }
        return id;
    }

    public void playCurrentSound() {
        if (currentSoundId != null) {
            soundPool.play(currentSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
        //mediaPlayer.start();
    }

    public void nextTurn() {
        if (gameType == GAME_TYPE_AUTO_PLAY_SOUND) {
            playCurrentSound();
        }
        gameTimer.start();
        nextTurnTaskStart();
    }

    public void nextTurnTaskStart() {
        new GameTurnTask().execute();
    }

    private boolean getGameReady() {
        return isMediaPlayerReady && isSoundPoolReady;
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Log.i(TAG, "onLoadComplete: sampleId =" + sampleId);
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
        gameTimer = initializeGameTimer();
        Log.i(TAG, "onPrepared: duration" + currentSoundDuration);
        if (!isMediaPlayerReady) {
            isMediaPlayerReady = true;
        }
        isGameReady = getGameReady();
        if(isGameReady) {
            viewModel.initDone();
        }
    }

    private class GameInitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                //TODO: delay imitation
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "doInBackground: ", e);
                return null;
            }
            uuidList = QuestionList.getInstance(appContext).getRandomIdList(numberOfQuestions, setIds);
            currentQuestion = getNextQuestion();
            if (isGameOver) {
                viewModel.gameOver();
            }
            if (gameType == GAME_TYPE_AUTO_PLAY_SOUND) {
                currentSoundId = prepareNextSound(currentQuestion.getId().toString());
            } else if(gameType == GAME_TYPE_ADDITION_TIME_FOR_READING) {
                roundDuration = roundDuration + ADDITION_TIME_DURATION;
                viewModel.initDone();
            } else {
                viewModel.initDone();
            }
            return null;
        }

    }

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
        public void initDone();
        public void gameOver();
        public void progressUpdate();
        public void timerFinished();
    }

    private CountDownTimer initializeGameTimer() {
        long duration = roundDuration;
        if(gameType == GAME_TYPE_AUTO_PLAY_SOUND) {
            duration = duration + currentSoundDuration;
        }

        return new CountDownTimer(duration, TICK_DURATION) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "onTick: " + millisUntilFinished);
                if(!isPaused) {
                    if(gameType == GAME_TYPE_AUTO_PLAY_SOUND && millisUntilFinished >= roundDuration) {
                    } else {
                        viewModel.progressUpdate();
                    }
                }
            }
            @Override
            public void onFinish() {
                viewModel.timerFinished();
            }
        };
    }
}
