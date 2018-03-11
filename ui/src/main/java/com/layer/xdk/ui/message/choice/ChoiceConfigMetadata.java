package com.layer.xdk.ui.message.choice;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("WeakerAccess") // For Gson serialization/de-serialization
public class ChoiceConfigMetadata {
    public static final String DEFAULT_RESPONSE_NAME = "selection";

    @SerializedName("response_name")
    public String mResponseName;

    @SerializedName("name")
    public String mName;

    @SerializedName("preselected_choice")
    public String mPreselectedChoice;

    @SerializedName("allow_reselect")
    public boolean mAllowReselect;

    @SerializedName("allow_deselect")
    public boolean mAllowDeselect;

    @SerializedName("allow_multiselect")
    public boolean mAllowMultiselect;

    @SerializedName("enabled_for")
    public List<String> mEnabledFor;

    @SerializedName("custom_response_data")
    public JsonObject mCustomResponseData;

    public transient boolean mEnabledForMe;

    @NonNull
    public String getResponseName() {
        if (TextUtils.isEmpty(mResponseName)) {
            return DEFAULT_RESPONSE_NAME;
        } else {
            return mResponseName;
        }
    }
}
