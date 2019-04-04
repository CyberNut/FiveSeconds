package ru.cybernut.fiveseconds;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.Locale;

public class FiveSecondsApplication extends Application {

    public static final String PREF_LANGUAGE = "lang";
    public static final String PREF_DEFAULT_GAME_TYPE = "default_game_type";
    public static final String PREF_ADD_TIME_VALUE = "additional_time_value";
    public static final String PREF_DEFAULT_NUMBER_OF_QUESTIONS = "default_number_of_questions";

    private static final String SOUNDS_FOLDER = "/Sounds/";
    private static final String[] supportLanguages = new String[] {"en", "ru", "es"};
    private static final String defaultLanguage = "en";
    private static String language;
    private static String soundFolderPath;
    private static String externalFilesDirPath;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        externalFilesDirPath = getExternalFilesDir(null).toString() ;
        loadLocaleSetting();
    }

    private static void updateLocaleSettings() {

        language = getLocale();
        soundFolderPath = externalFilesDirPath + SOUNDS_FOLDER + language + "/";
    }

    public static String getLanguage() {
        return language;
    }

    public static String getSoundFolderPath() {
        return soundFolderPath;
    }

    private void loadLocaleSetting() {
        language = getLocale();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        appContext.getApplicationContext().getResources().updateConfiguration(config, null);
        updateLanguage();
    }

    private static String getLocale() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        String currentLanguage = sharedPreferences.getString(PREF_LANGUAGE, null);
        if(currentLanguage == null) {
            currentLanguage = Locale.getDefault().getLanguage();
        }

        if( Arrays.binarySearch(FiveSecondsApplication.supportLanguages, currentLanguage) > 0) {
            return currentLanguage;
        } else {
            return defaultLanguage;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        appContext.get
        getBaseContext().getResources().updateConfiguration(config, null);

    }

    public static void updateLanguage() {
        updateLocaleSettings();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
