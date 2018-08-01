package com.layer.xdk.ui.message.feedback;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.message.response.crdt.FirstWriterWinsRegister;
import com.layer.xdk.ui.message.response.crdt.ORSet;
import com.layer.xdk.ui.message.response.crdt.ORSetHelper;
import com.layer.xdk.ui.message.response.crdt.OrOperation;

import java.util.LinkedHashSet;

/**
 * A helper class to use when making with feedback selections. This always uses a
 * {@link FirstWriterWinsRegister} for selections.
 */
public class FeedbackOrSetHelper extends ORSetHelper {

    /**
     * @param gson Gson object to use for serialization/de-serialization to the local data field of
     *             the Message
     * @param rootPart the root message part these choices belong to
     */
    @SuppressWarnings("WeakerAccess")
    public FeedbackOrSetHelper(@NonNull Gson gson, @NonNull MessagePart rootPart) {
        super(gson, rootPart);
    }

    @Override
    protected ORSet createORSet(String stateName, LinkedHashSet<OrOperation> adds,
            LinkedHashSet<String> removes) {
        return new FirstWriterWinsRegister(stateName, adds, removes);
    }
}
