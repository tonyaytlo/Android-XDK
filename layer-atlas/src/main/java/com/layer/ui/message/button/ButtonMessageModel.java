package com.layer.ui.message.button;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.ui.R;
import com.layer.ui.message.model.MessageModel;
import com.layer.ui.util.json.AndroidFieldNamingStrategy;

import java.io.InputStreamReader;
import java.util.List;

public class ButtonMessageModel extends MessageModel {
    public static final String ROOT_MIME_TYPE = "application/vnd.layer.buttons+json";
    private static final String ROLE_CONTENT = "content";
    private Gson mGson;

    private ButtonMessageMetadata mMetadata;

    public ButtonMessageModel(Context context, LayerClient layerClient) {
        super(context, layerClient);
        mGson = new GsonBuilder().setFieldNamingStrategy(new AndroidFieldNamingStrategy()).create();
    }

    @Override
    public Class<ButtonMessageView> getRendererType() {
        return ButtonMessageView.class;
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {
        if (getRootMessagePart().equals(messagePart)) {
            JsonReader reader = new JsonReader(new InputStreamReader(messagePart.getDataStream()));
            mMetadata = mGson.fromJson(reader, ButtonMessageMetadata.class);
        }
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
        MessageModel contentModel = getContentModel();
        if (contentModel != null) {
            return contentModel.getActionEvent();
        }

        return null;
    }

    @Override
    public JsonObject getActionData() {
        MessageModel contentModel = getContentModel();
        if (contentModel != null) {
            return contentModel.getActionData();
        }

        return null;
    }

    @Override
    public int getBackgroundColor() {
        return R.color.transparent;
    }

    @Override
    public boolean getHasContent() {
        return getRootMessagePart().isContentReady();
    }

    @Nullable
    public MessageModel getContentModel() {
        if (getChildMessageModels().size() > 0) {
            return getChildMessageModels().get(0);
        }

        return null;
    }

    @Nullable
    public List<ButtonModel> getButtonModels() {
        return mMetadata != null ? mMetadata.getButtonModels() : null;
    }
}
