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
import com.layer.xdk.ui.message.messagetypes.threepartimage.ThreePartImageCellFactory;
import com.layer.xdk.ui.util.Log;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;
import com.layer.xdk.ui.util.imagecache.ImageRequestParameters;
import com.layer.xdk.ui.util.imagecache.PicassoImageCacheWrapper;
import com.layer.xdk.ui.util.imagecache.requesthandlers.MessagePartRequestHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class LegacyThreePartImageMessageModel extends LegacyMessageModel {
    public final static Set<String> MIME_TYPES = new HashSet<>(3);
    static {
        MIME_TYPES.add(ThreePartImageConstants.MIME_TYPE_INFO);
        MIME_TYPES.add(ThreePartImageConstants.MIME_TYPE_PREVIEW);
        MIME_TYPES.add(ThreePartImageConstants.MIME_TYPE_IMAGE_PREFIX);
    }
    private static final String IMAGE_CACHING_TAG = LegacyThreePartImageMessageModel.class.getSimpleName();
    private static final int PLACEHOLDER = R.drawable.xdk_ui_message_item_cell_placeholder;

    private static ImageCacheWrapper sImageCacheWrapper;

    private ImageRequestParameters mImageRequestParameters;
    private Info mInfo;

    public LegacyThreePartImageMessageModel(Context context,
            LayerClient layerClient,
            Message message) {
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
            if (part.getMimeType().startsWith(ThreePartImageConstants.MIME_TYPE_IMAGE_PREFIX)
                    && !part.getMimeType().equals(ThreePartImageConstants.MIME_TYPE_PREVIEW)) {
                sb.append(ThreePartImageConstants.MIME_TYPE_IMAGE_PREFIX);
            } else {
                sb.append(part.getMimeType());
            }
            sb.append("[]");
            prependComma = true;
        }
        return sb.toString();
    }

    private void parseContent() {
        ThreePartMessageParts parts = new ThreePartMessageParts(getMessage());

        try {
            mInfo = new Info();
            JSONObject infoObject = new JSONObject(new String(parts.getInfoPart().getData()));
            mInfo.orientation = infoObject.getInt("orientation");
            mInfo.width = infoObject.getInt("width");
            mInfo.height = infoObject.getInt("height");
            mInfo.previewPartId = parts.getPreviewPart().getId();
            mInfo.fullPartId = parts.getFullPart().getId();

            createRequestParameters(parts);

        } catch (JSONException e) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e(e.getMessage(), e);
            }
        }
    }

    private void createRequestParameters(ThreePartMessageParts parts) {
        mImageRequestParameters = new ImageRequestParameters
                .Builder(parts.getPreviewPart().getId())
                .placeHolder(PLACEHOLDER)
                .resize(mInfo.width, mInfo.height)
                .tag(IMAGE_CACHING_TAG)
                .exifOrientation(mInfo.orientation)
                .centerCrop(false)
                .onlyScaleDown(false)
                .defaultCircularTransform(true)
                .build();
    }

    public Info getInfo() {
        return mInfo;
    }

    @Override
    public int getViewLayoutId() {
        return R.layout.xdk_ui_legacy_three_part_image_message_view;
    }

    @Override
    public int getContainerViewLayoutId() {
        return R.layout.xdk_ui_standard_message_container;
    }

    @Override
    protected boolean shouldDownloadContentIfNotReady(@NonNull MessagePart messagePart) {
        return false;
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
        public int orientation;
        public int width;
        public int height;
        public Uri fullPartId;
        public Uri previewPartId;

        public int sizeOf() {
            return ((Integer.SIZE + Integer.SIZE + Integer.SIZE) / Byte.SIZE) + fullPartId.toString().getBytes().length + previewPartId.toString().getBytes().length;
        }

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

        public static final Parcelable.Creator<ThreePartImageCellFactory.Info> CREATOR
                = new Parcelable.Creator<ThreePartImageCellFactory.Info>() {
            public ThreePartImageCellFactory.Info createFromParcel(Parcel in) {
                ThreePartImageCellFactory.Info info = new ThreePartImageCellFactory.Info();
                info.orientation = in.readInt();
                info.width = in.readInt();
                info.height = in.readInt();
                return info;
            }

            public ThreePartImageCellFactory.Info[] newArray(int size) {
                return new ThreePartImageCellFactory.Info[size];
            }
        };
    }

    private static class ThreePartMessageParts {
        private MessagePart mInfoPart;
        private MessagePart mPreviewPart;
        private MessagePart mFullPart;

        public ThreePartMessageParts(Message message) {
            Set<MessagePart> messageParts = message.getMessageParts();

            for (MessagePart part : messageParts) {
                if (part.getMimeType().equals(ThreePartImageConstants.MIME_TYPE_INFO)) {
                    mInfoPart = part;
                } else if (part.getMimeType().equals(ThreePartImageConstants.MIME_TYPE_PREVIEW)) {
                    mPreviewPart = part;
                } else if (part.getMimeType().startsWith("image/")) {
                    mFullPart = part;
                }
            }

            if (mInfoPart == null || mPreviewPart == null || mFullPart == null) {
                if (Log.isLoggable(Log.ERROR)) {
                    Log.e("Incorrect parts for a three part image: " + messageParts);
                }
                throw new IllegalArgumentException("Incorrect parts for a three part image: " + messageParts);
            }
        }

        @NonNull
        public MessagePart getInfoPart() {
            return mInfoPart;
        }

        @NonNull
        public MessagePart getPreviewPart() {
            return mPreviewPart;
        }

        @NonNull
        public MessagePart getFullPart() {
            return mFullPart;
        }
    }
}
