package ru.cybernut.fiveseconds;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;

public class NewGameAddPlayersFragment extends Fragment {

    private List<Player> playersList;

    private Button addPlayerButton;
    private EditText playerName;
    private ListView playerListView;
    private PlayersAdapter playerListAdapter;

    public static NewGameAddPlayersFragment newInstance() {
        return new NewGameAddPlayersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_players_fragment, container, false);

        playersList = PlayersList.getInstance().getList();

        playerName = (EditText) v.findViewById(R.id.playerName);
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

        playerListView = (ListView) v.findViewById(R.id.playersListView);

        playerListAdapter = new PlayersAdapter(getActivity(), playersList);
        playerListView.setAdapter(playerListAdapter);

        addPlayerButton = (Button) v.findViewById(R.id.addPlayerButton);
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlayer();
            }
        });
        return v;
    }

    private void addPlayer() {
        if(!playerName.getText().toString().isEmpty()) {
            Player player = new Player(playerName.getText().toString());
            PlayersList.getInstance().addPlayer(player);
            playerListAdapter.notifyDataSetChanged();
            playerName.setText("");
        }
    }

    private class PlayersAdapter extends ArrayAdapter<Player> {
        private Context context;
        private List<Player> list;

        public PlayersAdapter(Context context, List<Player> list) {
            super(context, R.layout.players_list_item, list);
            this.list = list;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.players_list_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.item_name);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.item_photo);
            textView.setText(list.get(position).toString());
            imageView.setImageResource(R.drawable.player_list_empty_photo);
            return rowView;
        }
    }


}
