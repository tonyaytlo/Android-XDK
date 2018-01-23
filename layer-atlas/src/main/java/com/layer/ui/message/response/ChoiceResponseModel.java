package com.layer.ui.message.response;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Collection;
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
     * Adds choices to the participant data map
     *
     * @param responseName key for the map entry
     * @param choiceIds values for the map entry
     */
    public void addChoices(@NonNull String responseName, @NonNull Collection<String> choiceIds) {
        Map<Object, Object> participantData = getParticipantData();
        if (participantData == null) {
            participantData = new HashMap<>(1);
            setParticipantData(participantData);
        }

        participantData.put(responseName, TextUtils.join(",", choiceIds));
    }
}
