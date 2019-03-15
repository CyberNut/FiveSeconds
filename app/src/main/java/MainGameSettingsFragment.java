import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.cybernut.fiveseconds.R;
import ru.cybernut.fiveseconds.databinding.MainSettingsFragmentBinding;

public class MainGameSettingsFragment extends Fragment {

    private static final String TAG = "MainGameSettingsFragment";

    private MainSettingsFragmentBinding binding;

    public static MainGameSettingsFragment newInstance() {
        return new MainGameSettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_settings_fragment, container, false);
        View v = binding.getRoot();
        prepareUI(v);
        return v;
    }

    private void prepareUI(View view) {
    }

}
