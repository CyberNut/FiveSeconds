package ru.cybernut.fiveseconds.billing;

import com.android.billingclient.api.SkuDetails;

public class SkuData {

    private String sku;
    private SkuDetails skuDetails;
    private boolean isOwned;

    public SkuData(String sku) {
        this.sku = sku;
        this.isOwned = false;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public SkuDetails getSkuDetails() {
        return skuDetails;
    }

    public void setSkuDetails(SkuDetails skuDetails) {
        this.skuDetails = skuDetails;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }
}
