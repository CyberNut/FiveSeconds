package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import ru.cybernut.fiveseconds.databinding.MainSettingsFragmentBinding;

public class MainGameSettingsFragment extends Fragment {

    protected static final String TAG = "MainGameSettingsFragment";
    private final int DEFAULT_ADDITIONAL_TIME_VALUE = 2;
    private final int DEFAULT_NUMBER_OF_QUESTIONS = 5;

    private MainSettingsFragmentBinding binding;
    private Locale locale;
    private String lang;
    private Spinner languageSpinner;
    private Spinner gameTypeSpinner;
    private EditText addTimeEditText;
    private Boolean isNeedToShowNotice;
    private int languagePosition;
    private EditText numberOfQuestionEdittext;

    public static MainGameSettingsFragment newInstance() {
        return new MainGameSettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_settings_fragment, container, false);
        View v = binding.getRoot();
        binding.setNoticeVisibility(isNeedToShowNotice);
        prepareUI(v);
        loadSettings();
        return v;
    }

    private void prepareUI(View view) {
        languageSpinner = binding.languageSpinner;
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MainGameSettings", "onItemClick: position = " + position + "  id = " + id);
                languagePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gameTypeSpinner = binding.defaultGameTypeSpinner;
        addTimeEditText = binding.addTimeValueEdittext;
        numberOfQuestionEdittext = binding.numberOfQuestionEdittext;
    }

    public Boolean getNeedToShowNotice() {
        return isNeedToShowNotice;
    }

    public void setNeedToShowNotice(Boolean needToShowNotice) {
        isNeedToShowNotice = needToShowNotice;
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSettings();
    }

    private void loadSettings() {
        Context context = getActivity();
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //Language
            String currentLanguage = sharedPreferences.getString(FiveSecondsApplication.PREF_LANGUAGE, null);
            if ( currentLanguage != null) {
                String[] languages = getResources().getStringArray(R.array.entryvalues_lang);
                int searchResult = Arrays.binarySearch(languages, currentLanguage);
                languageSpinner.setSelection(searchResult);
            }
            //Default game type
            String gameType = sharedPreferences.getString(FiveSecondsApplication.PREF_DEFAULT_GAME_TYPE, null);
            if (gameType != null) {
                String[] gameTypes = getResources().getStringArray(R.array.game_type_list);
                int searchResult = Arrays.binarySearch(gameTypes, gameType);
                gameTypeSpinner.setSelection(searchResult);
            }
            //additional time
            int addTime = sharedPreferences.getInt(FiveSecondsApplication.PREF_ADD_TIME_VALUE, DEFAULT_ADDITIONAL_TIME_VALUE);
            addTimeEditText.setText(String.valueOf(addTime));
            //default number of questions
            int numberOfQuestions = sharedPreferences.getInt(FiveSecondsApplication.PREF_DEFAULT_NUMBER_OF_QUESTIONS, DEFAULT_NUMBER_OF_QUESTIONS);
            numberOfQuestionEdittext.setText(String.valueOf(numberOfQuestions));
        }
    }

    private void saveSettings() {
        Context context = getActivity();
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //Language
            String[] languages = getResources().getStringArray(R.array.entryvalues_lang);
            if(languages.length >= languagePosition) {
                lang = languages[languagePosition];
                sharedPreferences.edit().putString(FiveSecondsApplication.PREF_LANGUAGE, lang).apply();
                if (lang.equals("default")) {
                    lang = getResources().getConfiguration().locale.getCountry();
                }
                locale = new Locale(lang);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                context.getApplicationContext().getResources().updateConfiguration(config, null);
                FiveSecondsApplication.updateLanguage();
            }
            //Default game type
            sharedPreferences.edit().putString(FiveSecondsApplication.PREF_DEFAULT_GAME_TYPE, gameTypeSpinner.getSelectedItem().toString()).apply();
            //additional time
            sharedPreferences.edit().putInt(FiveSecondsApplication.PREF_ADD_TIME_VALUE, Integer.valueOf(addTimeEditText.getText().toString()));
            //default number of questions
            sharedPreferences.edit().putInt(FiveSecondsApplication.PREF_DEFAULT_NUMBER_OF_QUESTIONS, Integer.valueOf(numberOfQuestionEdittext.getText().toString()));
        }
    }
}

