package ru.cybernut.fiveseconds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.cybernut.fiveseconds.model.Game;
import ru.cybernut.fiveseconds.model.Player;
import ru.cybernut.fiveseconds.model.PlayersList;
import ru.cybernut.fiveseconds.model.QuestionSet;
import ru.cybernut.fiveseconds.model.QuestionSetList;

public class NewGameAddPlayersFragment extends Fragment {

    private static final String PREFERENCE_USER_NAME = "PREFERENCE_USER_NAME";
    private static final int NUMBER_OF_PLAYERS_LIST_COLUMNS = 2;
    private static final int MAX_QUANTITY_OF_QUESTIONS = 20;
    private static final int MIN_QUANTITY_OF_QUESTIONS = 3;

    private int numberOfPlayers = 2;
    private SeekBar numberOfQuestionsSeekBar;
    private TextView numberOfQuestionsTextView;
    private ImageButton addPlayerButton;
    private EditText playerName;
    private RecyclerView playerRecyclerView;
    private RecyclerView questionSetsRecyclerView;
    private PlayersAdapter playersAdapter;
    private QuestionSetAdapter questionSetAdapter;

    public static NewGameAddPlayersFragment newInstance() {
        return new NewGameAddPlayersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_players_fragment, container, false);
        loadSettings();
        prepareUI(v);
        return v;
    }

    public void saveSettings() {
        Context context = getActivity();
        if(context != null ) {
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
        if(context != null && (PlayersList.getInstance().getNumberOfPlayers() == 0)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            for (int i = 0; i < Game.MAX_PLAYERS; i++) {
                if(sharedPreferences.contains(PREFERENCE_USER_NAME + i)) {
                    Player player = new Player(sharedPreferences.getString(PREFERENCE_USER_NAME + i, "Player"));
                    PlayersList.getInstance().putPlayer(player, i);
                }
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
            if(PlayersList.getInstance().addPlayer(player) == false) {
                Toast.makeText(getActivity(), R.string.max_player_error, Toast.LENGTH_SHORT).show();
            } else {
                playersAdapter.notifyDataSetChanged();
            }
            playerName.setText("");
        }
    }

    private void prepareUI(View view) {

        playerName = (EditText) view.findViewById(R.id.player_name);
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

        numberOfQuestionsTextView = (TextView)view.findViewById(R.id.number_of_questions);
        numberOfQuestionsSeekBar = (SeekBar)view.findViewById(R.id.number_of_questions_seek_bar);
        numberOfQuestionsSeekBar.setMax(MAX_QUANTITY_OF_QUESTIONS - MIN_QUANTITY_OF_QUESTIONS);
        numberOfQuestionsSeekBar.setProgress(numberOfPlayers);
        updateNumberOfQuestionsTextView();
        numberOfQuestionsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateNumberOfQuestionsTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateNumberOfQuestionsTextView();
            }
        });


        playerRecyclerView = (RecyclerView) view.findViewById(R.id.players_recycler_view);
        playerRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), NUMBER_OF_PLAYERS_LIST_COLUMNS));

        playersAdapter = new PlayersAdapter(PlayersList.getInstance().getList());
        playerRecyclerView.setAdapter(playersAdapter);

        addPlayerButton = (ImageButton) view.findViewById(R.id.add_player_button);
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlayer();
            }
        });

        questionSetsRecyclerView = view.findViewById(R.id.question_sets_recycler_view);
        questionSetsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        QuestionSetAdapter questionSetAdapter = new QuestionSetAdapter(QuestionSetList.getInstance(getActivity()).getQuestionSets());
        questionSetsRecyclerView.setAdapter(questionSetAdapter);
    }


    private void updateNumberOfQuestionsTextView() {
        numberOfPlayers = PlayersList.getInstance().getNumberOfPlayers();
        numberOfQuestionsTextView.setText(String.valueOf(MIN_QUANTITY_OF_QUESTIONS * numberOfPlayers +  numberOfPlayers * numberOfQuestionsSeekBar.getProgress()));
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

    private class QuestionSetHolder extends RecyclerView.ViewHolder {

        private QuestionSet questionSet;
        private CheckBox questionSetNameCheckBox;
        private int index;

        public QuestionSetHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.question_set_list_item, parent, false));

            questionSetNameCheckBox = (CheckBox) itemView.findViewById(R.id.question_set_name_checkbox);
        }

        public void bind(QuestionSet questionSet, int index) {
            this.questionSet = questionSet;
            this.index = index;
            if(this.questionSet!= null) {
                questionSetNameCheckBox.setText(this.questionSet.getName());
            }
        }
    }

    private class QuestionSetAdapter extends RecyclerView.Adapter<QuestionSetHolder> {

        private List<QuestionSet> questionSetList;

        public QuestionSetAdapter(List<QuestionSet> questionSetList) {
            this.questionSetList = questionSetList;
        }

        @Override
        public QuestionSetHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new QuestionSetHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionSetHolder questionSetHolder, int position) {
            QuestionSet questionSet = questionSetList.get(position);
            if(questionSet!= null) {
                questionSetHolder.bind(questionSet, position);
            }
        }

        @Override
        public int getItemCount() {
            return questionSetList.size();
        }
    }

}
