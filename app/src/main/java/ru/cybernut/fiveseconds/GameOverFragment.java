package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.cybernut.fiveseconds.databinding.GameOverActivityBinding;
import ru.cybernut.fiveseconds.databinding.PlayersScoresListItemBinding;
import ru.cybernut.fiveseconds.view.OnBackPressedListener;
import ru.cybernut.fiveseconds.view.PlayerModel;

public class GameOverFragment extends Fragment implements OnBackPressedListener {

    public static final String ARGS_PLAYERS_LIST = "ARGS_PLAYERS_LIST";
    private RecyclerView playerRecyclerView;
    private PlayersAdapter playersAdapter;

    public static GameOverFragment newInstance(ArrayList<PlayerModel> playerModels) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_PLAYERS_LIST, playerModels);

        GameOverFragment gameOverFragment = new GameOverFragment();
        gameOverFragment.setArguments(args);

        return gameOverFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GameOverActivityBinding binding = DataBindingUtil.inflate(inflater, R.layout.game_over_activity, container, false);

        ArrayList<PlayerModel> players = (ArrayList<PlayerModel>) getArguments().getSerializable(ARGS_PLAYERS_LIST);
        //sort players list by score desc
        Collections.sort(players, new Comparator<PlayerModel>() {
            @Override
            public int compare(PlayerModel o1, PlayerModel o2) {
                return Integer.compare(o2.getScore(), o1.getScore());
            }
        });
        playersAdapter = new PlayersAdapter(players, getActivity());
        playerRecyclerView = binding.winnersTable;
        playerRecyclerView.setAdapter(playersAdapter);
        playerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        View v = binding.getRoot();
        return v;
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), StartActivity.class);
        startActivity(intent);
    }

    private class PlayersHolder extends RecyclerView.ViewHolder {

        private PlayersScoresListItemBinding itemBinding;
        private PlayerModel playerModel;

        public PlayersHolder(PlayersScoresListItemBinding binding) {

            super(binding.getRoot());
            itemBinding = binding;

        }

        public void bind(PlayerModel player) {
            this.playerModel = player;
            itemBinding.setPlayerModel(playerModel);
        }
    }

    private class PlayersAdapter extends RecyclerView.Adapter<PlayersHolder> {

        private List<PlayerModel> playerList;
        private Context context;

        public PlayersAdapter(List<PlayerModel> players, Context context) {
            this.playerList = players;
            this.context = context;
        }

        @Override
        public PlayersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            PlayersScoresListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.players_scores_list_item, viewGroup, false);
            return new PlayersHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull PlayersHolder playersHolder, int position) {
            PlayerModel player = playerList.get(position);
            playersHolder.bind(player);
        }

        @Override
        public int getItemCount() {
            return playerList.size();
        }
    }

}