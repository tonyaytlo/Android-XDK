package com.layer.xdk.ui.conversation;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.MessageItemsListViewModel;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

public class ConversationViewModel extends BaseObservable {
    private Conversation mConversation;
    private MessageItemsListViewModel mMessageItemsListViewModel;
    private LayerClient mLayerClient;


    public ConversationViewModel(Context context,
            LayerClient layerClient,
            ImageCacheWrapper imageCacheWrapper,
            DateFormatter dateFormatter,
            IdentityFormatter identityFormatter) {
        mMessageItemsListViewModel = new MessageItemsListViewModel(context, layerClient,
                imageCacheWrapper, dateFormatter, identityFormatter);
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
