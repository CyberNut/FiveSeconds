package ru.cybernut.fiveseconds.model;

import java.util.UUID;

public class Sound {

    private String path;
    private UUID questionUUID;

    public Sound(String path) {
        this.path = path;
    }

    public Sound(UUID questionUUID) {
        this.questionUUID = questionUUID;
    }

    public Sound(String path, UUID questionUUID) {
        this.path = path;
        this.questionUUID = questionUUID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public UUID getQuestionUUID() {
        return questionUUID;
    }

    public void setQuestionUUID(UUID questionUUID) {
        this.questionUUID = questionUUID;
    }
}
