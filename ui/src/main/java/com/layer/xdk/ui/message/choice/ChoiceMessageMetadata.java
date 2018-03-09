package com.layer.xdk.ui.message.choice;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.layer.xdk.ui.message.model.Action;

import java.util.List;

/**
 * This metadata class represents a choice message. Since it contains a
 * {@link ChoiceConfigMetadata}, it merely subclasses that class and adds the necessary fields.
 */
public class ChoiceMessageMetadata extends ChoiceConfigMetadata {
    public static final String CHOICE_TYPE_STANDARD = "standard";

    @SerializedName("title")
    private String mTitle;

    @SerializedName("label")
    private String mLabel;

    @SerializedName("choices")
    private List<ChoiceMetadata> mChoices;

    @SerializedName("type")
    private String mType;

    @SerializedName("expanded_type")
    private String mExpandedType;

    @SerializedName("action")
    private Action mAction;

    @SerializedName("custom_data")
    private JsonObject mCustomData;

    public String getTitle() {
        return mTitle;
    }

    public String getLabel() {
        return mLabel;
    }

    public List<ChoiceMetadata> getChoices() {
        return mChoices;
    }

    @NonNull
    public String getType() {
        if (mType != null) {
            return mType;
        } else {
            return CHOICE_TYPE_STANDARD;
        }
    }

    public String getExpandedType() {
        return mExpandedType;
    }

    public Action getAction() {
        return mAction;
    }

    public JsonObject getCustomData() {
        return mCustomData;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public void setChoices(List<ChoiceMetadata> choices) {
        mChoices = choices;
    }

    public void setType(String type) {
        mType = type;
    }

    public void setExpandedType(String expandedType) {
        mExpandedType = expandedType;
    }

    public void setAction(Action action) {
        mAction = action;
    }

    public void setCustomData(JsonObject customData) {
        mCustomData = customData;
    }
}
