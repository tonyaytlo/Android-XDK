package com.layer.ui.message.choice;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.layer.ui.message.model.Action;

import java.util.List;

public class ChoiceMessageMetadata extends BaseObservable {
    public static final String CHOICE_TYPE_STANDARD = "standard";
    public static final String DEFAULT_RESPONSE_NAME = "selection";

    @SerializedName("title")
    private String mTitle;

    @SerializedName("label")
    private String mLabel;

    @SerializedName("choices")
    private List<ChoiceModel> mChoices;

    @SerializedName("type")
    private String mType;

    @SerializedName("expanded_type")
    private String mExpandedType;

    @SerializedName("allow_reselect")
    private boolean mAllowReselect;

    @SerializedName("allow_delselect")
    private boolean mAllowDeselect;

    @SerializedName("allow_multiselect")
    private boolean mAllowMultiselect;

    @SerializedName("enabled_for")
    private List<String> mEnabledFor;

    @SerializedName("response_name")
    private String mResponseName;

    @SerializedName("custom_response_data")
    private JsonObject mCustomResponseData;

    @SerializedName("preselected_choice")
    private String mPreselectedChoice;

    @SerializedName("action")
    private Action mAction;

    @SerializedName("custom_data")
    private JsonObject mCustomData;

    @Bindable
    public String getTitle() {
        return mTitle;
    }

    @Bindable
    public String getLabel() {
        return mLabel;
    }

    @Bindable
    public List<ChoiceModel> getChoices() {
        return mChoices;
    }

    @NonNull
    @Bindable
    public String getType() {
        if (mType != null) {
            return mType;
        } else {
            return CHOICE_TYPE_STANDARD;
        }
    }

    @Bindable
    public String getExpandedType() {
        return mExpandedType;
    }

    @Bindable
    public boolean getAllowReselect() {
        return mAllowReselect;
    }

    @Bindable
    public boolean getAllowDeselect() {
        return mAllowDeselect;
    }

    @Bindable
    public boolean getAllowMultiselect() {
        return mAllowMultiselect;
    }

    @Bindable
    public List<String> getEnabledFor() {
        return mEnabledFor;
    }

    @Bindable
    public String getResponseName() {
        if (TextUtils.isEmpty(mResponseName)) {
            return DEFAULT_RESPONSE_NAME;
        } else {
            return mResponseName;
        }
    }

    @Bindable
    public JsonObject getCustomResponseData() {
        return mCustomResponseData;
    }

    @Bindable
    public String getPreselectedChoice() {
        return mPreselectedChoice;
    }

    @Bindable
    public Action getAction() {
        return mAction;
    }

    @Bindable
    public JsonObject getCustomData() {
        return mCustomData;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public void setChoices(List<ChoiceModel> choices) {
        mChoices = choices;
    }

    public void setType(String type) {
        mType = type;
    }

    public void setExpandedType(String expandedType) {
        mExpandedType = expandedType;
    }

    public void setAllowReselect(boolean allowReselect) {
        this.mAllowReselect = allowReselect;
    }

    public void setAllowDeselect(boolean allowDeselect) {
        this.mAllowDeselect = allowDeselect;
    }

    public void setAllowMultiselect(boolean allowMultiselect) {
        mAllowMultiselect = allowMultiselect;
    }

    public void setEnabledFor(List<String> enabledFor) {
        mEnabledFor = enabledFor;
    }

    public void setResponseName(String responseName) {
        mResponseName = responseName;
    }

    public void setCustomResponseData(JsonObject customResponseData) {
        mCustomResponseData = customResponseData;
    }

    public void setPreselectedChoice(String preselectedChoice) {
        mPreselectedChoice = preselectedChoice;
    }

    public void setAction(Action action) {
        mAction = action;
    }

    public void setCustomData(JsonObject customData) {
        mCustomData = customData;
    }
}
