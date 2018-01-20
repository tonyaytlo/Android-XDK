package com.layer.ui.message;


import android.content.Context;
import android.databinding.Bindable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.ui.message.status.StatusMetadata;
import com.layer.ui.util.json.AndroidFieldNamingStrategy;
import com.layer.ui.viewmodel.ItemViewModel;

import java.io.InputStreamReader;

@SuppressWarnings("WeakerAccess")
public class MessageItemStatusViewModel extends ItemViewModel<Message> {
    public static final String STATUS_ROOT_MIME_TYPE = "application/vnd.layer.status+json";
    public static final String RESPONSE_ROOT_MIME_TYPE = "application/vnd.layer.response+json";

    private CharSequence mText;
    private boolean mVisible;
    private final Gson mGson;
    private boolean mEnableReadReceipts;

    public MessageItemStatusViewModel(Context context, LayerClient layerClient) {
        super(context, layerClient);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingStrategy(new AndroidFieldNamingStrategy());
        mGson = gsonBuilder.create();
    }

    public void update() {
        Message message = getItem();
        MessagePart statusPart = null;
        for (MessagePart part : message.getMessageParts()) {
            String mimeType = MessagePartUtils.getMimeType(part);
            if (mimeType == null) {
                continue;
            }
            switch (mimeType) {
                case STATUS_ROOT_MIME_TYPE:
                    statusPart = part;
                    break;
            }
        }

        if (statusPart != null) {
            mVisible = true;
            JsonReader reader = new JsonReader(new InputStreamReader(statusPart.getDataStream()));
            StatusMetadata metadata = mGson.fromJson(reader, StatusMetadata.class);
            mText = metadata.getText();
        } else {
            mVisible = false;
        }

        boolean myMessage = getItem().getSender().equals(getLayerClient().getAuthenticatedUser());
        if (!myMessage && mEnableReadReceipts) {
            message.markAsRead();
        }
    }

    public void setEnableReadReceipts(boolean enableReadReceipts) {
        mEnableReadReceipts = enableReadReceipts;
    }

    @Bindable
    public CharSequence getText() {
        return mText;
    }

    @Bindable
    public boolean isVisible() {
        return mVisible;
    }
}
