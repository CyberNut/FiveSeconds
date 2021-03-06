package ru.cybernut.fiveseconds.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import ru.cybernut.fiveseconds.R;
import ru.cybernut.fiveseconds.databinding.PlayerCardBinding;

public class PlayerCard extends RelativeLayout {

    private PlayerModel player;
    private PlayerCardBinding binding;

    public PlayerCard(Context context) {
        super(context);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(mInflater, R.layout.player_card, this, true);

    }

    public PlayerCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(mInflater, R.layout.player_card, this, true);
    }

    public PlayerCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(mInflater, R.layout.player_card, this, true);
    }

    public boolean isCurrentPlayer() {
        return player.isCurrentPlayer();
    }

    public void setCurrentPlayer(boolean currentPlayer) {
        player.setCurrentPlayer(currentPlayer);
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
        if (binding != null) {
            binding.setPlayer(player);
        }
    }

    public void setProgress(double progress) {
        player.setProgressbarValue(progress);
        binding.roundedProgressBar.setProgress(progress);
    }

    public double getProgress() {

        return binding.roundedProgressBar.getProgress();
    }
}
