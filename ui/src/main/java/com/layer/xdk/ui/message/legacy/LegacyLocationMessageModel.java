package com.layer.xdk.ui.message.legacy;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.util.Log;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;
import com.layer.xdk.ui.util.imagecache.ImageRequestParameters;
import com.layer.xdk.ui.util.imagecache.PicassoImageCacheWrapper;
import com.layer.xdk.ui.util.imagecache.requesthandlers.MessagePartRequestHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Set;

public class LegacyLocationMessageModel extends LegacyMessageModel {
    private static final double GOLDEN_RATIO = (1.0 + Math.sqrt(5.0)) / 2.0;
    private static final String KEY_LATITUDE = "lat";
    private static final String KEY_LONGITUDE = "lon";
    private static final String KEY_LABEL = "label";
    public final static Set<String> MIME_TYPES = Collections.singleton("location/coordinate");


    private static ImageCacheWrapper sImageCacheWrapper;

    private Location mLocation;
    private ImageRequestParameters mMapImageRequestParameters;

    public LegacyLocationMessageModel(Context context, LayerClient layerClient, Message message) {
        super(context, layerClient, message);
        parseContent();
    }

    private void parseContent() {
        try {
            JSONObject o = new JSONObject(new String(getMessage().getMessageParts().iterator().next().getData()));

            mLocation = new Location();
            mLocation.mLatitude = o.optDouble(KEY_LATITUDE, 0);
            mLocation.mLongitude = o.optDouble(KEY_LONGITUDE, 0);
            mLocation.mLabel = o.optString(KEY_LABEL, null);

            createRequestParameters(mLocation);
        } catch (JSONException e) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e(e.getMessage(), e);
            }
        }
    }

    private void createRequestParameters(Location location) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?zoom=16&maptype=roadmap&scale=2");

        url.append("&markers=");

        // Set location parameters or geocode
        url.append(location.mLatitude).append(",").append(location.mLongitude);

        // Set dimensions
        // Google Static Map API has max dimension 640
        int mapWidth = (int) getContext().getResources().getDimension(R.dimen.xdk_ui_location_message_map_width);
        int mapHeight = (int) Math.round((double) mapWidth / GOLDEN_RATIO);

        url.append("&size=").append(mapWidth).append("x").append(mapHeight);

        ImageRequestParameters.Builder paramsBuilder = new ImageRequestParameters.Builder(Uri.parse(url.toString()));
        paramsBuilder.resize(mapWidth, mapHeight);
        paramsBuilder.centerCrop(true);

        mMapImageRequestParameters = paramsBuilder.build();
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.xdk_ui_legacy_location_message_view;
    }

    @Override
    public int getContainerViewLayoutId() {
        return R.layout.xdk_ui_standard_message_container;
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        return true;
    }

    @Nullable
    @Override
    public String getPreviewText() {
        return getContext().getString(R.string.xdk_ui_message_preview_location);
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

    public ImageRequestParameters getMapImageRequestParameters() {
        return mMapImageRequestParameters;
    }

    public Location getLocation() {
        return mLocation;
    }

    static class Location {
        double mLatitude;
        double mLongitude;
        String mLabel;
    }
}
