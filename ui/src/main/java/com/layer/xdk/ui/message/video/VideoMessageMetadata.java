package com.layer.xdk.ui.message.video;

import android.support.annotation.Dimension;

import com.google.gson.annotations.SerializedName;
import com.layer.xdk.ui.message.model.Action;
import com.layer.xdk.ui.util.DisplayUtils;
import com.layer.xdk.ui.util.Log;

import java.util.Date;

public class VideoMessageMetadata {

    @SerializedName("title")
    public String mTitle;

    @SerializedName("artist")
    public String mArtist;

    @SerializedName("aspect_ratio")
    public Double mAspectRatio;

    @SerializedName("created_at")
    public Date mCreatedAt;

    @SerializedName("width")
    public int mWidth;

    @SerializedName("height")
    public int mHeight;

    @SerializedName("mime_type")
    public String mMimeType;

    @SerializedName("preview_url")
    public String mPreviewUrl;

    @SerializedName("subtitle")
    public String mSubtitle;

    @SerializedName("size")
    public long mSize;

    @SerializedName("source_url")
    public String mSourceUrl;

    @SerializedName("preview_width")
    public int mPreviewWidth;

    @SerializedName("preview_height")
    public int mPreviewHeight;

    @SerializedName("duration")
    public double mDuration;

    @SerializedName("action")
    public Action mAction;

    /**
     * @return the preview width in pixels
     */
    @Dimension
    public int getPreviewWidth() {
        return DisplayUtils.dpToPx(mPreviewWidth);
    }

    /**
     * @return the preview height in pixels
     */
    @Dimension
    public int getPreviewHeight() {
        return DisplayUtils.dpToPx(mPreviewHeight);
    }

    /**
     * @return the video width in pixels
     */
    @Dimension
    public int getWidth() {
        return DisplayUtils.dpToPx(mWidth);
    }

    /**
     * @return the video height in pixels
     */
    @Dimension
    public int getHeight() {
        return DisplayUtils.dpToPx(mHeight);
    }

    /**
     * @return the video aspect ratio. If no aspect ratio defined then it will be calculated from
     * the width and height.
     */
    public double getAspectRatio() {
        if (mAspectRatio != null) {
            return mAspectRatio;
        } else if (mHeight != 0) {
            return (double) mWidth / mHeight;
        } else {
            if (Log.isLoggable(Log.WARN)) {
                Log.w("A non-zero height should be used if there is no defined aspect ratio");
            }
            return 0;
        }
    }
}
