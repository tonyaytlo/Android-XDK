package com.layer.ui.message.action;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.layer.sdk.LayerClient;

import java.util.Locale;

public class GoogleMapsOpenMapActionHandler extends ActionHandler {

    private static final String ACTION_EVENT = "open-map";

    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_ZOOM = "zoom";
    public static final String KEY_TITLE = "title";

    private static final int DEFAULT_ZOOM = 17;

    public GoogleMapsOpenMapActionHandler(LayerClient layerClient) {
        super(layerClient, ACTION_EVENT);
    }

    @Override
    public void performAction(@NonNull Context context, @Nullable JsonObject data) {
        if (data != null && data.size() > 0) {
            Uri googleMapsUri;

            int zoom = DEFAULT_ZOOM;
            if (data.has(KEY_ZOOM)) {
                zoom = data.get(KEY_ZOOM).getAsInt();
            }

            double latitude = 0.0f;
            double longitude = 0.0f;

            if (data.has(KEY_LATITUDE) && data.has(KEY_LONGITUDE)) {
                latitude = data.get(KEY_LATITUDE).getAsDouble();
                longitude = data.get(KEY_LONGITUDE).getAsDouble();
            }

            String markerTitle = "";
            if (data.has(KEY_TITLE)) {
                markerTitle = data.get(KEY_TITLE).getAsString();
            }

            if (data.has(KEY_ADDRESS)) {
                googleMapsUri = constructGoogleMapsUri(longitude, latitude,
                        data.get(KEY_ADDRESS).getAsString(), markerTitle, zoom);
            } else {
                googleMapsUri = constructGoogleMapsUri(latitude, longitude, markerTitle, zoom);
            }

            Intent openMapsIntent = new Intent(Intent.ACTION_VIEW, googleMapsUri);

            if (openMapsIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(openMapsIntent);
            }
        }
    }

    private Uri constructGoogleMapsUri(double latitude, double longitude, String address, String markerTitle, int zoom) {
        String queryString = String.format(Locale.US, "geo:%f,%f?q=%s&z=%d(%s)",
                latitude, longitude, Uri.encode(address), zoom, markerTitle);
        return Uri.parse(queryString);
    }

    private Uri constructGoogleMapsUri(double latitude, double longitude, String markerTitle, int zoom) {
        String queryString = String.format(Locale.US, "geo:%f,%f?z=%d(%s)", latitude, longitude, zoom, markerTitle);
        return Uri.parse(queryString);
    }
}
