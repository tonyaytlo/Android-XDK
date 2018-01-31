package com.layer.xdk.ui.message.file;

import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.layer.xdk.ui.message.model.Action;

import java.util.Date;

public class FileMessageMetadata {

    @SerializedName("author")
    private String mAuthor;

    @SerializedName("created_at")
    private Date mCreatedAt;

    @SerializedName("comment")
    private String mComment;

    @SerializedName("size")
    private long mSize;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("mime_type")
    private String mMimeType;

    @SerializedName("updated_at")
    private Date mUpdatedAt;

    @SerializedName("source_url")
    private String mSourceUrl;

    @SerializedName("action")
    private Action mAction;

    @SerializedName("custom_data")
    private JsonObject mCustomData;

    @Nullable
    public String getAuthor() {
        return mAuthor;
    }

    @Nullable
    public Date getCreatedAt() {
        return mCreatedAt;
    }

    @Nullable
    public String getComment() {
        return mComment;
    }

    public long getSize() {
        return mSize;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getMimeType() {
        return mMimeType;
    }

    @Nullable
    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    @Nullable
    public String getSourceUrl() {
        return mSourceUrl;
    }

    @Nullable
    public Action getAction() {
        return mAction;
    }

    @Nullable
    public JsonObject getCustomData() {
        return mCustomData;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public void setSize(Long size) {
        mSize = size;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public void setUpdatedAt(Date updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public void setSourceUrl(String sourceUrl) {
        mSourceUrl = sourceUrl;
    }

    public void setAction(Action action) {
        mAction = action;
    }

    public void setCustomData(JsonObject customData) {
        mCustomData = customData;
    }
}
