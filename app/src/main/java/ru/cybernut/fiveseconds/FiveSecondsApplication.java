package ru.cybernut.fiveseconds;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

public class FiveSecondsApplication extends Application {

    public static String TAG = "FiveSecondsApp";

    public static final String PREF_LANGUAGE = "lang";
    public static final String PREF_DEFAULT_GAME_TYPE = "default_game_type";
    public static final String PREF_ADD_TIME_VALUE = "additional_time_value";
    public static final String PREF_DEFAULT_NUMBER_OF_QUESTIONS = "default_number_of_questions";
    public static final int DEFAULT_ADDITIONAL_TIME_VALUE = 2;
    public static final int DEFAULT_NUMBER_OF_QUESTIONS = 5;


    private static final String SOUNDS_FOLDER = "/Sounds/";
    private static final String[] supportLanguages = new String[] {"en", "ru", "es"};
    private static final String defaultLanguage = "en";
    private static String language;
    private static String soundFolderPath;
    private static String externalFilesDirPath;
    private static Context appContext;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: " + language);
        super.onCreate();
        appContext = getApplicationContext();
        externalFilesDirPath = getExternalFilesDir(null).toString() ;
        loadLocaleSetting();
    }

    private static void updateLocaleSettings() {
        Log.i(TAG, "updateLocaleSettings: " + language);
        language = getLocale();
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
}
