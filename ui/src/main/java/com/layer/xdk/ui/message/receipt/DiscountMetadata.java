package com.layer.xdk.ui.message.receipt;

import com.google.gson.annotations.SerializedName;

public class DiscountMetadata {
    @SerializedName("name")
    String mName;
    @SerializedName("amount")
    Double mAmount;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Double getAmount() {
        return mAmount;
    }

    public void setAmount(Double amount) {
        mAmount = amount;
    }
}
