package com.layer.xdk.ui.message.link;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;
import com.layer.xdk.ui.util.imagecache.ImageRequestParameters;
import com.layer.xdk.ui.util.imagecache.PicassoImageCacheWrapper;
import com.layer.xdk.ui.util.imagecache.requesthandlers.MessagePartRequestHandler;
import com.layer.xdk.ui.util.json.AndroidFieldNamingStrategy;
import com.squareup.picasso.Picasso;

import java.io.InputStreamReader;

public class LinkMessageModel extends MessageModel {

    public static final String ROOT_MIME_TYPE = "application/vnd.layer.link+json";
    private static final String ACTION_OPEN_URL = "open-url";

    private static ImageCacheWrapper sImageCacheWrapper;

    private LinkMessageMetadata mLinkMessageMetadata;
    private Gson mGson;

    public LinkMessageModel(Context context, LayerClient layerClient) {
        super(context, layerClient);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingStrategy(new AndroidFieldNamingStrategy());
        mGson = gsonBuilder.create();
    }

    @Override
    public Class<LinkMessageView> getRendererType() {
        return LinkMessageView.class;
    }

    @Override
    protected void parse(MessagePart messagePart) {
        JsonReader reader = new JsonReader(new InputStreamReader(messagePart.getDataStream()));
        mLinkMessageMetadata = mGson.fromJson(reader, LinkMessageMetadata.class);
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(MessagePart messagePart) {
        return true;
    }

    @Nullable
    @Override
    public String getTitle() {
        return mLinkMessageMetadata != null ? mLinkMessageMetadata.getTitle() : null;
    }

    @Nullable
    @Override
    public String getDescription() {
        return mLinkMessageMetadata != null ? mLinkMessageMetadata.getDescription() : null;
    }

    @Nullable
    @Override
    public String getFooter() {
        return mLinkMessageMetadata != null ? mLinkMessageMetadata.getAuthor() : null;
    }

    @Override
    public String getActionEvent() {
        if (super.getActionEvent() != null) {
            return super.getActionEvent();
        }

        if (mLinkMessageMetadata != null && mLinkMessageMetadata.getAction() != null) {
            return mLinkMessageMetadata.getAction().getEvent();
        }

        return ACTION_OPEN_URL;
    }

    @Override
    public JsonObject getActionData() {
        if (super.getActionData().size() > 0) {
            return super.getActionData();
        }

        JsonObject actionData;
        if (mLinkMessageMetadata != null) {
            if (mLinkMessageMetadata.getAction() != null) {
                actionData = mLinkMessageMetadata.getAction().getData();
            } else {
                actionData = new JsonObject();
                actionData.addProperty("url", mLinkMessageMetadata.getUrl());
            }
        } else {
            actionData = super.getActionData();
        }

        return actionData;
    }

    @Override
    public int getBackgroundColor() {
        return isMessageFromMe() ? R.color.xdk_ui_text_message_view_background_me : R.color.xdk_ui_text_message_view_background_them;
    }

    @Override
    public boolean getHasContent() {
        return mLinkMessageMetadata != null;
    }

    @Nullable
    @Override
    public String getPreviewText() {
        String title = getTitle();
        return title != null ? title : getContext().getString(R.string.xdk_ui_link_message_preview_text);
    }

    public LinkMessageMetadata getMetadata() {
        return mLinkMessageMetadata;
    }

    public ImageCacheWrapper getImageCacheWrapper() {
        if (sImageCacheWrapper == null) {
            sImageCacheWrapper = new PicassoImageCacheWrapper(new Picasso.Builder(getContext())
                    .addRequestHandler(new MessagePartRequestHandler(getLayerClient()))
                    .build());
        }
        return sImageCacheWrapper;
    }

    public static void setImageCacheWrapper(ImageCacheWrapper imageCacheWrapper) {
        sImageCacheWrapper = imageCacheWrapper;
    }

    public ImageRequestParameters getImageRequestParameters() {
        if (getHasContent()) {
            ImageRequestParameters.Builder builder = new ImageRequestParameters.Builder();
            if (mLinkMessageMetadata.getImageUrl() != null) {
                builder.url(mLinkMessageMetadata.getImageUrl());
            } else {
                return null;
            }

            builder.fit(true)
                    .tag(getClass().getSimpleName());

            return builder.build();
        }

        return null;
    }
}
