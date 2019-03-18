package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.databinding.GameOverActivityBinding;
import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.view.PlayerModel;

public class GameOverActivity extends AppCompatActivity {

    private static final String TAG = "GameOverActivity";
    private static final String EXTRA_WINNERS_LIST = "EXTRA_WINNERS_LIST";

    private ArrayList<PlayerModel> players;
    private GameOverActivityBinding binding;
    private RecyclerView playerRecyclerView;
    private PlayersAdapter playersAdapter;

    public static Intent newIntent(Context context, ArrayList<PlayerModel> playerModels) {
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra(EXTRA_WINNERS_LIST, playerModels);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ArrayList<PlayerModel> players = (ArrayList<PlayerModel>) getIntent().getSerializableExtra(EXTRA_WINNERS_LIST);
        super.onCreate(savedInstanceState);

        GameOverActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.game_over_activity);

        playerRecyclerView = binding.winnersTable;
    }

    private class PlayersHolder extends RecyclerView.ViewHolder {

        private Player player;
        private TextView playerNameTextView;
        private ImageView playerPhotoImageView;
        private ImageButton deletePlayerButton;
        private int index;

        public PlayersHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.players_list_item, parent, false));

            playerNameTextView = (TextView) itemView.findViewById(R.id.item_name);
            playerPhotoImageView = (ImageView) itemView.findViewById(R.id.item_photo);
            deletePlayerButton = (ImageButton) itemView.findViewById(R.id.item_delete_player_button);
            deletePlayerButton.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          PlayersList.getInstance().deletePlayer(index);
                                                          playersAdapter.notifyDataSetChanged();
                                                      }
                                                  }
            );

        }

        public void bind(Player player, int index) {
            this.player = player;
            this.index = index;
            playerNameTextView.setText(this.player.getName());
            //TODO: need store photo
//            if(this.player.getPhoto() == null) {
//                playerPhotoImageView.setImageDrawable(this.player.getPhoto());
//            } else {
            playerPhotoImageView.setImageResource(R.drawable.player_list_empty_photo);
//            }
        }
    }

    private class PlayersAdapter extends RecyclerView.Adapter<PlayersHolder> {

        private List<Player> playerList;

        public PlayersAdapter(List<Player> players) {
            this.playerList = players;
        }

        @Override
        public NewGameAddPlayersFragment.PlayersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new NewGameAddPlayersFragment.PlayersHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull NewGameAddPlayersFragment.PlayersHolder playersHolder, int position) {
            Player player = PlayersList.getInstance().getPlayer(position);
            playersHolder.bind(player, position);
        }

        @Override
        public int getItemCount() {
            return playerList.size();
        }
    }


}
