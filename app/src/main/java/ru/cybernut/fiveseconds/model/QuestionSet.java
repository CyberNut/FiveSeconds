package ru.cybernut.fiveseconds.model;

public class QuestionSet {

    public enum QuestionSetType {free, paid}

    private String name;
    private String type;
    private String soundsLink;
    private boolean soundsLoaded;


    public QuestionSet(String name, String type) {
        this(name, type, false);
    }

    public QuestionSet(String name, String type, boolean soundLoaded) {
        this.name = name;
        this.type = type;
        this.soundsLoaded = soundLoaded;
    }

    public QuestionSet(String name, String type, String soundsLink, boolean soundsLoaded) {
        this(name, type, soundsLoaded);
        this.soundsLink = soundsLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSoundsLink() {
        return soundsLink;
    }

    public void setSoundsLink(String soundsLink) {
        this.soundsLink = soundsLink;
    }

    public boolean isSoundsLoaded() {
        return soundsLoaded;
    }

    public void setSoundsLoaded(boolean soundsLoaded) {
        this.soundsLoaded = soundsLoaded;
    }
}
