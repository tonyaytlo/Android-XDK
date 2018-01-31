package com.layer.xdk.ui.conversation;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Query;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.MessageItemsListViewModel;
import com.layer.xdk.ui.message.messagetypes.CellFactory;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

import java.util.List;

public class ConversationViewModel extends BaseObservable {
    private Conversation mConversation;
    private MessageItemsListViewModel mMessageItemsListViewModel;
    private LayerClient mLayerClient;
    private Query<Message> mQuery;

    public ConversationViewModel(Context context, LayerClient layerClient, List<CellFactory> cellFactories,
                                 ImageCacheWrapper imageCacheWrapper, DateFormatter dateFormatter,
                                 IdentityFormatter identityFormatter) {
        mMessageItemsListViewModel = new MessageItemsListViewModel(context, layerClient,
                imageCacheWrapper, dateFormatter, identityFormatter);
        mMessageItemsListViewModel.setCellFactories(cellFactories);
        mLayerClient = layerClient;
    }

    public void setConversation(Conversation conversation) {
        mConversation = conversation;
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
