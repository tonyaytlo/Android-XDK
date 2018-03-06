package com.layer.xdk.ui.message.adapter2;


import android.arch.paging.DataSource;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.model.MessageModel;

/**
 * Factory that handles creations of {@link MessagesDataSource}. This contains the variables used
 * to create new instances of the DataSource when old ones become invalid.
 */
public class MessagesDataSourceFactory implements DataSource.Factory<Integer, MessageModel> {

    private final LayerClient mLayerClient;
    private final Conversation mConversation;
    private final Predicate mPredicate;
    private final BinderRegistry mBinderRegistry;
    private final GroupingCalculator mGroupingCalculator;

    /**
     * Creates a Factory
     *
     * @param layerClient client to use for the query
     * @param binderRegistry registry that handles model creation
     * @param conversation conversation to fetch the messages for
     * @param predicate custom predicate to use for the query or null if default should be used
     */
    public MessagesDataSourceFactory(@NonNull LayerClient layerClient,
            @NonNull BinderRegistry binderRegistry,
            @NonNull Conversation conversation,
            @Nullable Predicate predicate) {
        mLayerClient = layerClient;
        mConversation = conversation;
        mPredicate = predicate;
        mBinderRegistry = binderRegistry;
        mGroupingCalculator = new GroupingCalculator();
    }

    @Override
    public DataSource<Integer, MessageModel> create() {
        return new MessagesDataSource(mLayerClient, mConversation, mPredicate, mBinderRegistry, mGroupingCalculator);
    }
}
