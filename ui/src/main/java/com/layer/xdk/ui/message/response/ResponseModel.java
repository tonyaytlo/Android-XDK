package com.layer.xdk.ui.message.response;


import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * Model class for creating a response message.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ResponseModel {

    private Uri mMessageIdToRespondTo;
    private UUID mPartIdToRespondTo;
    private Map<Object, Object> mParticipantData;

    public ResponseModel(@Nullable Uri messageIdToRespondTo, @Nullable UUID partIdToRespondTo,
            @Nullable Map<Object, Object> participantData) {
        mMessageIdToRespondTo = messageIdToRespondTo;
        mPartIdToRespondTo = partIdToRespondTo;
        mParticipantData = participantData;
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

    public Map<Object, Object> getParticipantData() {
        return mParticipantData;
    }

    public void setParticipantData(Map<Object, Object> participantData) {
        mParticipantData = participantData;
    }
}
