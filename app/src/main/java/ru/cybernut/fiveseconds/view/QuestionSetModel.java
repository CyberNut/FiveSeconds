package ru.cybernut.fiveseconds.view;

import ru.cybernut.fiveseconds.model.QuestionSet;

public class QuestionSetModel {

    private QuestionSet questionSet;
    private boolean isChecked;
    private boolean isAvailable;
    private boolean isFree;
    private boolean isSoundsAvailable;
    private String soundsFilesSize;
    public QuestionSetModel(QuestionSet questionSet) {
        this.questionSet = questionSet;
        this.isFree = questionSet.getType().equalsIgnoreCase(QuestionSet.QuestionSetType.free.toString());
        this.isAvailable = this.isFree || questionSet.getOwned() >= 500;
        this.isSoundsAvailable = questionSet.isSoundsLoaded();
        this.isChecked = isAvailable;
        this.soundsFilesSize = questionSet.getSoundsFilesSize();
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

    public String getSoundsFilesSize() {
        return soundsFilesSize;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getSoundsLink() {
        if (questionSet == null) {
            return null;
        }
        return questionSet.getSoundsLink();
    }

    public boolean isFree() {
        return isFree;
    }

    public boolean isSoundsAvailable() {
        return isSoundsAvailable;
    }

    public void setSoundsAvailable(boolean soundsAvailable) {
        isSoundsAvailable = soundsAvailable;
    }

    public boolean markAsDownloaded() {
        questionSet.setSoundsLoaded(true);
        setSoundsAvailable(true);
        return true;
    }
}
