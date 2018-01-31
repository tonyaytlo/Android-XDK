package com.layer.xdk.ui.message.choice;

import com.google.gson.annotations.SerializedName;

public class ChoiceMetadata {
    @SerializedName("id")
    private String mId;

    @SerializedName("text")
    private String mText;

    @SerializedName("tooltip")
    private String mTooltip;

    public String getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    public String getTooltip() {
        return mTooltip;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setTooltip(String tooltip) {
        mTooltip = tooltip;
    }
}