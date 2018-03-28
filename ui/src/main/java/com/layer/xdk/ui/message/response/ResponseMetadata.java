package com.layer.xdk.ui.message.response;


import com.google.gson.annotations.SerializedName;
import com.layer.xdk.ui.message.response.crdt.OrOperationResult;

import java.util.List;

/**
 * Metadata for a response message
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ResponseMetadata {

    @SerializedName("response_to")
    public String mMessageIdToRespondTo;

    @SerializedName("response_to_node_id")
    public String mPartIdToRespondTo;

    @SerializedName("changes")
    public List<OrOperationResult> mChanges;
}
