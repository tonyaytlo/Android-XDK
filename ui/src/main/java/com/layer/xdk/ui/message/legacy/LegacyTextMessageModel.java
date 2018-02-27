package com.layer.xdk.ui.message.legacy;


import android.content.Context;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;

public class LegacyTextMessageModel extends LegacyMessageModel {
    public final static String MIME_TYPE = "text/plain";

    private String mText;

    public LegacyTextMessageModel(Context context, LayerClient layerClient, Message message) {
        super(context, layerClient, message);
        MessagePart part = message.getMessageParts().iterator().next();
        mText = part.isContentReady() ? new String(part.getData()) : null;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.xdk_ui_text_message_view;
    }

    @Override
    public int getContainerViewLayoutId() {
        return R.layout.xdk_ui_empty_message_container;
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        return true;
    }

    @Nullable
    @Override
    public String getPreviewText() {
        return mText == null ? "" : mText;
    }

    @Bindable
    public String getText() {
        return mText;
    }

}
