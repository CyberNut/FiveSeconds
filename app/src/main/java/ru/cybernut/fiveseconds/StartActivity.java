package ru.cybernut.fiveseconds;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import ru.cybernut.fiveseconds.model.Question;
import ru.cybernut.fiveseconds.model.QuestionSet;

public class StartActivity extends SingleFragmentFullScreenActivity {

    @Override
    protected Fragment createFragment() {
        return StartFragment.newInstance();
    }

    public void onPlayButtonClick(View view) {
        Intent intent = NewGameActivity.newIntent(this);
        startActivity(intent);
    }

    public void onHelpButtonClick(View view) {
        QuestionSet questionSet = QuestionSet.getInstance(this);
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
}
