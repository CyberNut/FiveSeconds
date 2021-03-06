package ru.cybernut.fiveseconds.model;

import java.util.UUID;

public class Question {

    private String text;
    private UUID id;

    public Question() {
        this(UUID.randomUUID());
    }

    public Question(String text) {
        this(UUID.randomUUID());
        this.text = text;
    }

    public Question(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
