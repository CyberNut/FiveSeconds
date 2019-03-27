package ru.cybernut.fiveseconds.model;

public class QuestionSet {

    public enum QuestionSetType {free, paid}

    private String name;
    private String type;

    public QuestionSet(String name, String type) {
        this.name = name;
        this.type = type;
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
}
