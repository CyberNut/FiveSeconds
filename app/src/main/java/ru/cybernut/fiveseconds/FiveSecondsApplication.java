package ru.cybernut.fiveseconds;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.Locale;

public class FiveSecondsApplication extends Application {

    private static final String SOUNDS_FOLDER = "/Sounds/";
    private static final String[] supportLanguages = new String[] {"en", "ru", "es"};
    private static final String defaultLanguage = "en";
    private static String language;
    private static String soundFolderPath;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        language = getLocale();
        soundFolderPath = getExternalFilesDir(null) + SOUNDS_FOLDER + language + "/";
        appContext = getApplicationContext();
    }

    public static String getLanguage() {
        return language;
    }

    public static String getSoundFolderPath() {
        return soundFolderPath;
    }

    private static String getLocale() {
        String currentLanguage = Locale.getDefault().getLanguage();

        if( Arrays.binarySearch(FiveSecondsApplication.supportLanguages, currentLanguage) > 0) {
            return currentLanguage;
        } else {
            return defaultLanguage;
        }
    }

    public static Context getAppContext() {
        return appContext;
    }
}
