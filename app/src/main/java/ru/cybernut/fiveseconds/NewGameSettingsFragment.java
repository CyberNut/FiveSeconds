package ru.cybernut.fiveseconds;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.view.RoundedSquareProgressView;


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

        numberOfQuestionsTextView = (TextView)v.findViewById(R.id.numberOfQuestions);
        numberOfPlayers = PlayersList.getInstance().getNumberOfPlayers();

        startNewGameButton = (ImageButton)v.findViewById(R.id.startNewGameButton);
        startNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfQuestions = MIN_QUANTITY_OF_QUESTIONS * numberOfPlayers + numberOfPlayers * numberOfQuestionsSeekBar.getProgress();
                if (numberOfQuestions <= MIN_QUANTITY_OF_QUESTIONS || numberOfPlayers < 2) {
                    Toast.makeText(getActivity(), R.string.incorrect_number_of_questions, Toast.LENGTH_SHORT).show();
                } else {
                    onGamePreparedListener.onGamePrepared(numberOfQuestions);
                }
            }
        });

        numberOfQuestionsSeekBar = (SeekBar)v.findViewById(R.id.numberOfQuestionsSeekBar);
        numberOfQuestionsSeekBar.setMax(MAX_QUANTITY_OF_QUESTIONS - MIN_QUANTITY_OF_QUESTIONS);
        numberOfQuestionsSeekBar.setProgress(numberOfPlayers);
        updateNumberOfQuestionsTextView();
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
        numberOfPlayers = PlayersList.getInstance().getNumberOfPlayers();
        numberOfQuestionsTextView.setText(String.valueOf(MIN_QUANTITY_OF_QUESTIONS * numberOfPlayers +  numberOfPlayers * numberOfQuestionsSeekBar.getProgress()));
    }


}
