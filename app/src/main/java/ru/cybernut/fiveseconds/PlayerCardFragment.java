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
import ru.cybernut.fiveseconds.view.RoundedSquareProgressView;

public class PlayerCardFragment extends Fragment {

    private static final String TAG = "PlayerCardFragment";
    private static final String PLAYER_ARGS_KEY = "PLAYER_ARGS_KEY";

    private Player player;
    private ImageView playerPhoto;
    private TextView playerScore;
    private TextView playerName;
    private ImageView isCurrentImageView;
    private RoundedSquareProgressView progressView;


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
        View playerCard = inflater.inflate(R.layout.new_player_card, container, false);
        playerName = (TextView) playerCard.findViewById(R.id.player_card_name);
        playerScore = (TextView) playerCard.findViewById(R.id.player_card_score);
        playerPhoto = (ImageView) playerCard.findViewById(R.id.player_card_photo);
        isCurrentImageView = (ImageView) playerCard.findViewById(R.id.is_current_image_view);
        isCurrentImageView.setVisibility(View.INVISIBLE);
        progressView = (RoundedSquareProgressView) playerCard.findViewById(R.id.rounded_progress_bar);
        progressView.setProgress(0.01);

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

    public void setProgress(double progress) {
        progressView.setProgress(progress);
    }

    public void setCurrentLabel(boolean isCurrent) {
        Log.i(TAG, "setCurrentLabel: " + isCurrent);
        if(isCurrent) {
            isCurrentImageView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.VISIBLE);
        }
        else {
            isCurrentImageView.setVisibility( View.INVISIBLE);
            progressView.setVisibility(View.INVISIBLE);
        }
    }

    public double getProgress() {
        return progressView.getProgress();
    }
}
