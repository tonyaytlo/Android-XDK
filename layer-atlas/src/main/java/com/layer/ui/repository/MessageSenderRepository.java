package com.layer.ui.repository;


import android.content.Context;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.ui.message.response.ResponseSender;
import com.layer.ui.message.response.ChoiceResponseModel;

/**
 * Manages the sending of different message types using a {@link LayerClient}.
 */
@SuppressWarnings("UnusedReturnValue")
public class MessageSenderRepository {

    private LayerClient mLayerClient;
    private Context mContext;

    public MessageSenderRepository(Context context, LayerClient layerClient) {
        mContext = context;
        mLayerClient = layerClient;
    }

    /**
     * Send a choice response message on a given conversation.
     *
     * @param conversation conversation to send the message on
     * @param choiceResponse model to populate the response message
     * @return result of {@link com.layer.ui.message.messagetypes.MessageSender#send(Message)}
     */
    public boolean sendChoiceResponse(Conversation conversation, ChoiceResponseModel choiceResponse) {
        ResponseSender responseSender = new ResponseSender(mContext, mLayerClient);
        responseSender.setConversation(conversation);
        return responseSender.sendChoiceResponse(choiceResponse);
    }
}
