package com.layer.xdk.ui.message.response;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.xdk.ui.message.response.crdt.OrOperationResult;

import java.util.List;
import java.util.UUID;

/**
 * Model class for creating a response message.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ResponseModel {

    private Uri mMessageIdToRespondTo;
    private UUID mPartIdToRespondTo;
    private List<OrOperationResult> mChanges;

    public ResponseModel(@Nullable Uri messageIdToRespondTo, @Nullable UUID partIdToRespondTo,
            @NonNull List<OrOperationResult> changes) {
        mMessageIdToRespondTo = messageIdToRespondTo;
        mPartIdToRespondTo = partIdToRespondTo;
        mChanges = changes;
    }

    public Uri getMessageIdToRespondTo() {
        return mMessageIdToRespondTo;
    }

    public void setMessageIdToRespondTo(Uri messageIdToRespondTo) {
        mMessageIdToRespondTo = messageIdToRespondTo;
    }

    public UUID getPartIdToRespondTo() {
        return mPartIdToRespondTo;
    }

    public void setPartIdToRespondTo(UUID partIdToRespondTo) {
        mPartIdToRespondTo = partIdToRespondTo;
    }

    /**
     * @return list of OR set operations that are part of this response
     */
    public List<OrOperationResult> getChanges() {
        return mChanges;
    }

    /**
     * Set the OR set operations to send as part of this response.
     *
     * @param changes set of OR set operations that resulted in this change
     */
    public void setChanges(List<OrOperationResult> changes) {
        mChanges = changes;
    }
}
