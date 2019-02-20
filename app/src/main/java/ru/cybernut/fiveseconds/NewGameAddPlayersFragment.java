package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ru.cybernut.fiveseconds.model.Game;
import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;

public class NewGameAddPlayersFragment extends Fragment {

    private static final String PREFERENCE_USER_NAME = "PREFERENCE_USER_NAME";

    private ImageButton addPlayerButton;
    private EditText playerName;
    private ListView playerListView;
    private PlayersAdapter playersAdapter;

    public static NewGameAddPlayersFragment newInstance() {
        return new NewGameAddPlayersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_players_fragment, container, false);

        prepareUI(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSettings();
    }

    private void saveSettings() {
        Context context = getActivity();
        if(context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            for (int i = 0; i < PlayersList.getInstance().getNumberOfPlayers(); i++) {
                Player player = PlayersList.getInstance().getPlayer(i);
                sharedPreferences.edit()
                        .putString(PREFERENCE_USER_NAME + i, player.getName())
                        .apply();
            }
        }
    }

    private void loadSettings() {
        Context context = getActivity();
        if(context != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            for (int i = 0; i < Game.MAX_PLAYERS; i++) {
                if(sharedPreferences.contains(PREFERENCE_USER_NAME + i)) {
                    Player player = new Player(sharedPreferences.getString(PREFERENCE_USER_NAME + i, "Player"));
                    PlayersList.getInstance().putPlayer(player, i);
                }
            }
        }
        playersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSettings();
    }

    private void addPlayer() {
        if(!playerName.getText().toString().isEmpty()) {
            Player player = new Player(playerName.getText().toString());
            PlayersList.getInstance().addPlayer(player);
            playersAdapter.notifyDataSetChanged();
            playerName.setText("");
        }
    }

    private void prepareUI(View view) {

        playerName = (EditText) view.findViewById(R.id.playerName);
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

        playerListView = (ListView) view.findViewById(R.id.playersListView);

        playersAdapter = new PlayersAdapter(getActivity());
        playerListView.setAdapter(playersAdapter);

        addPlayerButton = (ImageButton) view.findViewById(R.id.addPlayerButton);
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlayer();
            }
        });
    }

    private class PlayersAdapter extends BaseAdapter {
        private Context context;

        public PlayersAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return PlayersList.getInstance().getNumberOfPlayers();
        }

        @Override
        public Player getItem(int position) {
            return PlayersList.getInstance().getPlayer(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.players_list_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.item_name);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.item_photo);
            textView.setText(PlayersList.getInstance().getPlayer(position).getName());
            imageView.setImageResource(R.drawable.player_list_empty_photo);
            ImageButton deletePlayerButton = (ImageButton) rowView.findViewById(R.id.item_delete_player_button);
            final int pos = position;
            deletePlayerButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      PlayersList.getInstance().deletePlayer(pos);
                      playersAdapter.notifyDataSetChanged();
                  }
              }
            );
            return rowView;
        }
    }
}
