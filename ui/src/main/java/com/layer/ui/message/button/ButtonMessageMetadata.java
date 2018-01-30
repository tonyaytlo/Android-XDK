package com.layer.ui.message.button;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ButtonMessageMetadata {
    @SerializedName("buttons")
    private List<ButtonModel> mButtonModels;

    public List<ButtonModel> getButtonModels() {
        return mButtonModels;
    }

    public void setButtonModels(List<ButtonModel> buttonModels) {
        mButtonModels = buttonModels;
    }
}
