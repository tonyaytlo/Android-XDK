package com.layer.ui.message.choice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.ui.message.model.MessageModel;
import com.layer.ui.message.view.MessageView;

public class ChoiceModel extends MessageModel {
    @SerializedName("id")
    private String mId;

    @SerializedName("text")
    private String mText;

    @SerializedName("tooltip")
    private String mTooltip;

    public ChoiceModel(Context context, LayerClient layerClient, String id, String text) {
        super(context, layerClient);
        mId = id;
        mText = text;
    }

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

    @Override
    public Class<? extends MessageView> getRendererType() {
        return null;
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {

    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        return true;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @Nullable
    @Override
    public String getFooter() {
        return null;
    }

    @Override
    public String getActionEvent() {
        return null;
    }

    @Override
    public int getBackgroundColor() {
        return 0;
    }

    @Override
    public boolean getHasContent() {
        return false;
    }
}
