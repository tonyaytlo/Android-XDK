package com.layer.xdk.ui.message.response;


import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ResponseSummary {

    @SerializedName("participant_data")
    public Map<String, JsonObject> mParticipantData;

    public boolean hasData() {
        return mParticipantData != null && !mParticipantData.isEmpty();
    }
}