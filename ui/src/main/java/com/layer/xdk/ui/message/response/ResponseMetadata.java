package com.layer.xdk.ui.message.response;


import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Metadata for a response message
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ResponseMetadata {

    @SerializedName("response_to")
    public String mMessageIdToRespondTo;

    @SerializedName("response_to_node_id")
    public String mPartIdToRespondTo;

    @SerializedName("participant_data")
    public Map<Object, Object> mParticipantData;
}
