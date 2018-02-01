package com.layer.xdk.ui.message.receipt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.layer.xdk.ui.R;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class ReceiptMessageMetadata {
    @SerializedName("created_at")
    private String mCreatedAt;

    @SerializedName("currency")
    private String mCurrency;

    @SerializedName("discounts")
    private List<Discount> mDiscounts;

    @SerializedName("order")
    private Order mOrder;

    @SerializedName("payment_method")
    private String mPaymentMethod;

    @SerializedName("summary")
    private Summary mSummary;

    @SerializedName("title")
    private String mTitle;

    public String getCreatedAt() {
        return mCreatedAt;
    }

    @NonNull
    public String getCurrency(Context context) {
        return mCurrency != null ? mCurrency : context.getString(R.string.xdk_ui_product_message_model_default_currency);
    }

    public List<Discount> getDiscounts() {
        return mDiscounts;
    }

    public Order getOrder() {
        return mOrder;
    }

    public String getPaymentMethod() {
        return mPaymentMethod;
    }

    public Summary getSummary() {
        return mSummary;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public void setDiscounts(List<Discount> discounts) {
        mDiscounts = discounts;
    }

    public void setOrder(Order order) {
        mOrder = order;
    }

    public void setPaymentMethod(String paymentMethod) {
        mPaymentMethod = paymentMethod;
    }

    public void setSummary(Summary summary) {
        mSummary = summary;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Nullable
    public String getTotalCostToDisplay(@NonNull Context context) {
        if (mSummary != null && mSummary.getTotalCost() != null) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            currencyFormat.setCurrency(Currency.getInstance(getCurrency(context)));
            return currencyFormat.format(mSummary.getTotalCost());
        }

        return null;
    }
}
