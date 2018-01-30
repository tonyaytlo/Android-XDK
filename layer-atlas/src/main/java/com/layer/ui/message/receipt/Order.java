package com.layer.ui.message.receipt;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("number")
    String mNumber;

    @SerializedName("url")
    String mUrl;

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
