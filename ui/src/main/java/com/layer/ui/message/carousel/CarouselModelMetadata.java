package com.layer.ui.message.carousel;

import com.google.gson.annotations.SerializedName;
import com.layer.ui.message.model.Action;

public class CarouselModelMetadata {
    @SerializedName("action")
    private Action mAction;

    public Action getAction() {
        return mAction;
    }

    public void setAction(Action action) {
        mAction = action;
    }
}
