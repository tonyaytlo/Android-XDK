package com.layer.ui.message.location;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.layer.ui.message.model.Action;

import org.json.JSONObject;

public class LocationMessageMetadata {

    @SerializedName("accuracy")
    private Double mAccuracy;

    @SerializedName("heading")
    private Double mHeading;
    @SerializedName("altitude")
    private Double mAltitude;
    @SerializedName("latitude")
    private Double mLatitude;
    @SerializedName("longitude")
    private Double mLongitude;

    @SerializedName("street1")
    private String mStreet1;
    @SerializedName("street2")
    private String mStreet2;
    @SerializedName("city")
    private String mCity;
    @SerializedName("administrative_area")
    private String mAdministrativeArea;
    @SerializedName("country")
    private String mCountry;
    @SerializedName("postal_code")
    private String mPostalCode;

    @SerializedName("zoom")
    private Integer mZoom;

    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;

    @SerializedName("custom_data")
    private JSONObject mCustomData;

    @SerializedName("action")
    private Action mAction;

    @Nullable
    public Double getAccuracy() {
        return mAccuracy;
    }

    @Nullable
    public Double getHeading() {
        return mHeading;
    }

    public Double getAltitude() {
        return mAltitude != null ? mAltitude : 0.0f;
    }

    @Nullable
    public Double getLatitude() {
        return mLatitude;
    }

    @Nullable
    public Double getLongitude() {
        return mLongitude;
    }

    @Nullable
    public String getStreet1() {
        return mStreet1;
    }

    @Nullable
    public String getStreet2() {
        return mStreet2;
    }

    @Nullable
    public String getCity() {
        return mCity;
    }

    @Nullable
    public String getAdministrativeArea() {
        return mAdministrativeArea;
    }

    @Nullable
    public String getCountry() {
        return mCountry;
    }

    @Nullable
    public String getPostalCode() {
        return mPostalCode;
    }

    public Integer getZoom() {
        return mZoom != null ? mZoom : 17;
    }

    @Nullable
    public String getCreatedAt() {
        return mCreatedAt;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    @Nullable
    public JSONObject getCustomData() {
        return mCustomData;
    }

    @Nullable
    public Action getAction() {
        return mAction;
    }

    @Nullable
    public String getFormattedAddress() {
        StringBuilder formattedAddress = new StringBuilder();

        if (mStreet1 != null) {
            formattedAddress.append(mStreet1).append(" ");
        }

        if (mStreet2 != null) {
            formattedAddress.append(mStreet2).append("\n");
        }

        if (mCity != null) {
            if (formattedAddress.length() > 0) formattedAddress.append("\n");
            formattedAddress.append(mCity).append(" ");
        }

        if (mAdministrativeArea != null) {
            formattedAddress.append(mAdministrativeArea).append(" ");
        }

        if (mPostalCode != null) {
            formattedAddress.append(mPostalCode).append(" ");
        }

        if (mCountry != null) {
            if (formattedAddress.length() > 0) formattedAddress.append("\n");
            formattedAddress.append("\n").append(mCountry);
        }

        return formattedAddress.length() > 0 ? formattedAddress.toString() : null;
    }

    public void setAccuracy(Double accuracy) {
        mAccuracy = accuracy;
    }

    public void setHeading(Double heading) {
        mHeading = heading;
    }

    public void setAltitude(Double altitude) {
        mAltitude = altitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    public void setStreet1(String street1) {
        mStreet1 = street1;
    }

    public void setStreet2(String street2) {
        mStreet2 = street2;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public void setAdministrativeArea(String administrativeArea) {
        mAdministrativeArea = administrativeArea;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public void setPostalCode(String postalCode) {
        mPostalCode = postalCode;
    }

    public void setZoom(Integer zoom) {
        mZoom = zoom;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setCustomData(JSONObject customData) {
        mCustomData = customData;
    }

    public void setAction(Action action) {
        mAction = action;
    }
}
