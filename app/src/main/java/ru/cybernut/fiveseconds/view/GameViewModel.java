package ru.cybernut.fiveseconds.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;

import ru.cybernut.fiveseconds.model.Game;
import ru.cybernut.fiveseconds.model.PlayersList;

public class GameViewModel extends BaseObservable {

    private Game game;
    private ArrayList<Integer> setIds;
    private String currentQuestionText;

    public GameViewModel(@NonNull Application application, int numberOfQuestions, ArrayList<Integer> setIds) {
        game = new Game(application, Game.GameType.AUTO_PLAY_SOUND, numberOfQuestions);
        game.init(setIds);
    }



    @Bindable
    public String getCurrentQuestionText() {
        if(game != null) {
            return game.getCurrentQuestion().getText();
        }
        return null;
    }

    public void nextTurn(boolean isCorrectAnswer) {
        if(game!=null) {
            game.nextTurn(isCorrectAnswer);
            notifyPropertyChanged(BR.currentQuestionText);
        }
    }

    public int getNumberOfPlayers() {
        return PlayersList.getInstance().getNumberOfPlayers();
    }
}
