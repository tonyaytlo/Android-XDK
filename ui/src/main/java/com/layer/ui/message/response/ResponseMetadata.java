package com.layer.ui.message.response;


import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Metadata for a response message
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ResponseMetadata {

    @SerializedName("response_to")
    private String mMessageIdToRespondTo;

    @SerializedName("response_to_node_id")
    private String mPartIdToRespondTo;

    @SerializedName("participant_data")
    private Map<Object, Object> mParticipantData;

    @Nullable
    public String getMessageIdToRespondTo() {
        return mMessageIdToRespondTo;
    }

    public void setMessageIdToRespondTo(@Nullable String messageIdToRespondTo) {
        mMessageIdToRespondTo = messageIdToRespondTo;
    }

    @Nullable
    public String getPartIdToRespondTo() {
        return mPartIdToRespondTo;
    }

    public void setPartIdToRespondTo(@Nullable String partIdToRespondTo) {
        mPartIdToRespondTo = partIdToRespondTo;
    }

    public Map<Object, Object> getParticipantData() {
        return mParticipantData;
    }

    public void setParticipantData(Map<Object, Object> participantData) {
        mParticipantData = participantData;
    }
}
