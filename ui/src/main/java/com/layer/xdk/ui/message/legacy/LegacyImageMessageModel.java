package com.layer.xdk.ui.message.legacy;


import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
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
import java.util.HashSet;
import java.util.Set;

public class LegacyImageMessageModel extends LegacyMessageModel {
    public final static Set<String> SINGLE_PART_MIME_TYPES = Collections.singleton(
            LegacyImageConstants.MIME_TYPE_IMAGE_PREFIX);
    public final static Set<String> THREE_PART_MIME_TYPES = new HashSet<>(3);
    static {
        THREE_PART_MIME_TYPES.add(LegacyImageConstants.MIME_TYPE_INFO);
        THREE_PART_MIME_TYPES.add(LegacyImageConstants.MIME_TYPE_PREVIEW);
        THREE_PART_MIME_TYPES.add(LegacyImageConstants.MIME_TYPE_IMAGE_PREFIX);
    }
    private static final String IMAGE_CACHING_TAG = LegacyImageMessageModel.class.getSimpleName();
    private static final int PLACEHOLDER = R.drawable.xdk_ui_message_item_cell_placeholder;

    private static ImageCacheWrapper sImageCacheWrapper;

    private ImageRequestParameters mImageRequestParameters;
    private Info mInfo;

    public LegacyImageMessageModel(Context context, LayerClient layerClient, Message message) {
        super(context, layerClient, message);
        parseContent();
    }

    @Override
    protected String createMimeTypeTree() {
        StringBuilder sb = new StringBuilder();
        boolean prependComma = false;
        for (MessagePart part : getMessage().getMessageParts()) {
            if (prependComma) {
                sb.append(",");
            }
            if (part.getMimeType().startsWith(LegacyImageConstants.MIME_TYPE_IMAGE_PREFIX)
                    && !part.getMimeType().equals(LegacyImageConstants.MIME_TYPE_PREVIEW)) {
                sb.append(LegacyImageConstants.MIME_TYPE_IMAGE_PREFIX);
            } else {
                sb.append(part.getMimeType());
            }
            sb.append("[]");
            prependComma = true;
        }
        return sb.toString();
    }

    private void parseContent() {
        ImageMessageParts parts = new ImageMessageParts(getMessage());

        try {
            mInfo = new Info();
            if (parts.getInfoPart() != null) {
                JSONObject infoObject = new JSONObject(new String(parts.getInfoPart().getData()));
                mInfo.orientation = infoObject.getInt("orientation");
                mInfo.width = infoObject.getInt("width");
                mInfo.height = infoObject.getInt("height");
            }
            if (parts.getPreviewPart() != null) {
                mInfo.previewPartId = parts.getPreviewPart().getId();
            }
            mInfo.fullPartId = parts.getFullPart().getId();

            createRequestParameters(parts);

        } catch (JSONException e) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e(e.getMessage(), e);
            }
        }
    }

    private void createRequestParameters(ImageMessageParts parts) {
        Uri uri;
        if (parts.getPreviewPart() != null) {
            uri = parts.getPreviewPart().getId();
        } else {
            uri = parts.getFullPart().getId();
        }
        ImageRequestParameters.Builder builder = new ImageRequestParameters.Builder(uri)
                .placeHolder(PLACEHOLDER)
                .tag(IMAGE_CACHING_TAG)
                .centerCrop(false)
                .onlyScaleDown(false)
                .defaultCircularTransform(true);
        if (mInfo.width != null && mInfo.height != null) {
            builder.resize(mInfo.width, mInfo.height);
        }
        if (mInfo.orientation != null) {
            builder.exifOrientation(mInfo.orientation);
        }
        mImageRequestParameters = builder.build();
    }

    public Info getInfo() {
        return mInfo;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.xdk_ui_legacy_image_message_view;
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
        return getContext().getString(R.string.xdk_ui_message_preview_image);
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
        return mImageRequestParameters;
    }

    public static class Info implements Parcelable {
        public Integer orientation;
        public Integer width;
        public Integer height;
        Uri fullPartId;
        Uri previewPartId;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(orientation);
            dest.writeInt(width);
            dest.writeInt(height);
        }

        public static final Parcelable.Creator<Info> CREATOR = new Parcelable.Creator<Info>() {
            public Info createFromParcel(Parcel in) {
                Info info = new Info();
                info.orientation = in.readInt();
                info.width = in.readInt();
                info.height = in.readInt();
                return info;
            }

            public Info[] newArray(int size) {
                return new Info[size];
            }
        };
    }

    private static class ImageMessageParts {
        private MessagePart mInfoPart;
        private MessagePart mPreviewPart;
        private MessagePart mFullPart;

        ImageMessageParts(Message message) {
            Set<MessagePart> messageParts = message.getMessageParts();

            for (MessagePart part : messageParts) {
                if (part.getMimeType().equals(LegacyImageConstants.MIME_TYPE_INFO)) {
                    mInfoPart = part;
                } else if (part.getMimeType().equals(LegacyImageConstants.MIME_TYPE_PREVIEW)) {
                    mPreviewPart = part;
                } else if (part.getMimeType().startsWith("image/")) {
                    mFullPart = part;
                }
            }

            if (messageParts.size() == 3
                    && (mInfoPart == null || mPreviewPart == null || mFullPart == null)) {
                if (Log.isLoggable(Log.ERROR)) {
                    Log.e("Incorrect parts for a three part image: " + messageParts);
                }
                throw new IllegalArgumentException("Incorrect parts for a three part image: " + messageParts);
            }
        }

        @Nullable
        MessagePart getInfoPart() {
            return mInfoPart;
        }

        @Nullable
        MessagePart getPreviewPart() {
            return mPreviewPart;
        }

        @NonNull
        MessagePart getFullPart() {
            return mFullPart;
        }
    }
}
