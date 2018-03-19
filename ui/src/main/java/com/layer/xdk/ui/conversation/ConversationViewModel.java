package com.layer.xdk.ui.conversation;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.xdk.ui.message.MessageItemsListViewModel;

import javax.inject.Inject;

public class ConversationViewModel extends BaseObservable {
    private Conversation mConversation;
    private MessageItemsListViewModel mMessageItemsListViewModel;
    private LayerClient mLayerClient;

    @Inject
    public ConversationViewModel(LayerClient layerClient,
            MessageItemsListViewModel messageItemsListViewModel) {
        mMessageItemsListViewModel = messageItemsListViewModel;
        mLayerClient = layerClient;
    }

    public void setConversation(Conversation conversation) {
        mConversation = conversation;
        mMessageItemsListViewModel.setConversation(conversation);
        notifyChange();
    }

    @Bindable
    public LayerClient getLayerClient() {
        return mLayerClient;
    }

    @Bindable
    public Conversation getConversation() {
        return mConversation;
    }

    public MessageItemsListViewModel getMessageItemsListViewModel() {
        return mMessageItemsListViewModel;
    }
}
