package com.layer.xdk.ui.message;

import android.content.Context;
import android.databinding.Bindable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.viewmodel.ItemViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageItemHeaderViewModel extends ItemViewModel<Message> {
    protected Conversation mConversation;
    private Context mContext;
    private LayerClient mLayerclient;

    public MessageItemHeaderViewModel(Context context, LayerClient layerClient) {
        super(context, layerClient);
        mContext = context;
        mLayerclient = layerClient;
    }

    public void setConversation(Conversation conversation) {
        mConversation = conversation;
    }

    @Bindable
    public String getHeaderText() {
        return mConversation != null ? provideHeaderTextMessage(mConversation.getParticipants(),
                mLayerclient.getAuthenticatedUser()) : "";
    }

    protected String provideHeaderTextMessage(Set<Identity> participants, Identity authenticatedUser) {

        if (participants.size() == 0 || authenticatedUser == null) return "";
        participants.remove(authenticatedUser);
        List<Identity> participantList = new ArrayList<>(participants);

        String headerText;

        switch (participants.size()) {
            case 0:
                headerText = mContext.getResources().getString(
                        R.string.xdk_ui_empty_conversation_with_zero_participant);
                break;
            case 1:
                headerText = mContext.getResources().getString(
                        R.string.xdk_ui_empty_conversation_with_one_participant,
                        participantList.get(0).getDisplayName());
                break;
            case 2:
                headerText = mContext.getResources().getString(
                        R.string.xdk_ui_empty_conversation_with_two_participants,
                        participantList.get(0).getDisplayName(),
                        participantList.get(1).getDisplayName());
                break;
            case 3:
                headerText = mContext.getResources().getString(
                        R.string.xdk_ui_empty_conversation_with_three_participants,
                        participantList.get(0).getDisplayName(),
                        participantList.get(1).getDisplayName());
                break;
            default:
                int remainder = participantList.size() - 2;
                headerText = mContext.getResources().getString(
                        R.string.xdk_ui_empty_conversation_with_many_participants,
                        participantList.get(0).getDisplayName(),
                        participantList.get(1).getDisplayName(), remainder);
        }

        return headerText;
    }

    @Bindable
    public Conversation getConversation() {
        return mConversation;
    }
}
