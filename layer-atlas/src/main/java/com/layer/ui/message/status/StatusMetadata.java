package com.layer.ui.message.status;


import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Metadata for a status message
 */
public class StatusMetadata {

    @SerializedName("text")
    private String mText;

    @Nullable
    public String getText() {
        return mText;
    }

    public void setText(@Nullable String text) {
        mText = text;
    }
}
