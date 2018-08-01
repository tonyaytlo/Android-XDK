package com.layer.xdk.ui.message.feedback;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.response.ChoiceResponseMetadata;
import com.layer.xdk.ui.message.response.crdt.OrOperationResult;
import com.layer.xdk.ui.repository.MessageSenderRepository;

import java.util.List;
import java.util.UUID;

/**
 * Convenience class to send a response message for a feedback selection.
 */
public class SendFeedbackDelegate {

    private final Context mContext;
    private final Uri mRootMessagePartId;
    private final MessageSenderRepository mMessageSenderRepository;
    private final Message mMessage;

    /**
     * @param context application context used for resource lookups
     * @param rootMessagePartId ID for the part this response is in response to
     * @param messageSenderRepository repository for sending the message
     * @param message message this feedback is a part of
     */
    @SuppressWarnings("WeakerAccess")
    public SendFeedbackDelegate(@NonNull Context context, @NonNull Uri rootMessagePartId,
            @NonNull MessageSenderRepository messageSenderRepository, @NonNull Message message) {
        mContext = context;
        mRootMessagePartId = rootMessagePartId;
        mMessageSenderRepository = messageSenderRepository;
        mMessage = message;
    }

    /**
     * Send a response message for the feedback.
     *
     * @param results list of OR Set results to send to the server
     */
    public void sendResponse(@NonNull List<OrOperationResult> results) {
        String statusText = mContext.getString(R.string.xdk_ui_feedback_message_status_text);

        UUID rootPartId = UUID.fromString(mRootMessagePartId.getLastPathSegment());

        ChoiceResponseMetadata responseMetadata = new ChoiceResponseMetadata(mMessage.getId(),
                rootPartId, statusText, results);

        mMessageSenderRepository.sendChoiceResponse(mMessage.getConversation(),
                responseMetadata);
    }
}
