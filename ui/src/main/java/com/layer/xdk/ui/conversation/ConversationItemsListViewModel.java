package com.layer.xdk.ui.conversation;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.SortDescriptor;
import com.layer.xdk.ui.adapters.ConversationItemsAdapter;
import com.layer.xdk.ui.conversationitem.ConversationItemFormatter;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.recyclerview.OnItemLongClickListener;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

import java.util.Collection;

public class ConversationItemsListViewModel extends BaseObservable {

    private final static int INITIAL_HISTORIC_MESSAGES_TO_SYNC = 20;
    private final static Query<Conversation> MY_CURRENT_CONVERSATIONS_BY_TIME;

    static {
        MY_CURRENT_CONVERSATIONS_BY_TIME = Query.builder(Conversation.class)
                .predicate(new Predicate(Conversation.Property.PARTICIPANT_COUNT, Predicate.Operator.GREATER_THAN, 1))
                .sortDescriptor(new SortDescriptor(Conversation.Property.LAST_MESSAGE_RECEIVED_AT, SortDescriptor.Order.DESCENDING))
                .build();
    }

    private ConversationItemsAdapter mConversationItemsAdapter;
    /**
     * Only show conversations we're still a member of, sorted by the last Message's receivedAt time
     */
    public ConversationItemsListViewModel(Context context, LayerClient layerClient,
                                          ConversationItemFormatter conversationItemFormatter,
                                          ImageCacheWrapper imageCacheWrapper) {
        this(context, layerClient, MY_CURRENT_CONVERSATIONS_BY_TIME, null,
                INITIAL_HISTORIC_MESSAGES_TO_SYNC, conversationItemFormatter, imageCacheWrapper);
    }

    /**
     * @param query custom query for Conversation objects
     */
    public ConversationItemsListViewModel(Context context, LayerClient layerClient,
                                          Query<Conversation> query,
                                          Collection<String> updateAttributes,
                                          int initialHistoricMessagesToFetch,
                                          ConversationItemFormatter conversationItemFormatter,
                                          ImageCacheWrapper imageCacheWrapper) {
        mConversationItemsAdapter = new ConversationItemsAdapter(context, layerClient, query,
                updateAttributes, conversationItemFormatter, imageCacheWrapper);
        mConversationItemsAdapter.setInitialHistoricMessagesToFetch(initialHistoricMessagesToFetch);
    }

    @Bindable
    public ConversationItemsAdapter getConversationItemsAdapter() {
        return mConversationItemsAdapter;
    }

    public void setItemClickListener(OnItemClickListener<Conversation> listener) {
        mConversationItemsAdapter.setItemClickListener(listener);
    }

    public void setItemLongClickListener(OnItemLongClickListener<Conversation> listener) {
        mConversationItemsAdapter.setItemLongClickListener(listener);
    }

    public void setIdentityFormatter(IdentityFormatter identityFormatter) {
        mConversationItemsAdapter.setIdentityFormatter(identityFormatter);
    }

    public void setDateFormatter(DateFormatter dateFormatter) {
        mConversationItemsAdapter.setDateFormatter(dateFormatter);
    }
}
