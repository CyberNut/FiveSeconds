package ru.cybernut.fiveseconds.view;

import ru.cybernut.fiveseconds.model.QuestionSet;

public class QuestionSetModel {

    private QuestionSet questionSet;
    private boolean isChecked;
    private boolean isAvailable;

    public QuestionSetModel(QuestionSet questionSet) {
        this.questionSet = questionSet;
        this.isAvailable = questionSet.getType().equalsIgnoreCase(QuestionSet.QuestionSetType.free.toString()) ? true : false;
        this.isChecked = isAvailable;
    }

    public QuestionSet getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        this.questionSet = questionSet;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return questionSet.getName();
    }

    public String getType() {
        return questionSet.getType();
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
