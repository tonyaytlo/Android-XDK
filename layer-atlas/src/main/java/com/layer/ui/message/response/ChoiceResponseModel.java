package com.layer.ui.message.response;


import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Model class for creating a choice response message.
 */
@SuppressWarnings("WeakerAccess")
public class ChoiceResponseModel extends ResponseModel {
    private final String mStatusText;

    public ChoiceResponseModel(@NonNull Uri messageIdToRespondTo,
            @NonNull UUID partIdToRespondTo, @NonNull String statusText) {
        super(messageIdToRespondTo, partIdToRespondTo, null);
        mStatusText = statusText;
    }

    /**
     * @return text to be displayed in the status message
     */
    @NonNull
    public String getStatusText() {
        return mStatusText;
    }

    /**
     * Add a choice to the participant data map
     *
     * @param responseName key for the map entry
     * @param choiceId value for the map entry
     */
    public void addChoice(@NonNull String responseName, @NonNull String choiceId) {
        Map<Object, Object> participantData = getParticipantData();
        if (participantData == null) {
            participantData = new HashMap<>(1);
            setParticipantData(participantData);
        }
        participantData.put(responseName, choiceId);
    }
}
