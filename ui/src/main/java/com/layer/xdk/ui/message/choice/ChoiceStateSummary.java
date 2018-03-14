package com.layer.xdk.ui.message.choice;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.response.ResponseSummary;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This encapsulates the selected choices for a choice set (whether it be via a button or choice
 * message). The response summary for the message is handled here, as it will merge the summary
 * received from the server with local summary stored in the database. This allows for local choice
 * state persistence which is necessary for offline support.
 */
public class ChoiceStateSummary {

    private final Message mMessage;
    private final Gson mGson;
    private final Map<String, Set<String>> mSelectedChoices;
    private ResponseSummary mResponseSummary;
    private final Map<String, ChoiceConfigMetadata> mConfigMetadataMap;

    /**
     * Call this constructor once the metadata has been determined for the choice sets. This set is
     * immutable.
     *
     * @param message The message associated with the handling model
     * @param gson A Gson object that handles serializing/de-serializing the local state to the
     *             database
     * @param metadata The set of choice config metadata to use. Normally this is every choice
     *                 metadata object associated with the message.
     */
    public ChoiceStateSummary(Message message, Gson gson, @NonNull Set<ChoiceConfigMetadata> metadata) {
        mMessage = message;
        mGson = gson;
        mSelectedChoices = new HashMap<>();
        mConfigMetadataMap = new HashMap<>(metadata.size());
        for (ChoiceConfigMetadata current : metadata) {
            mConfigMetadataMap.put(current.getResponseName(), current);
            mSelectedChoices.put(current.getResponseName(), null);
        }

        byte[] cachedSummary = message.getLocalData();
        if (cachedSummary != null) {
            String json = new String(cachedSummary, Charset.forName("UTF-8"));
            mResponseSummary = mGson.fromJson(json, ResponseSummary.class);
        }
        processSelections();
    }

    /**
     * Returns the currently selected choices for a given response name. This response name is
     * defined in the appropriate {@link ChoiceConfigMetadata}.
     *
     * @param responseName response name for the selected choices
     * @return set of selected choices or an empty set if none are selected
     */
    @NonNull
    public Set<String> getSelectedChoices(String responseName) {
        Set<String> selection = mSelectedChoices.get(responseName);
        if (selection == null) {
            selection = new HashSet<>(1);
            mSelectedChoices.put(responseName, selection);
        }
        return selection;
    }

    /**
     * Handle processing of a {@link ResponseSummary} object received in a
     * {@link com.layer.sdk.messaging.MessagePart}. This should normally be called in
     * {@link MessageModel#getResponseSummaryPart()}.
     *
     * @param responseSummary response summary object from the appropriate message part
     */
    public void processRemoteResponseSummary(ResponseSummary responseSummary) {
        // TODO merge local and remote response summary with AND-1340
        mResponseSummary = responseSummary;
        processSelections();
    }

    /**
     * Update the selected choices and update the locally cached response summary. Call this
     * whenever a user makes a change to the selection set.
     *
     * @param responseName response name this change relates to
     * @param selectedChoices the current selected choices for this response name
     * @param userId the ID for the user that made this change, normally the authenticated user
     */
    public void handleUserSelectionChange(String responseName, @NonNull Set<String> selectedChoices, String userId) {
        Set<String> currentSelectedChoices = getSelectedChoices(responseName);
        currentSelectedChoices.clear();
        currentSelectedChoices.addAll(selectedChoices);
        cacheLocalResponseSummary(responseName, userId, currentSelectedChoices);
    }

    private void cacheLocalResponseSummary(String responseName, String userId,
            Set<String> selectedChoices) {
        if (mResponseSummary == null) {
            mResponseSummary = new ResponseSummary();
        }
        if (mResponseSummary.mParticipantData == null) {
            mResponseSummary.mParticipantData = new HashMap<>(1);
        }
        JsonObject jsonObject = mResponseSummary.mParticipantData.get(userId);
        if (jsonObject == null) {
            jsonObject = new JsonObject();
            mResponseSummary.mParticipantData.put(userId, jsonObject);
        }
        jsonObject.remove(responseName);
        jsonObject.addProperty(responseName, TextUtils.join(",", selectedChoices));
        byte[] newResponseSummary = mGson.toJson(mResponseSummary).getBytes(Charset.forName("UTF-8"));
        mMessage.putLocalData(newResponseSummary);
    }

    private void processSelections() {
        if (mResponseSummary != null && mResponseSummary.hasData()) {
            for (Map.Entry<String, JsonObject> participantResponses : mResponseSummary.mParticipantData.entrySet()) {
                for (String responseName : mSelectedChoices.keySet()) {
                    if (participantResponses.getValue().has(responseName)) {
                        Set<String> selectedChoices = getSelectedChoices(responseName);
                        selectedChoices.clear();
                        String[] ids = participantResponses.getValue().get(
                                responseName).getAsString().split(",");
                        selectedChoices.addAll(Arrays.asList(ids));
                        // If nothing is selected then we can get an empty string. Remove this
                        selectedChoices.remove("");
                    }
                }
            }
        } else {
            for (Map.Entry<String, ChoiceConfigMetadata> entry : mConfigMetadataMap.entrySet()) {
                String preselectedChoice = entry.getValue().mPreselectedChoice;
                if (preselectedChoice != null) {
                    Set<String> selectedChoices = getSelectedChoices(entry.getKey());
                    selectedChoices.add(preselectedChoice);
                }
            }
        }
    }
}
