package ru.cybernut.fiveseconds.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ru.cybernut.fiveseconds.R;

public class RoundedSquareProgressBar extends RelativeLayout {

    private FrameLayout contentView;
    private final RoundedSquareProgressView bar;


    public RoundedSquareProgressBar(Context context) {
        super(context);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.rounded_progressbar, this, true);
        bar = (RoundedSquareProgressView) findViewById(R.id.squareProgressBar1);
        contentView = (FrameLayout) findViewById(R.id.contentView1);
        bar.bringToFront();
    }

    public RoundedSquareProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.rounded_progressbar, this, true);
        bar = (RoundedSquareProgressView) findViewById(R.id.squareProgressBar1);
        contentView = (FrameLayout) findViewById(R.id.contentView1);
        bar.bringToFront();
    }

    public RoundedSquareProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.rounded_progressbar, this, true);
        bar = (RoundedSquareProgressView) findViewById(R.id.squareProgressBar1);
        contentView = (FrameLayout) findViewById(R.id.contentView1);
        bar.bringToFront();
    }
}
