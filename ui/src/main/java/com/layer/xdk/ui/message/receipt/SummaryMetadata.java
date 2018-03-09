package com.layer.xdk.ui.message.receipt;

import com.google.gson.annotations.SerializedName;

public class SummaryMetadata {
    @SerializedName("shipping_cost")
    private Double mShippingCost;

    @SerializedName("subtotal")
    private Double mSubtotal;

    @SerializedName("total_cost")
    private Double mTotalCost;

    @SerializedName("total_tax")
    private Double mTotalTax;

    public Double getShippingCost() {
        return mShippingCost;
    }

    public Double getSubtotal() {
        return mSubtotal;
    }

    public Double getTotalCost() {
        return mTotalCost;
    }

    public Double getTotalTax() {
        return mTotalTax;
    }

    public void setShippingCost(Double shippingCost) {
        mShippingCost = shippingCost;
    }

    public void setSubtotal(Double subtotal) {
        mSubtotal = subtotal;
    }

    public void setTotalCost(Double totalCost) {
        mTotalCost = totalCost;
    }

    public void setTotalTax(Double totalTax) {
        mTotalTax = totalTax;
    }
}
