package com.layer.ui.message.status;


import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.layer.ui.message.model.Action;

/**
 * Metadata for a status message
 */
public class StatusMessageMetadata {

    @SerializedName("text")
    private String mText;

    @SerializedName("action")
    private Action mAction;

    @Nullable
    public String getText() {
        return mText;
    }

    public void setText(@Nullable String text) {
        mText = text;
    }

    @Nullable
    public Action getAction() {
        return mAction;
    }

    public void setAction(@Nullable Action action) {
        mAction = action;
    }
}
