package ru.cybernut.fiveseconds;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ru.cybernut.fiveseconds.databinding.MainSettingsFragmentBinding;
import ru.cybernut.fiveseconds.model.QuestionSet;
import ru.cybernut.fiveseconds.model.QuestionSetList;

public class MainGameSettingsFragment extends Fragment {

    protected static final String TAG = "MainGameSettFragment";

    private MainSettingsFragmentBinding binding;
    private Locale locale;
    private String lang;
    private Spinner languageSpinner;
    private Spinner gameTypeSpinner;
    private EditText addTimeEditText;
    private Boolean isNeedToShowNotice;
    private int languagePosition;
    private EditText numberOfQuestionEdittext;
    private int languagePositionFromPref;
    private Button deleteSoundsFilesButton;
    private ProgressDialog progressDialog;
    private DeleteSoundFilesTask deleteSoundFilesTask;

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
        //save language from preferences
        languagePositionFromPref = languageSpinner.getSelectedItemPosition();
        return v;
    }

    private void prepareUI(View view) {
        languageSpinner = binding.languageSpinner;
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MainGameSettings", "onItemClick: position = " + position + "  id = " + id);
                languagePosition = position;
                if (languagePositionFromPref != position) {
                    binding.noticeLabel.setVisibility(View.VISIBLE);
                } else {
                    binding.noticeLabel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gameTypeSpinner = binding.defaultGameTypeSpinner;
        addTimeEditText = binding.addTimeValueEdittext;
        numberOfQuestionEdittext = binding.numberOfQuestionEdittext;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.deleting_sounds_dialog_title));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
            deleteSoundFilesTask.cancel(true); //cancel the task
            }
        });

        deleteSoundsFilesButton = binding.deleteSoundsFilesButton;
        deleteSoundsFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteSoundsFilesDialog();
            }
        });
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
        if (languageSpinner.getSelectedItemPosition() != languagePositionFromPref) {
            openQuitDialog();
        }
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
            int addTime = sharedPreferences.getInt(FiveSecondsApplication.PREF_ADD_TIME_VALUE, FiveSecondsApplication.DEFAULT_ADDITIONAL_TIME_VALUE);
            addTimeEditText.setText(String.valueOf(addTime));
            //default number of questions
            int numberOfQuestions = sharedPreferences.getInt(FiveSecondsApplication.PREF_DEFAULT_NUMBER_OF_QUESTIONS, FiveSecondsApplication.DEFAULT_NUMBER_OF_QUESTIONS);
            numberOfQuestionEdittext.setText(String.valueOf(numberOfQuestions));
        }
    }

    private void saveSettings() {
        Context context = getActivity();
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.edit()
                //Default game type
                .putString(FiveSecondsApplication.PREF_DEFAULT_GAME_TYPE, gameTypeSpinner.getSelectedItem().toString())
                //additional time
                .putInt(FiveSecondsApplication.PREF_ADD_TIME_VALUE, Integer.valueOf(addTimeEditText.getText().toString()))
                //default number of questions
                .putInt(FiveSecondsApplication.PREF_DEFAULT_NUMBER_OF_QUESTIONS, Integer.valueOf(numberOfQuestionEdittext.getText().toString()))
                .apply();
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
        }
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                getActivity());
        quitDialog.setTitle(R.string.change_language_dialog_title);

        quitDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                System.exit(0);
            }
        });

        quitDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }

    private void openDeleteSoundsFilesDialog() {
        AlertDialog.Builder deleteSoundFilesDialog = new AlertDialog.Builder(
                getActivity());
        deleteSoundFilesDialog.setTitle(getString(R.string.delete_sounds_files) + "?");

        deleteSoundFilesDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            new DeleteSoundFilesTask(getActivity()).execute();
            }
        });

        deleteSoundFilesDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        deleteSoundFilesDialog.show();
    }

    private class DeleteSoundFilesTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Context> contextRef;

        public DeleteSoundFilesTask(Context context) {
            this.contextRef = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String filePath = FiveSecondsApplication.getSoundFolderPath();
            File file = new File(filePath);
            if (!isCancelled()) {
                if (file.exists()) {
                    File[] files = file.listFiles();
                    for (File currentFile : files) {
                        deleteFile(currentFile);
                    }
                }

                QuestionSetList questionSetList = QuestionSetList.getInstance();
                List<QuestionSet> list = questionSetList.getQuestionSets();
                for (QuestionSet questionSet : list) {
                    questionSet.setSoundsLoaded(false);
                    questionSetList.updateQuestionSet(questionSet);
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (contextRef.get() != null) {
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (contextRef.get() != null) {
                progressDialog.dismiss();
            }
        }

        private void deleteFile(File file) {
            if (!file.exists()) {
                return;
            }
            if (file.isDirectory()) {
                for (File temp : file.listFiles()) {
                    deleteFile(temp);
                }
                file.delete();
            } else {
                file.delete();
            }
        }
    }
}

