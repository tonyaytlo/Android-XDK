package com.layer.xdk.ui.conversation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.Nullable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Query;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.MessageItemsListViewModel;
import com.layer.xdk.ui.message.adapter2.MessagesDataSourceFactory;
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.model.AbstractMessageModel;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

public class ConversationViewModel extends BaseObservable {
    private Conversation mConversation;
    private MessageItemsListViewModel mMessageItemsListViewModel;
    private LayerClient mLayerClient;
    private Query<Message> mQuery;
    private BinderRegistry mBinderRegistry;


    public ConversationViewModel(Context context,
            LayerClient layerClient,
            ImageCacheWrapper imageCacheWrapper,
            DateFormatter dateFormatter,
            IdentityFormatter identityFormatter) {
        mBinderRegistry = new BinderRegistry(context, layerClient);
        mMessageItemsListViewModel = new MessageItemsListViewModel(context, layerClient,
                imageCacheWrapper, dateFormatter, identityFormatter, mBinderRegistry);
        mLayerClient = layerClient;

    }

    public void setConversation(Conversation conversation) {
        mConversation = conversation;
        final LiveData<PagedList<AbstractMessageModel>> messageList = new LivePagedListBuilder<>(
                new MessagesDataSourceFactory(getLayerClient(), mBinderRegistry, conversation),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setPageSize(10)
                        .build()
        ).build();

        messageList.observeForever(new Observer<PagedList<AbstractMessageModel>>() {
            @Override
            public void onChanged(@Nullable PagedList<AbstractMessageModel> messages) {
                mMessageItemsListViewModel.getAdapter().submitList(messages);
            }
        });
        notifyChange();
    }

    public void setQuery(Query<Message> query) {
        mQuery = query;
        notifyChange();
    }

    public Query<Message> getQuery() {
        return mQuery;
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

    public void setOnItemClickListner(OnItemClickListener<Message> itemClickListner) {
        mMessageItemsListViewModel.setItemClickListener(itemClickListner);
    }
}
