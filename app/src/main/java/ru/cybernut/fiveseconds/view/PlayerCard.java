package ru.cybernut.fiveseconds.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import ru.cybernut.fiveseconds.R;
import ru.cybernut.fiveseconds.databinding.NewPlayerCardBinding;

public class PlayerCard extends RelativeLayout {

    private PlayerModel player;
    private NewPlayerCardBinding binding;

    public PlayerCard(Context context) {
        super(context);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //init(null, 0);
        binding = DataBindingUtil.inflate(mInflater, R.layout.new_player_card, this, true);

    }

    public PlayerCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //init(attrs, 0);
        binding = DataBindingUtil.inflate(mInflater, R.layout.new_player_card, this, true);
    }

    public PlayerCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //init(attrs, defStyleAttr);
        binding = DataBindingUtil.inflate(mInflater, R.layout.new_player_card, this, true);
    }

//    public void init(AttributeSet attrs, int defStyleAttr) {
//        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, R.styleable.PlayerCard, defStyleAttr, 0);
//        isCurrentPlayer = a.getBoolean(R.styleable.PlayerCard_currentPlayer, false);
//        a.recycle();
//    }

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
