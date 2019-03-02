package ru.cybernut.fiveseconds;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.Locale;

import ru.cybernut.fiveseconds.model.Question;
import ru.cybernut.fiveseconds.model.QuestionList;

public class StartFragment extends Fragment {

    private static final String TAG = "StartFragment";

    private ImageButton newGameButton;
    private ImageButton helpButton;

    public static StartFragment newInstance() {
        return new StartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.start_fragment, container, false);
        prepareUI(v);
        return v;
    }

    private void prepareUI(View view) {
        newGameButton = (ImageButton) view.findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = NewGameActivity.newIntent(getActivity());
                Log.i(TAG, Locale.getDefault().getLanguage());
                startActivity(intent);
            }
        });

        helpButton = (ImageButton) view.findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

}
