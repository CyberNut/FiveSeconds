package ru.cybernut.fiveseconds;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.utils.SharedPreferencesHelper;

public class NewGameAddPlayersFragment extends Fragment {

    private static final String PREFERENCE_USER_NAME = "PREFERENCE_USER_NAME";
    private static final int NUMBER_OF_PLAYERS_LIST_COLUMNS = 2;

    private ImageButton addPlayerButton;
    private EditText playerName;
    private RecyclerView playerRecyclerView;
    private PlayersAdapter playersAdapter;
    private SharedPreferencesHelper sharedPreferencesHelper;

    public static NewGameAddPlayersFragment newInstance() {
        return new NewGameAddPlayersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_players_fragment, container, false);
        sharedPreferencesHelper = new SharedPreferencesHelper(FiveSecondsApplication.getAppContext());
        loadSettings();
        prepareUI(v);
        return v;
    }

    public void saveSettings() {
        Context context = getActivity();
        if(context != null ) {
            sharedPreferencesHelper.savePlayers(PlayersList.getInstance().getList());
        }
    }

    private void loadSettings() {
        Context context = getActivity();
        PlayersList playersList = PlayersList.getInstance();
        if(context != null && (playersList.getNumberOfPlayers() == 0)) {
            List<Player> tempList = sharedPreferencesHelper.getPlayers();
            for (Player player : tempList) {
                playersList.addPlayer(player);
            }
        }

        if (playersAdapter != null) {
            playersAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSettings();
    }

    private void addPlayer() {
        if(!playerName.getText().toString().isEmpty()) {
            Player player = new Player(playerName.getText().toString());
            if(!PlayersList.getInstance().addPlayer(player)) {
                Toast.makeText(getActivity(), R.string.max_player_error, Toast.LENGTH_SHORT).show();
            } else {
                playersAdapter.notifyDataSetChanged();
            }
            playerName.setText("");
        }
    }

    private void prepareUI(View view) {

        playerName = view.findViewById(R.id.player_name);
        playerName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addPlayer();
                    return true;
                }
                return false;
            }
        });

        playerRecyclerView = view.findViewById(R.id.players_recycler_view);
        playerRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), NUMBER_OF_PLAYERS_LIST_COLUMNS));

        playersAdapter = new PlayersAdapter(PlayersList.getInstance().getList());
        playerRecyclerView.setAdapter(playersAdapter);

        addPlayerButton = view.findViewById(R.id.add_player_button);
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlayer();
            }
        });

    }

    private class PlayersHolder extends RecyclerView.ViewHolder {

        private Player player;
        private TextView playerNameTextView;
        private ImageView playerPhotoImageView;
        private ImageButton deletePlayerButton;
        private int index;

        public PlayersHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.players_list_item, parent, false));

            playerNameTextView = itemView.findViewById(R.id.item_name);
            playerPhotoImageView = itemView.findViewById(R.id.item_photo);
            deletePlayerButton = itemView.findViewById(R.id.item_delete_player_button);
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
        public PlayersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new PlayersHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull PlayersHolder playersHolder, int position) {
            Player player = PlayersList.getInstance().getPlayer(position);
            playersHolder.bind(player, position);
        }

        @Override
        public int getItemCount() {
            return playerList.size();
        }
    }
}
