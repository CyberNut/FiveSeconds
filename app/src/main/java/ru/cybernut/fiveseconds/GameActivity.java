package ru.cybernut.fiveseconds;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.cybernut.fiveseconds.databinding.GameActivityBinding;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.game_activity);
        GameActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.game_activity);

    }
}
