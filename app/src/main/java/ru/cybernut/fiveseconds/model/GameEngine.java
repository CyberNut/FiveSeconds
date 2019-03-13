package ru.cybernut.fiveseconds.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameEngine implements SoundPool.OnLoadCompleteListener {

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
    private List<String> uuidList;
    private SoundPool soundPool;
    private AssetManager assetManager;
    private ArrayList<Integer> setIds;
    private boolean isGameOver = false;
    private boolean isGameReady = false;
    private boolean isPaused = false;
    private long roundDuration;
    private CountDownTimer gameTimer;

    private Updatable viewModel;

    public GameEngine(Context context, Updatable viewModel, int gameType, int numberOfQuestions) {
        this.appContext = context;
        this.viewModel = viewModel;
        this.gameType = gameType;
        this.assetManager = context.getAssets();
        this.numberOfQuestions = numberOfQuestions;
        this.roundDuration = STANDART_ROUND_DURATION;
        this.soundPool = new SoundPool(MAX_SOUND, AudioManager.STREAM_MUSIC, 0);
    }

    public void initialize(ArrayList<Integer> setIds) {
        this.setIds = setIds;
        soundPool.setOnLoadCompleteListener(this);
        gameTimer = new CountDownTimer(roundDuration, TICK_DURATION) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isPaused) {
                    viewModel.progressUpdate();
                }
            }
            @Override
            public void onFinish() {
                viewModel.timerFinished();
            }
        };
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
        //TODO need use language setting
        final String path = "en/" + uuid + ".mp3";
        AssetFileDescriptor assetFileDescriptor;
        Log.i(TAG, "prepareNextSound: isGameReady = " + isGameReady);
        try {
            assetFileDescriptor = assetManager.openFd(path);
            id = soundPool.load(assetFileDescriptor, 1);
        } catch (IOException e) {
            Log.e(TAG, "playSound: ", e);
            isGameReady = true;
            viewModel.initDone();
        }
        Log.i(TAG, "prepareNextSound: EXIT isGameReady = " + isGameReady);
        return id;
    }

    public void playCurrentSound() {
        if (currentSoundId != null) {
            soundPool.play(currentSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
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

    private class GameInitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                //TODO: delay imitation
                Thread.sleep(3000);
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
                currentSoundId = prepareNextSound(currentQuestion.getId().toString());
            }
            return null;
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Log.i(TAG, "onLoadComplete: sampleId =" + sampleId);
        if (!isGameReady) {
            isGameReady = true;
        }
        viewModel.initDone();
    }

    public interface Updatable {
        public void initDone();
        public void gameOver();
        public void progressUpdate();
        public void timerFinished();
    }
}
