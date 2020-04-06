package ru.cybernut.fiveseconds;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.cybernut.fiveseconds.billing.BillingConstants;
import ru.cybernut.fiveseconds.billing.BillingManager;
import ru.cybernut.fiveseconds.billing.SkuData;
import ru.cybernut.fiveseconds.model.QuestionSetList;


public class ShopActivity extends AppCompatActivity implements BillingManager.BillingUpdatesListener, SkuDetailsResponseListener {

    private static final String TAG = "ShopActivity";
    private ShopItemsAdapter shopItemsAdapter;
    private Button shopButton;
    private RecyclerView shopItemsRecyclerView;
    private BillingManager mBillingManager;
    private ProgressBar progressBar;
    private Map<String, SkuData> skuDataMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_fragment);

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, this);

        progressBar = findViewById(R.id.progressBar);
        shopButton = findViewById(R.id.shop_test_button);
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemList();
            }
        });
        shopItemsRecyclerView = findViewById(R.id.shop_item_list);
        shopItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shopItemsAdapter = new ShopItemsAdapter();
        shopItemsRecyclerView.setAdapter(shopItemsAdapter);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void updateItemList() {
        List<String> skuList = BillingConstants.getSkuList(BillingClient.SkuType.INAPP);
        mBillingManager.querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList, ShopActivity.this);
        if(shopItemsAdapter!=null) {
            shopItemsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBillingClientSetupFinished() {
        Log.i(TAG, "onBillingClientSetupFinished: ");
        updateItemList();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConsumeFinished(String token, int result) {
        Log.i(TAG, "onConsumeFinished: ");
    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        Log.i(TAG, "onPurchasesUpdated: ");
        //TODO: Обновить список и отметить приобретенные элементы
        for (Purchase purchase : purchases) {

            String sku = purchase.getSku();

            Boolean purchased = purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED;
            if (skuDataMap.containsKey(sku)) {
                skuDataMap.get(sku).setOwned(purchased);
            } else {
                SkuData newSkuData = new SkuData(sku);
                newSkuData.setOwned(purchased);
                skuDataMap.put(sku, newSkuData);
            }
            if(purchased) {
                QuestionSetList.getInstance().setOwned(purchase.getSku());
            }
        }
        shopItemsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
        Log.i(TAG, "onSkuDetailsResponse: ");
        for (SkuDetails skuDetails : skuDetailsList) {
            String sku = skuDetails.getSku();
            if (skuDataMap.containsKey(sku)) {
                skuDataMap.get(sku).setSkuDetails(skuDetails);
            } else {
                SkuData newSkuData = new SkuData(sku);
                newSkuData.setSkuDetails(skuDetails);
                skuDataMap.put(sku, newSkuData);
            }
        }
        shopItemsAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private class ShopItemHolder extends RecyclerView.ViewHolder {

        private SkuData skuData;
        private TextView itemName;
        private TextView itemCost;
        private Button buyButton;
        public ShopItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.shop_list_item, parent, false));
            itemName = itemView.findViewById(R.id.shop_item_name);
            itemCost = itemView.findViewById(R.id.shop_item_cost);
            buyButton = itemView.findViewById(R.id.shop_item_buy_button);
            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(skuData != null) {
                        mBillingManager.initiatePurchaseFlow(skuData.getSkuDetails(), BillingClient.SkuType.INAPP);
                    }
                }
            });
        }

        public void bind(SkuData skuData) {
            this.skuData = skuData;
            if(skuData!=null && skuData.getSkuDetails() != null) {
                itemName.setText(skuData.getSkuDetails().getDescription());
                itemCost.setText(skuData.getSkuDetails().getPrice());
                if (skuData.isOwned()) {
                    buyButton.setText(R.string.bought);
                    buyButton.setEnabled(false);
                }
            }
        }
    }
    private class ShopItemsAdapter extends RecyclerView.Adapter<ShopItemHolder> {

        @NonNull
        @Override
        public ShopItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(ShopActivity.this);
            return new ShopItemHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull ShopItemHolder shopItemHolder, int position) {
            String[] skyArray = skuDataMap.keySet().toArray(new String[skuDataMap.size()]);
            if(position <= skyArray.length) {
                String sku = skyArray[position];
                SkuData skuData = skuDataMap.get(sku);
                if(skuData!=null) {
                    shopItemHolder.bind(skuData);
                }
            }
        }

        @Override
        public int getItemCount() {
            return skuDataMap.size();
        }
    }

}
