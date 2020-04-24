package ru.cybernut.fiveseconds;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;

import java.util.List;
import java.util.Locale;

import ru.cybernut.fiveseconds.billing.BillingConstants;
import ru.cybernut.fiveseconds.billing.BillingManager;
import ru.cybernut.fiveseconds.databinding.StartFragmentBinding;
import ru.cybernut.fiveseconds.model.QuestionSetList;

public class StartFragment extends Fragment implements BillingManager.BillingUpdatesListener {

    private static final String TAG = "StartFragment";
    private static final String MAIN_GAME_SETTINGS_FRAGMENT = "MAIN_GAME_SETTINGS_FRAGMENT";

    private BillingManager mBillingManager;
    private StartFragmentBinding binding;
    private ImageButton newGameButton;
    private ImageButton helpButton;
    private ImageButton settingsButton;
    private ImageButton shopButton;
    private ImageButton shareButton;

    public static StartFragment newInstance() {
        return new StartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.start_fragment, container, false);
        prepareUI();
        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this.getActivity(), this);
        return binding.getRoot();
    }

    @Override
    public void onBillingClientSetupFinished() {
        updatePurchasesItemList();
    }

    @Override
    public void onConsumeFinished(String token, int result) {

    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        for (Purchase purchase : purchases) {
            Boolean purchased = purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED;
            if(purchased) {
                QuestionSetList.getInstance().setOwned(purchase.getSku());
            }
        }
    }

    private void updatePurchasesItemList() {
        List<String> skuList = BillingConstants.getSkuList(BillingClient.SkuType.INAPP);
        //mBillingManager.querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList, this);
    }


    private void prepareUI() {
        newGameButton = binding.newGameButton;
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = NewGameActivity.newIntent(getActivity());
                Log.i(TAG, Locale.getDefault().getLanguage());
                startActivity(intent);
            }
        });

        helpButton = binding.helpButton;
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RulesActivity.newIntent(getActivity());
                startActivity(intent);
            }
        });

        settingsButton = binding.settingsButton;
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity activity = (StartActivity) getActivity();
                if ( activity!= null) {
                    activity.replaceFragment(new ru.cybernut.fiveseconds.MainGameSettingsFragment(), MAIN_GAME_SETTINGS_FRAGMENT);
                }
            }
        });

        shopButton = binding.shopButton;
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShopActivity.class);
                startActivity(intent);
            }
        });

        shareButton = binding.shareButton;
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/apps/internaltest/4699175933318535405");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

    }
}
