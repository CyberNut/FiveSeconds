package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.Locale;

import ru.cybernut.fiveseconds.databinding.MainSettingsFragmentBinding;

public class MainGameSettingsFragment extends Fragment {

    protected static final String TAG = "MainGameSettingsFragment";
    private static final String PREFERENCES = "preferences";
    private static final String PREF_LANGUAGE = "lang";

    private MainSettingsFragmentBinding binding;
    private Locale locale;
    private String lang;
    private Spinner languageSpinner;
    private Spinner gameTypeSpinner;

    public static MainGameSettingsFragment newInstance() {
        return new MainGameSettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_settings_fragment, container, false);
        View v = binding.getRoot();
        prepareUI(v);
        return v;
    }

    private void prepareUI(View view) {
        languageSpinner = binding.languageSpinner;
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MainGameSettings", "onItemClick: position = " + position + "  id = " + id);
                Context context = getActivity();
                if (context != null) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String[] languages = getResources().getStringArray(R.array.entryvalues_lang);
                    if(languages.length >= position) {
                        lang = languages[position];
                        sharedPreferences.edit().putString(PREF_LANGUAGE, lang).apply();
                        if (lang.equals("default")) {
                            lang = getResources().getConfiguration().locale.getCountry();
                        }
                        locale = new Locale(lang);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        context.getApplicationContext().getResources().updateConfiguration(config, null);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gameTypeSpinner = binding.defaultGameTypeSpinner;
    }

}
