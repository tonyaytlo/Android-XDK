package com.layer.xdk.ui.message.legacy;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.message.model.AbstractMessageModel;

public abstract class LegacyMessageModel extends AbstractMessageModel {

    private String mMimeTypeTree;


    public LegacyMessageModel(Context context, LayerClient layerClient, Message message) {
        super(context, layerClient, message);
        setMimeTypeTree();
        initiatePartDownloads(message);
    }

    @Override
    public String getMimeTypeTree() {
        return mMimeTypeTree;
    }

    @Nullable
    @Override
    public final String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public final String getDescription() {
        return null;
    }

    @Nullable
    @Override
    public final String getFooter() {
        return null;
    }

    private void initiatePartDownloads(@NonNull Message message) {
        for (MessagePart part : message.getMessageParts()) {
            if (!part.isContentReady() && shouldDownloadContentIfNotReady(part)) {
                part.download(null);
            }
        }
    }

    private void setMimeTypeTree() {
        StringBuilder sb = new StringBuilder();
        boolean prependComma = false;
        for (MessagePart part : getMessage().getMessageParts()) {
            if (prependComma) {
                sb.append(",");
            }
            sb.append(part.getMimeType());
            sb.append("[]");
            prependComma = true;
        }
        mMimeTypeTree = sb.toString();
    }
}
