package ru.cybernut.fiveseconds;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import ru.cybernut.fiveseconds.model.Question;
import ru.cybernut.fiveseconds.model.QuestionSet;

public class StartFragment extends Fragment {

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
                startActivity(intent);
            }
        });

        helpButton = (ImageButton) view.findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: delete this before release
                QuestionSet questionSet = QuestionSet.getInstance(getActivity());
                questionSet.addQuestion(new Question("Name 3 Mathematical symbols"));
                questionSet.addQuestion(new Question("Name 3 countries you really want to visit"));
                questionSet.addQuestion(new Question("Name 3 dice games"));
                questionSet.addQuestion(new Question("Name 3 comedians"));
                questionSet.addQuestion(new Question("Name 3 talk show hosts"));
                questionSet.addQuestion(new Question("Name 3 breakfast foods"));
                questionSet.addQuestion(new Question("Name 3 deceased actresses"));
                questionSet.addQuestion(new Question("Name 3 movie theatre snacks"));
                questionSet.addQuestion(new Question("Name 3 sports not played with a ball"));
                questionSet.addQuestion(new Question("Name 3 types of jewellery"));
                questionSet.addQuestion(new Question("Name 3 famous TV detectives"));
                questionSet.addQuestion(new Question("Name 3 things to do with water"));
                questionSet.addQuestion(new Question("Name 3 sports played with a ball"));
                questionSet.addQuestion(new Question("Name 3 Pokemon"));
                questionSet.addQuestion(new Question("Name 3 things you can cut"));
                questionSet.addQuestion(new Question("Name 3 TV game or quiz shows"));
                questionSet.addQuestion(new Question("Name 3 apps on your phone"));
                questionSet.addQuestion(new Question("Name 3 airlines"));
                questionSet.addQuestion(new Question("Name 3 things that can cause death"));
                questionSet.addQuestion(new Question("Name 3 children's TV show"));
            }
        });
    }

}
