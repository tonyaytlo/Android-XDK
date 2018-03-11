package com.layer.xdk.ui.message.status;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.util.Linkify;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.util.json.AndroidFieldNamingStrategy;

import java.io.InputStreamReader;

public class StatusMessageModel extends MessageModel {

    public static final String MIME_TYPE = "application/vnd.layer.status+json";

    private StatusMessageMetadata mMetadata;
    private SpannableString mText;
    private final Gson mGson;

    public StatusMessageModel(Context context, LayerClient layerClient, Message message) {
        super(context, layerClient, message);
        mGson = new GsonBuilder().setFieldNamingStrategy(new AndroidFieldNamingStrategy()).create();
    }

    @Override
    public int getViewLayoutId() {
        // No view layout since this is rendered inside a MessageItemStatusViewModel
        return 0;
    }

    @Override
    public int getContainerViewLayoutId() {
        // No container layout since this is rendered inside a MessageItemStatusViewModel
        return 0;
    }

    @Override
    protected void parse(@NonNull MessagePart messagePart) {
        JsonReader reader = new JsonReader(new InputStreamReader(messagePart.getDataStream()));
        mMetadata = mGson.fromJson(reader, StatusMessageMetadata.class);
        if (mMetadata != null && mMetadata.mText != null) {
            mText = new SpannableString(mMetadata.mText);
            Linkify.addLinks(mText, Linkify.ALL);
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
    public boolean getHasContent() {
        return mMetadata != null;
    }

    @Nullable
    @Override
    public String getPreviewText() {
        if (mText != null) {
            return mText.toString();
        }
        return getAppContext().getString(R.string.xdk_ui_status_message_preview_text);
    }

    @Override
    public String getActionEvent() {
        if (super.getActionEvent() != null) {
            return super.getActionEvent();
        }

        if (mMetadata.mAction != null) {
            return mMetadata.mAction.getEvent();
        }
        return null;
    }


    @NonNull
    @Override
    public JsonObject getActionData() {
        if (super.getActionData().size() > 0) {
            return super.getActionData();
        }

        if (mMetadata.mAction != null) {
            return mMetadata.mAction.getData();
        }

        return new JsonObject();
    }

    @Nullable
    public CharSequence getText() {
        return mText;
    }
}
