package com.layer.xdk.ui.message.product;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.model.Action;

import java.util.List;

public class ProductMessageMetadata {
    @SerializedName("brand")
    private String mBrand;

    @SerializedName("name")
    private String mName;

    @SerializedName("image_urls")
    private List<String> mImageUrls;

    @SerializedName("price")
    private Float mPrice;

    @SerializedName("quantity")
    private Integer mQuantity;

    @SerializedName("currency")
    private String mCurrency;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("url")
    private String mUrl;

    @SerializedName("action")
    private Action mAction;

    @Nullable
    public String getBrand() {
        return mBrand;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    @Nullable
    public List<String> getImageUrls() {
        return mImageUrls;
    }

    @Nullable
    public Float getPrice() {
        return mPrice;
    }

    @NonNull
    public int getQuantity() {
        return mQuantity != null ? mQuantity : 1;
    }

    @NonNull
    public String getCurrency(Context context) {
        return mCurrency != null ? mCurrency : context.getString(R.string.xdk_ui_product_message_model_default_currency);
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    @Nullable
    public String getUrl() {
        return mUrl;
    }

    @Nullable
    public Action getAction() {
        return mAction;
    }

    public void setBrand(String brand) {
        mBrand = brand;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setImageUrls(List<String> imageUrls) {
        mImageUrls = imageUrls;
    }

    public void setPrice(Float price) {
        mPrice = price;
    }

    public void setQuantity(Integer quantity) {
        mQuantity = quantity;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setAction(Action action) {
        mAction = action;
    }
}
