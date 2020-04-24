package ru.cybernut.fiveseconds;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.lang.ref.WeakReference;
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
    private AppCompatCheckBox isNeedPlaySoundCheckBox;
    private EditText addTimeEditText;
    private Boolean isNeedToShowNotice;
    private int languagePosition;
    private EditText numberOfRoundsEdittext;
    private int languagePositionFromPref;
    private ImageButton deleteSoundsFilesButton;
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
        numberOfRoundsEdittext = binding.numberOfQuestionEdittext;
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

        isNeedPlaySoundCheckBox = binding.playSoundAfterTimeDoneCheckBox;

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
                String[] languages = getResources().getStringArray(R.array.language_list_values);
                int searchResult = Arrays.binarySearch(languages, currentLanguage);
                languageSpinner.setSelection(searchResult);
            }
            //Default game type
            int gameType = sharedPreferences.getInt(FiveSecondsApplication.PREF_DEFAULT_GAME_TYPE, 0);
            gameTypeSpinner.setSelection(gameType);
            //additional time
            int addTime = sharedPreferences.getInt(FiveSecondsApplication.PREF_ADD_TIME_VALUE, FiveSecondsApplication.DEFAULT_ADDITIONAL_TIME_VALUE);
            addTimeEditText.setText(String.valueOf(addTime));
            //default number of questions
            int numberOfRounds = sharedPreferences.getInt(FiveSecondsApplication.PREF_DEFAULT_NUMBER_OF_ROUNDS, FiveSecondsApplication.DEFAULT_NUMBER_OF_ROUNDS);
            numberOfRoundsEdittext.setText(String.valueOf(numberOfRounds));
            isNeedPlaySoundCheckBox.setChecked(sharedPreferences.getBoolean(FiveSecondsApplication.PREF_PLAY_SOUND_AFTER_TIMER_ENDS, FiveSecondsApplication.DEFAULT_IS_NEED_PLAY_SOUND_AFTER_TIMER_ENDS));
        }
    }

    private void saveSettings() {
        Context context = getActivity();
        if (context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            int numberOfRounds = 0;
            int addTimeValue = 0;
            try {
                numberOfRounds = Integer.valueOf(numberOfRoundsEdittext.getText().toString());
                if (numberOfRounds < FiveSecondsApplication.MIN_QUANTITY_OF_ROUNDS) {
                    numberOfRounds = FiveSecondsApplication.MIN_QUANTITY_OF_ROUNDS;
                } else if(numberOfRounds > FiveSecondsApplication.MAX_QUANTITY_OF_ROUNDS) {
                    numberOfRounds = FiveSecondsApplication.MAX_QUANTITY_OF_ROUNDS;
                }
            } catch (NumberFormatException e) {
                numberOfRounds = FiveSecondsApplication.MIN_QUANTITY_OF_ROUNDS;
            }
            try {
                addTimeValue = Integer.valueOf(addTimeEditText.getText().toString());
            } catch (NumberFormatException e) {
                addTimeValue = FiveSecondsApplication.DEFAULT_ADDITIONAL_TIME_VALUE;
            }
            sharedPreferences.edit()
                //Default game type
                .putInt(FiveSecondsApplication.PREF_DEFAULT_GAME_TYPE, gameTypeSpinner.getSelectedItemPosition())
                //additional time
                .putInt(FiveSecondsApplication.PREF_ADD_TIME_VALUE, addTimeValue)
                //default number of questions
                .putInt(FiveSecondsApplication.PREF_DEFAULT_NUMBER_OF_ROUNDS, numberOfRounds)
                .putBoolean(FiveSecondsApplication.PREF_PLAY_SOUND_AFTER_TIMER_ENDS, isNeedPlaySoundCheckBox.isChecked())
                .apply();
            //Language
            String[] languages = getResources().getStringArray(R.array.language_list_values);
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
                Log.i(TAG, "saveSettings: before updateConfConfig");
                context.getApplicationContext().getResources().updateConfiguration(config, null);
                Log.i(TAG, "saveSettings: before updateLanguage");
                FiveSecondsApplication.updateLanguage();
            }
        }
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                getActivity(), R.style.DialogTheme);
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
                getActivity(), R.style.DialogTheme);
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
            Toast.makeText(getActivity(), R.string.sound_files_deleted_successfully, Toast.LENGTH_SHORT).show();
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

