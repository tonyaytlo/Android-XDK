package com.layer.xdk.ui.message.response;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.status.StatusMessageModel;
import com.layer.xdk.ui.message.view.MessageView;

public class ResponseMessageModel extends MessageModel {

    public static final String MIME_TYPE = "application/vnd.layer.response+json";

    public ResponseMessageModel(Context context, LayerClient layerClient) {
        super(context, layerClient);
    }

    @Override
    public Class<? extends MessageView> getRendererType() {
        // No renderer type since this type is not rendered
        return null;
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {
        // Nothing to do
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
    public boolean getHasContent() {
        if (getChildMessageModels() != null && !getChildMessageModels().isEmpty()) {
            return getChildMessageModels().get(0).getHasContent();
        }
        return false;
    }

    @Nullable
    @Override
    public String getPreviewText() {
        if (getChildMessageModels() != null && !getChildMessageModels().isEmpty()) {
            return getChildMessageModels().get(0).getPreviewText();
        }
        return null;
    }

    @Nullable
    public String getText() {
        if (getChildMessageModels() != null && !getChildMessageModels().isEmpty()) {
            return ((StatusMessageModel) getChildMessageModels().get(0)).getText();
        }
        return null;
    }
}
