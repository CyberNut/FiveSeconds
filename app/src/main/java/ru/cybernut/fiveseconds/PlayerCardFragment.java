package ru.cybernut.fiveseconds;

import android.databinding.DataBindingUtil;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.cybernut.fiveseconds.databinding.NewPlayerCardBinding;
import ru.cybernut.fiveseconds.databinding.PlayerCardBinding;
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
    private NewPlayerCardBinding binding;


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
        binding = DataBindingUtil.inflate(inflater, R.layout.new_player_card, container, false );
//        playerName = (TextView) playerCard.findViewById(R.id.player_card_name);
//        playerScore = (TextView) playerCard.findViewById(R.id.player_card_score);
//        playerPhoto = (ImageView) playerCard.findViewById(R.id.player_card_photo);
        binding.isCurrentImageView.setVisibility(View.INVISIBLE);
        binding.roundedProgressBar.setProgress(0.01);

        player = (Player) getArguments().getSerializable(PLAYER_ARGS_KEY);

        binding.setPlayer(player);
//        playerName.setText(player.getName());
//        playerScore.setText(String.valueOf(player.getScore()));
        //playerPhoto.setImageDrawable(player.getPhoto());

        return playerCard;
    }

    public void updateUI() {
        playerScore.setText(String.valueOf(player.getScore()));
        //isCurrentImageView.setVisibility(player.isCurrentPlayer() ? View.VISIBLE : View.INVISIBLE);
    }

    public void setProgress(double progress) {
        binding.roundedProgressBar.setProgress(progress);
    }

    public void setCurrentLabel(boolean isCurrent) {
        Log.i(TAG, "setCurrentLabel: " + isCurrent);
        if(isCurrent) {
            binding.isCurrentImageView.setVisibility(View.VISIBLE);
            binding.roundedProgressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.isCurrentImageView.setVisibility( View.INVISIBLE);
            binding.roundedProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public double getProgress() {
        return progressView.getProgress();
    }
}
