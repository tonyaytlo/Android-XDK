package com.layer.ui.message.location;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.MessagePart;
import com.layer.ui.R;
import com.layer.ui.message.model.MessageModel;
import com.layer.ui.util.imagecache.ImageCacheWrapper;
import com.layer.ui.util.imagecache.ImageRequestParameters;
import com.layer.ui.util.imagecache.PicassoImageCacheWrapper;
import com.layer.ui.util.imagecache.requesthandlers.MessagePartRequestHandler;
import com.layer.ui.util.json.AndroidFieldNamingStrategy;
import com.squareup.picasso.Picasso;

import java.io.InputStreamReader;

public class LocationMessageModel extends MessageModel {
    public static final String ROOT_MIME_TYPE = "application/vnd.layer.location+json";

    private static final String ACTION_EVENT_OPEN_MAP = "open-map";
    private static final double GOLDEN_RATIO = (1.0 + Math.sqrt(5.0)) / 2.0;
    private static ImageCacheWrapper sImageCacheWrapper;

    private final Gson mGson;

    private LocationMessageMetadata mMetadata;

    public LocationMessageModel(Context context, LayerClient layerClient) {
        super(context, layerClient);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingStrategy(new AndroidFieldNamingStrategy());
        mGson = gsonBuilder.create();
    }

    @Override
    public Class<LocationMessageView> getRendererType() {
        return LocationMessageView.class;
    }

    @Override
    protected void parse(MessagePart messagePart) {
        JsonReader reader = new JsonReader(new InputStreamReader(messagePart.getDataStream()));
        mMetadata = mGson.fromJson(reader, LocationMessageMetadata.class);
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(MessagePart messagePart) {
        return true;
    }

    @Override
    public String getTitle() {
        return mMetadata != null ? mMetadata.getTitle() : null;
    }

    @Nullable
    @Override
    public String getDescription() {
        if (mMetadata != null) {
            if (mMetadata.getDescription() != null) {
                return mMetadata.getDescription();
            } else {
                return mMetadata.getFormattedAddress();
            }
        }

        return null;
    }

    @Override
    public String getFooter() {
        return null;
    }

    @Override
    public String getActionEvent() {
        if (super.getActionEvent() != null) {
            return super.getActionEvent();
        }

        if (mMetadata != null) {
            return mMetadata.getAction() != null ? mMetadata.getAction().getEvent() : ACTION_EVENT_OPEN_MAP;
        }

        return null;
    }

    @Override
    public JsonObject getActionData() {
        if (super.getActionData().size() > 0) {
            return super.getActionData();
        }

        JsonObject actionData;

        if (mMetadata != null) {
            if (mMetadata.getAction() != null) {
                actionData = mMetadata.getAction().getData();
            } else {
                actionData = new JsonObject();
                if (mMetadata.getLongitude() != null && mMetadata.getLatitude() != null) {
                    actionData.addProperty("latitude", mMetadata.getLatitude());
                    actionData.addProperty("longitude", mMetadata.getLongitude());
                } else if (mMetadata.getFormattedAddress() != null) {
                    actionData.addProperty("address", mMetadata.getFormattedAddress());
                }

                if (mMetadata.getTitle() != null) {
                    actionData.addProperty("title", mMetadata.getTitle());
                }
            }
        } else {
            actionData = null;
        }
        return actionData;
    }

    @Override
    public int getBackgroundColor() {
        return R.color.ui_location_message_background;
    }

    @Override
    public boolean getHasContent() {
        return mMetadata != null;
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

    @Nullable
    public ImageRequestParameters getMapImageRequestParameters() {
        if (mMetadata != null) {
            StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?maptype=roadmap&scale=2");

            url.append("&zoom=")
                    .append(mMetadata.getZoom())
                    .append("&markers=");

            // Set location parameters or geocode
            if (mMetadata.getLatitude() != null && mMetadata.getLongitude() != null) {
                url.append(mMetadata.getLatitude()).append(",").append(mMetadata.getLongitude());
            } else if (mMetadata.getFormattedAddress() != null) {
                url.append(mMetadata.getFormattedAddress());
            } else {
                return null;
            }

            // Set dimensions
            // Google Static Map API has max dimension 640
            int mapWidth = (int) getContext().getResources().getDimension(R.dimen.layer_ui_location_message_map_width);
            int mapHeight = (int) Math.round((double) mapWidth / GOLDEN_RATIO);

            url.append("&size=").append(mapWidth).append("x").append(mapHeight);

            ImageRequestParameters.Builder paramsBuilder = new ImageRequestParameters.Builder(Uri.parse(url.toString()));
            paramsBuilder.resize(mapWidth, mapHeight);
            paramsBuilder.centerCrop(true);

            return paramsBuilder.build();
        }

        return null;
    }
}
