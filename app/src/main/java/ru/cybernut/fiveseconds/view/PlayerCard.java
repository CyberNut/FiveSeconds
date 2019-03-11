package ru.cybernut.fiveseconds.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import ru.cybernut.fiveseconds.R;
import ru.cybernut.fiveseconds.databinding.NewPlayerCardBinding;
import ru.cybernut.fiveseconds.model.Player;

public class PlayerCard extends RelativeLayout {

    private Player player;
    private NewPlayerCardBinding binding;
    private boolean isCurrentPlayer;

    public PlayerCard(Context context) {
        super(context);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init(null, 0);
        binding = DataBindingUtil.inflate(mInflater, R.layout.new_player_card, this, true);

    }

    public PlayerCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init(attrs, 0);
        binding = DataBindingUtil.inflate(mInflater, R.layout.new_player_card, this, true);
    }

    public PlayerCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init(attrs, defStyleAttr);
        binding = DataBindingUtil.inflate(mInflater, R.layout.new_player_card, this, true);
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PlayerCard, defStyleAttr, 0);
        isCurrentPlayer = a.getBoolean(R.styleable.PlayerCard_currentPlayer, false);
        a.recycle();
    }

    public boolean isCurrentPlayer() {
        return isCurrentPlayer;
    }

    public void setCurrentPlayer(boolean currentPlayer) {
        isCurrentPlayer = currentPlayer;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        if (binding != null) {
            binding.setPlayer(player);
        }
    }

    public void setProgress(double progress) {
        binding.roundedProgressBar.setProgress(progress);
    }

    public double getProgress() {
        return binding.roundedProgressBar.getProgress();
    }
}
