package ru.cybernut.fiveseconds;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewGameAddPlayersFragment extends Fragment {

    public static NewGameAddPlayersFragment newInstance() {
        return new NewGameAddPlayersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_players_fragment, container, false);
        return v;
    }

}
