package com.layer.xdk.ui.message.adapter2;


import android.arch.paging.DataSource;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.model.MessageModel;

public class MessagesDataSourceFactory implements DataSource.Factory<Integer, MessageModel> {

    private final LayerClient mLayerClient;
    private final Conversation mConversation;
    private final BinderRegistry mBinderRegistry;

    public MessagesDataSourceFactory(LayerClient layerClient, BinderRegistry binderRegistry,
            Conversation conversation) {
        mLayerClient = layerClient;
        mConversation = conversation;
        mBinderRegistry = binderRegistry;
    }

    @Override
    public DataSource<Integer, MessageModel> create() {
        return new MessagesDataSource(mLayerClient, mConversation, mBinderRegistry);
    }
}
