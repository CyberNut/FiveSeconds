package ru.cybernut.fiveseconds;

import java.util.UUID;

public class Question {

    private String text;
    private UUID id;

    public Question() {
        this(UUID.randomUUID());
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
