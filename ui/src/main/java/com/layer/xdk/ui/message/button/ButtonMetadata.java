package com.layer.xdk.ui.message.button;

import android.databinding.BaseObservable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.layer.xdk.ui.message.choice.ChoiceMetadata;
import com.layer.xdk.ui.message.choice.ChoiceConfigMetadata;

import java.util.List;

public class ButtonMetadata extends BaseObservable{
    public static final String TYPE_ACTION = "action";
    public static final String TYPE_CHOICE = "choice";

    @SerializedName("type")
    private String mType;

    @SerializedName("text")
    private String mText;

    @SerializedName("event")
    private String mEvent;

    @SerializedName("choices")
    private List<ChoiceMetadata> mChoices;

    @SerializedName("data")
    private JsonObject mData;

    private transient ChoiceConfigMetadata mChoiceConfigMetadata;

    public String getType() {
        return mType;
    }

    public String getText() {
        return mText;
    }

    public String getEvent() {
        return mEvent;
    }

    public JsonObject getData() {
        return mData;
    }

    public List<ChoiceMetadata> getChoices() {
        return mChoices;
    }

    public void setType(String type) {
        mType = type;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setEvent(String event) {
        mEvent = event;
    }

    public void setChoices(List<ChoiceMetadata> choices) {
        mChoices = choices;
    }

    public void setData(JsonObject data) {
        mData = data;
    }

    public ChoiceConfigMetadata getChoiceConfigMetadata() {
        return mChoiceConfigMetadata;
    }

    public void setChoiceConfigMetadata(ChoiceConfigMetadata config) {
        mChoiceConfigMetadata = config;
    }

}
