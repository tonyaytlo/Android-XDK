package com.layer.xdk.ui.message.button;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ButtonMessageMetadata {
    @SerializedName("buttons")
    private List<ButtonMetadata> mButtonMetadata;

    public List<ButtonMetadata> getButtonMetadata() {
        return mButtonMetadata;
    }

    public void setButtonMetadata(List<ButtonMetadata> buttonMetadata) {
        mButtonMetadata = buttonMetadata;
    }
}
