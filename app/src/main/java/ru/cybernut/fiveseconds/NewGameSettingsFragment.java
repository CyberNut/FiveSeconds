package ru.cybernut.fiveseconds;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import ru.cybernut.fiveseconds.model.PlayersList;


public class NewGameSettingsFragment extends Fragment {

    private static final int MAX_QUANTITY_OF_QUESTIONS = 20;
    private static final int MIN_QUANTITY_OF_QUESTIONS = 3;

    private int numberOfPlayers = 2;
    private SeekBar numberOfQuestionsSeekBar;
    private TextView numberOfQuestionsTextView;
    private ImageButton startNewGameButton;
    private OnGamePreparedListener onGamePreparedListener;

    public static NewGameSettingsFragment newInstance() {
        return new NewGameSettingsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onGamePreparedListener = (OnGamePreparedListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement OnGamePreparedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_settings_fragment, container, false);

        numberOfPlayers = PlayersList.getInstance().getNumberOfPlayers();
        numberOfQuestionsTextView = (TextView)v.findViewById(R.id.numberOfQuestions);

        startNewGameButton = (ImageButton)v.findViewById(R.id.startNewGameButton);
        startNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGamePreparedListener.onGamePrepared(numberOfPlayers);
            }
        });

        numberOfQuestionsSeekBar = (SeekBar)v.findViewById(R.id.numberOfQuestionsSeekBar);
        numberOfQuestionsSeekBar.setMax(MAX_QUANTITY_OF_QUESTIONS);
        numberOfQuestionsSeekBar.setProgress(MIN_QUANTITY_OF_QUESTIONS);
        updateNumberOfQuestionsTextView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            numberOfQuestionsSeekBar.setMin(MIN_QUANTITY_OF_QUESTIONS);
        }
        numberOfQuestionsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateNumberOfQuestionsTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateNumberOfQuestionsTextView();
            }
        });
        return v;
    }

    public interface OnGamePreparedListener {
        public void onGamePrepared(int numberOfQuestions);
    }


    private void updateNumberOfQuestionsTextView() {
        numberOfQuestionsTextView.setText(String.valueOf(numberOfPlayers * numberOfQuestionsSeekBar.getProgress()));
    }


}