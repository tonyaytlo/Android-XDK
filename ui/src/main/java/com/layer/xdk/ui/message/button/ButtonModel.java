package com.layer.xdk.ui.message.button;

import android.databinding.BaseObservable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.layer.xdk.ui.message.choice.ChoiceMetadata;

import java.util.List;

public class ButtonModel extends BaseObservable{
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

    private transient ChoiceData mChoiceData;

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

    public ChoiceData getChoiceData() {
        return mChoiceData;
    }

    public void setChoiceData(ChoiceData choiceData) {
        mChoiceData = choiceData;
    }

    public class ChoiceData {

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

        private transient boolean mEnabledForMe;

        public String getResponseName() {
            return mResponseName;
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
    }

}
