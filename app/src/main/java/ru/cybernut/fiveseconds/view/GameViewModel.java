package ru.cybernut.fiveseconds.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import ru.cybernut.fiveseconds.model.Game;

public class GameViewModel extends AndroidViewModel {

    private Game game;

    public GameViewModel(@NonNull Application application, int numberOfQuestions) {
        super(application);
        game = new Game(application, Game.GameType.AUTO_PLAY_SOUND, numberOfQuestions);
    }

}
