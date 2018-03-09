package com.layer.xdk.ui.message.choice;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChoiceConfigMetadata {
    public static final String DEFAULT_RESPONSE_NAME = "selection";

    @SerializedName("response_name")
    private String mResponseName;

    @SerializedName("name")
    private String mName;

    @SerializedName("preselected_choice")
    private String mPreselectedChoice;

    @SerializedName("allow_reselect")
    private boolean mAllowReselect;

    @SerializedName("allow_deselect")
    private boolean mAllowDeselect;

    @SerializedName("allow_multiselect")
    private boolean mAllowMultiselect;

    @SerializedName("enabled_for")
    private List<String> mEnabledFor;

    @SerializedName("custom_response_data")
    private JsonObject mCustomResponseData;

    private transient boolean mEnabledForMe;

    @NonNull
    public String getResponseName() {
        if (TextUtils.isEmpty(mResponseName)) {
            return DEFAULT_RESPONSE_NAME;
        } else {
            return mResponseName;
        }
    }

    public void setResponseName(String responseName) {
        mResponseName = responseName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPreselectedChoice() {
        return mPreselectedChoice;
    }

    public void setPreselectedChoice(String preselectedChoice) {
        mPreselectedChoice = preselectedChoice;
    }

    public boolean isAllowReselect() {
        return mAllowReselect;
    }

    public void setAllowReselect(boolean allowReselect) {
        mAllowReselect = allowReselect;
    }

    public boolean isAllowDeselect() {
        return mAllowDeselect;
    }

    public void setAllowDeselect(boolean allowDeselect) {
        mAllowDeselect = allowDeselect;
    }

    public boolean isAllowMultiselect() {
        return mAllowMultiselect;
    }

    public void setAllowMultiselect(boolean allowMultiselect) {
        mAllowMultiselect = allowMultiselect;
    }

    public List<String> getEnabledFor() {
        return mEnabledFor;
    }

    public void setEnabledFor(List<String> enabledFor) {
        mEnabledFor = enabledFor;
    }

    public boolean isEnabledForMe() {
        return mEnabledForMe;
    }

    public void setEnabledForMe(boolean enabledForMe) {
        mEnabledForMe = enabledForMe;
    }

    public void setCustomResponseData(JsonObject customResponseData) {
        mCustomResponseData = customResponseData;
    }

    public JsonObject getCustomResponseData() {
        return mCustomResponseData;
    }
}
