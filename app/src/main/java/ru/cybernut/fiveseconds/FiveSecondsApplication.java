package ru.cybernut.fiveseconds;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.security.auth.login.LoginException;

import ru.cybernut.fiveseconds.database.FiveSecondsDBSchema;

public class FiveSecondsApplication extends Application {

    public static String TAG = "FiveSecondsApp";

    public static final int MAX_QUANTITY_OF_ROUNDS = 20;
    public static final int MIN_QUANTITY_OF_ROUNDS = 3;
    public static final String PREF_LANGUAGE = "lang";
    public static final String PREF_DEFAULT_GAME_TYPE = "default_game_type";
    public static final String PREF_ADD_TIME_VALUE = "additional_time_value";
    public static final String PREF_DEFAULT_NUMBER_OF_ROUNDS = "default_number_of_rounds";
    public static final String PREF_PLAY_SOUND_AFTER_TIMER_ENDS = "play_sound_after_timer_ends";
    public static final String PREF_TIMER_DURATION = "default_timer_duration";
    public static final int DEFAULT_ADDITIONAL_TIME_VALUE = 2;
    public static final int DEFAULT_NUMBER_OF_ROUNDS = 5;
    public static final int DEFAULT_TIMER_DURATION = 5;
    public static final int MIN_TIMER_DURATION = 4;
    public static final int MAX_TIMER_DURATION = 7;
    public static final boolean DEFAULT_IS_NEED_PLAY_SOUND_AFTER_TIMER_ENDS = true;

    public static final String TIMER_FINISHED_FILENAME = "timer_finished.mp3";
    private static final String SOUNDS_FOLDER = "/Sounds/";
    private static final String[] supportLanguages = new String[] {"en", "ru"};
    private static final String defaultLanguage = "ru";
    private static String language;
    private static String soundFolderPath;
    private static String externalFilesDirPath;
    private static Context appContext;
    private static AssetFileDescriptor timerFinishedAssetFileDescriptor;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: " + language);
        super.onCreate();
        appContext = getApplicationContext();
        externalFilesDirPath = getExternalFilesDir(null).toString() ;
        loadLocaleSetting();
        initStaticSound();

    }

    private void initStaticSound() {
        try {
            timerFinishedAssetFileDescriptor = getAssets().openFd(TIMER_FINISHED_FILENAME);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private static void updateLocaleSettings() {
        Log.i(TAG, "updateLocaleSettings: " + language);
        language = getLocale();
        if (!Arrays.asList(supportLanguages).contains(language)) {
            language = defaultLanguage;
        }
        //TODO: this fix for initialize static fields
        String temp = FiveSecondsDBSchema.QuestionSetsTable.Cols.NAME;
        temp = FiveSecondsDBSchema.QuestionsTable.Cols.QUESTION_TEXT;
        soundFolderPath = externalFilesDirPath + SOUNDS_FOLDER + language + "/";
    }

    public static String getLanguage() {
        Log.i(TAG, "getLanguage: " + language);
        return language;
    }

    public static String getSoundFolderPath() {
        return soundFolderPath;
    }

    private void loadLocaleSetting() {
        Log.i(TAG, "loadLocaleSetting: " + language);
        language = getLocale();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        updateLanguage();
    }

    private static String getLocale() {
        Log.i(TAG, "getLocale: " + language);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        String lang = sharedPreferences.getString(PREF_LANGUAGE, "default");
        if (lang.equals("default")) {
            lang = appContext.getResources().getConfiguration().locale.getLanguage();
        }
        return lang;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged: " + language);
        super.onConfigurationChanged(newConfig);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, appContext.getResources().getDisplayMetrics());
    }

    public static void updateLanguage() {
        Log.i(TAG, "updateLanguage: " + language);
        updateLocaleSettings();
    }

    public static Context getAppContext() {
        Log.i(TAG, "getAppContext: " + language);
        return appContext;
    }

    public static AssetFileDescriptor getTimerFinishedAssetFileDescriptor() {
        return timerFinishedAssetFileDescriptor;
    }
}
