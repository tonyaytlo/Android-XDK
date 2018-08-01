package com.layer.xdk.ui.message.choice;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.message.response.crdt.FirstWriterWinsRegister;
import com.layer.xdk.ui.message.response.crdt.LastWriterWinsNullableRegister;
import com.layer.xdk.ui.message.response.crdt.LastWriterWinsRegister;
import com.layer.xdk.ui.message.response.crdt.ORSet;
import com.layer.xdk.ui.message.response.crdt.ORSetHelper;
import com.layer.xdk.ui.message.response.crdt.OrOperation;
import com.layer.xdk.ui.message.response.crdt.StandardORSet;
import com.layer.xdk.ui.util.Log;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A helper class to use when making with choice selections. This creates the appropriate register
 * based on the {@link ChoiceConfigMetadata}.
 */
public class ChoiceOrSetHelper extends ORSetHelper {

    private final Map<String, ChoiceConfigMetadata> mConfigMetadataMap;

    /**
     * @param gson Gson object to use for serialization/de-serialization to the local data field of
     *             the Message
     * @param rootPart the root message part these choices belong to
     * @param metadata set of choice configurations to manage for this message
     */
    public ChoiceOrSetHelper(@NonNull Gson gson, @NonNull MessagePart rootPart,
            @NonNull Set<ChoiceConfigMetadata> metadata) {
        super(gson, rootPart);

        mConfigMetadataMap = new HashMap<>(metadata.size());
        for (ChoiceConfigMetadata current : metadata) {
            mConfigMetadataMap.put(current.getResponseName(), current);
        }
    }

    /**
     * Get the selections for the user that this choice config is enabled for.
     *
     * @param responseName response name of the choice configuration to use
     * @return the selected values or an empty set if none are selected
     */
    @NonNull
    public Set<String> getSelections(String responseName) {
        ChoiceConfigMetadata config = mConfigMetadataMap.get(responseName);
        return getSelections(config.mEnabledFor, responseName);
    }

    protected ORSet createORSet(String responseName, @Nullable LinkedHashSet<OrOperation> adds,
            @Nullable LinkedHashSet<String> removes) {
        ChoiceConfigMetadata choiceConfigMetadata = mConfigMetadataMap.get(responseName);
        if (choiceConfigMetadata == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Choice with response name '" + responseName + "' has no configuration");
            }
            throw new IllegalArgumentException("Choice with response name '" + responseName
                    + "' has no configuration");
        }
        if (choiceConfigMetadata.mAllowMultiselect) {
            return new StandardORSet(responseName, adds, removes);
        } else if (choiceConfigMetadata.mAllowDeselect) {
            return new LastWriterWinsNullableRegister(responseName, adds, removes);
        } else if (choiceConfigMetadata.mAllowReselect) {
            return new LastWriterWinsRegister(responseName, adds, removes);
        } else {
            return new FirstWriterWinsRegister(responseName, adds, removes);
        }
    }
}
