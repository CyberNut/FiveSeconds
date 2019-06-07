package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.cybernut.fiveseconds.databinding.GameOverActivityBinding;
import ru.cybernut.fiveseconds.databinding.PlayersScoresListItemBinding;
import ru.cybernut.fiveseconds.view.PlayerModel;

public class GameOverActivity extends SingleFragmentFullScreenActivity {

    private static final String TAG = "GameOverActivity";
    private static final String EXTRA_WINNERS_LIST = "EXTRA_WINNERS_LIST";
    private static final int NUMBER_OF_PLAYERS_LIST_COLUMNS = 2;

    private GameOverFragment gameOverFragment;

    @Override
    protected Fragment createFragment() {
        ArrayList<PlayerModel> playerModels = new ArrayList<>();
        gameOverFragment = GameOverFragment.newInstance(playerModels);
        return gameOverFragment;
    }

    public static Intent newIntent(Context context, ArrayList<PlayerModel> playerModels) {
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra(EXTRA_WINNERS_LIST, playerModels);
        return intent;
    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        ArrayList<PlayerModel> players = (ArrayList<PlayerModel>) getIntent().getSerializableExtra(EXTRA_WINNERS_LIST);
//        super.onCreate(savedInstanceState);
//
//        GameOverActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.game_over_activity);
//
//        //sort players list by score desc
//        Collections.sort(players, new Comparator<PlayerModel>() {
//            @Override
//            public int compare(PlayerModel o1, PlayerModel o2) {
//                return Integer.compare(o2.getScore(), o1.getScore());
//            }
//        });
//        playersAdapter = new PlayersAdapter(players, this);
//        playerRecyclerView = binding.winnersTable;
//        playerRecyclerView.setAdapter(playersAdapter);
//        playerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(GameOverActivity.this, StartActivity.class);
        startActivity(intent);
    }
}
