package com.layer.ui.message.button;

import android.databinding.BaseObservable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

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
    private List<Choice> mChoices;

    @SerializedName("data")
    private JsonObject mData;

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

    public List<Choice> getChoices() {
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

    public void setChoices(List<Choice> choices) {
        mChoices = choices;
    }

    public void setData(JsonObject data) {
        mData = data;
    }

    public static class Choice {
        @SerializedName("id")
        private String mId;

        @SerializedName("text")
        private String mText;

        public String getId() {
            return mId;
        }

        public String getText() {
            return mText;
        }

        public void setId(String id) {
            mId = id;
        }

        public void setText(String text) {
            mText = text;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof Choice)) return false;

            Choice other = (Choice) obj;
            return this.mId.equals(other.mId);
        }
    }
}
