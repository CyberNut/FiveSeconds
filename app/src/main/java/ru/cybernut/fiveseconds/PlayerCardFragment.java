package ru.cybernut.fiveseconds;

import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.cybernut.fiveseconds.model.Player;

public class PlayerCardFragment extends Fragment {

    private static final String TAG = "PlayerCardFragment";
    private static final String PLAYER_ARGS_KEY = "PLAYER_ARGS_KEY";

    private Player player;
    private ImageView playerPhoto;
    private TextView playerScore;
    private TextView playerName;
    private ImageView isCurrentImageView;

    public static PlayerCardFragment newInstance(Player player) {

        PlayerCardFragment playerCardFragment = new PlayerCardFragment();
        Bundle args = new Bundle();
        args.putSerializable(PLAYER_ARGS_KEY, player);
        playerCardFragment.setArguments(args);
        return playerCardFragment;
    }

    public PlayerCardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View playerCard = inflater.inflate(R.layout.player_card, container, false);
        playerName = (TextView) playerCard.findViewById(R.id.player_card_name);
        playerScore = (TextView) playerCard.findViewById(R.id.player_card_score);
        playerPhoto = (ImageView) playerCard.findViewById(R.id.player_card_photo);
        isCurrentImageView = (ImageView) playerCard.findViewById(R.id.is_current_image_view);
        isCurrentImageView.setVisibility(View.INVISIBLE);

        player = (Player) getArguments().getSerializable(PLAYER_ARGS_KEY);

        playerName.setText(player.getName());
        playerScore.setText(String.valueOf(player.getScore()));
        //playerPhoto.setImageDrawable(player.getPhoto());

        return playerCard;
    }

    public void updateUI() {
        playerScore.setText(String.valueOf(player.getScore()));
        //isCurrentImageView.setVisibility(player.isCurrentPlayer() ? View.VISIBLE : View.INVISIBLE);
    }

    public void setCurrentLabel(boolean isCurrent) {
        Log.i(TAG, "setCurrentLabel: " + isCurrent);
        isCurrentImageView.setVisibility(isCurrent ? View.VISIBLE : View.INVISIBLE);
    }
}
