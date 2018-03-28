package com.layer.xdk.ui.message.response;


import android.net.Uri;
import android.support.annotation.NonNull;

import com.layer.xdk.ui.message.response.crdt.OrOperationResult;

import java.util.List;
import java.util.UUID;

/**
 * Model class for creating a choice response message.
 */
@SuppressWarnings("WeakerAccess")
public class ChoiceResponseModel extends ResponseModel {
    private final String mStatusText;

    /**
     * @param messageIdToRespondTo full ID of the message this is in response to
     * @param partIdToRespondTo UUID of the message part this is in response to
     * @param statusText text to use for the status message part
     * @param results list of OR set operations that resulted in the desired state
     */
    public ChoiceResponseModel(@NonNull Uri messageIdToRespondTo,
            @NonNull UUID partIdToRespondTo, @NonNull String statusText,
            List<OrOperationResult> results) {
        super(messageIdToRespondTo, partIdToRespondTo, results);
        mStatusText = statusText;
    }

    /**
     * @return text to be displayed in the status message
     */
    @NonNull
    public String getStatusText() {
        return mStatusText;
    }
}
