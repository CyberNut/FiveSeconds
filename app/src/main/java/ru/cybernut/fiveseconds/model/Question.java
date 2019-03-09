package ru.cybernut.fiveseconds.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.UUID;

import ru.cybernut.fiveseconds.BR;

public class Question extends BaseObservable {

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

    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

}
