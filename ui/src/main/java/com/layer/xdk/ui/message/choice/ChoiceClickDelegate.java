package com.layer.xdk.ui.message.choice;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.response.ChoiceResponseModel;
import com.layer.xdk.ui.repository.MessageSenderRepository;

import java.util.Set;
import java.util.UUID;

/**
 * Convenience class to send a response message for a choice selection. This encapsulates the shared
 * functionality between a Choice message model and a Button message model with choices.
 */
public class ChoiceClickDelegate {

    private final String mUserName;
    private final Context mContext;
    private final Uri mRootMessagePartId;
    private final MessageSenderRepository mMessageSenderRepository;
    private final Message mMessage;

    /**
     * @param formattedUserName name to use in the response message. This is usually formatted with
     *                          an {@link com.layer.xdk.ui.identity.IdentityFormatter} and is
     *                          usually the authenticated user.
     * @param context application context used for resource lookups
     * @param rootMessagePartId ID for the part this response is in response to
     * @param messageSenderRepository repository for sending the message
     * @param message message this choice is for
     */
    public ChoiceClickDelegate(String formattedUserName, Context context,
            Uri rootMessagePartId,
            MessageSenderRepository messageSenderRepository, Message message) {
        mUserName = formattedUserName;
        mContext = context;
        mRootMessagePartId = rootMessagePartId;
        mMessageSenderRepository = messageSenderRepository;
        mMessage = message;
    }

    /**
     * Send a response message for the selected choice.
     *
     * @param choiceConfig configuration for the choice
     * @param choice choice that was selected
     * @param selected true if the choice is being selected, false if it is being deselected
     * @param selectedChoices set of selected choices in this choice config
     */
    public void sendResponse(ChoiceConfigMetadata choiceConfig, @NonNull ChoiceMetadata choice,
            boolean selected, @NonNull Set<String> selectedChoices) {
        String statusText;
        if (TextUtils.isEmpty(choiceConfig.mName)) {
            statusText = mContext.getString(
                    selected ? R.string.xdk_ui_response_message_status_text_selected
                            : R.string.xdk_ui_response_message_status_text_deselected,
                    mUserName,
                    choice.mText);
        } else {
            statusText = mContext.getString(
                    selected ? R.string.xdk_ui_response_message_status_text_with_name_selected
                            : R.string.xdk_ui_response_message_status_text_with_name_deselected,
                    mUserName,
                    choice.mText,
                    choiceConfig.mName);
        }

        UUID rootPartId = UUID.fromString(mRootMessagePartId.getLastPathSegment());

        ChoiceResponseModel choiceResponseModel = new ChoiceResponseModel(mMessage.getId(),
                rootPartId, statusText);
        choiceResponseModel.addChoices(choiceConfig.getResponseName(), selectedChoices);

        MessageSenderRepository messageSenderRepository = mMessageSenderRepository;
        messageSenderRepository.sendChoiceResponse(mMessage.getConversation(),
                choiceResponseModel);
    }
}
