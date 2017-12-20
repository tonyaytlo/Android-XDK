package com.layer.ui.message.link;

import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.layer.ui.message.model.Action;

public class LinkMessageMetadata {

    @SerializedName("author")
    private String mAuthor;
    @SerializedName("description")
    private String description;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("image_url")
    private String mImageUrl;
    @SerializedName("url")
    private String mUrl;
    @SerializedName("action")
    private Action mAction;
    @SerializedName("custom_data")
    private JsonObject mCustomData;

    @Nullable
    public String getAuthor() {
        return mAuthor;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getImageUrl() {
        return mImageUrl;
    }

    @Nullable
    public String getUrl() {
        return mUrl;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setAction(Action action) {
        mAction = action;
    }

    public void setCustomData(JsonObject customData) {
        mCustomData = customData;
    }
}
