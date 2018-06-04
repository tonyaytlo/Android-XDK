package com.layer.xdk.ui.message.audio;

import android.support.annotation.Dimension;

import com.google.gson.annotations.SerializedName;

public class AudioMessageMetadata {
    
    @SerializedName("title")
    public String mTitle;

    @SerializedName("artist")
    public String mArtist;

    @SerializedName("album")
    public String mAlbum;

    @SerializedName("genre")
    public String mGenre;

    @SerializedName("mime_type")
    public String mMimeType;

    @SerializedName("source_url")
    public String mSourceUrl;

    @SerializedName("size")
    public long mSize;

    @SerializedName("preview_url")
    public String mPreviewUrl;

    @Dimension
    @SerializedName("preview_width")
    public int mPreviewWidth;

    @Dimension
    @SerializedName("preview_height")
    public int mPreviewHeight;

    @SerializedName("duration")
    public double mDuration;
}
