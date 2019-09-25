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
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import ru.cybernut.fiveseconds.billing.BillingConstants;
import ru.cybernut.fiveseconds.billing.BillingManager;


public class ShopActivity extends AppCompatActivity implements BillingManager.BillingUpdatesListener, SkuDetailsResponseListener {

    private static final String TAG = "ShopActivity";
    private ShopItemsAdapter shopItemsAdapter;
    private Button shopButton;
    private RecyclerView shopItemsRecyclerView;
    private BillingManager mBillingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_fragment);

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, this);

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
        updateItemList();
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
    }

    @Override
    public void onConsumeFinished(String token, int result) {
        Log.i(TAG, "onConsumeFinished: ");
    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        Log.i(TAG, "onPurchasesUpdated: ");
        //TODO: Обновить список и отметить приобретенные элементы
    }

    @Override
    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
        Log.i(TAG, "onSkuDetailsResponse: ");
        shopItemsAdapter.setSkuDetailsList(skuDetailsList);
        shopItemsAdapter.notifyDataSetChanged();
    }

    private class ShopItemHolder extends RecyclerView.ViewHolder {

        SkuDetails skuDetails;
        TextView itemName;
        TextView itemCost;
        Button buyButton;
        public ShopItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.shop_list_item, parent, false));
            itemName = itemView.findViewById(R.id.shop_item_name);
            itemCost = itemView.findViewById(R.id.shop_item_cost);
            buyButton = itemView.findViewById(R.id.shop_item_buy_button);
            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(skuDetails != null) {
                        mBillingManager.initiatePurchaseFlow(skuDetails, BillingClient.SkuType.INAPP);
                    }
                }
            });
        }

        public void bind(SkuDetails skuDetails) {
            this.skuDetails = skuDetails;
            itemName.setText(skuDetails.getDescription());
            itemCost.setText(skuDetails.getPrice());

        }
    }
    private class ShopItemsAdapter extends RecyclerView.Adapter<ShopItemHolder> {

        private List<SkuDetails> skuDetailsList;
        public ShopItemsAdapter() {

            skuDetailsList = new ArrayList<>();
        }

        public List<SkuDetails> getSkuDetailsList() {
            return skuDetailsList;
        }

        public void setSkuDetailsList(List<SkuDetails> skuDetailsList) {
            this.skuDetailsList = skuDetailsList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ShopItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(ShopActivity.this);
            return new ShopItemHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull ShopItemHolder shopItemHolder, int position) {
            SkuDetails skuDetails = skuDetailsList.get(position);
            shopItemHolder.bind(skuDetails);
        }

        @Override
        public int getItemCount() {
            return skuDetailsList.size();
        }
    }

}
