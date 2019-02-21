package ru.cybernut.fiveseconds;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.cybernut.fiveseconds.model.Game;
import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.model.Question;

public class GameFragment extends Fragment {

    private static final String NUMBER_OF_QUESTIONS_KEY = "NUMBER_OF_QUESTIONS_KEY";

    private Game game;
    private int numberOfQuestions;
    private ImageButton nextQuestionButton;
    private TextView questionTextView;

    public GameFragment() {
    }

    public static GameFragment newInstance(int numberOfQuestions) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putInt("NUMBER_OF_QUESTIONS_KEY", numberOfQuestions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numberOfQuestions = getArguments().getInt("NUMBER_OF_QUESTIONS_KEY", 0);
        game = new Game(getActivity(), PlayersList.getInstance().getList(), numberOfQuestions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.game_fragment, container, false);

        questionTextView = (TextView)v.findViewById(R.id.question_text);
        nextQuestionButton = (ImageButton)v.findViewById(R.id.nextQuestionButton);
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question question = game.getNextQuestion();
                if(question != null) {
                    questionTextView.setText(question.getText());
                }
            }
        });
        return v;
    }
}
