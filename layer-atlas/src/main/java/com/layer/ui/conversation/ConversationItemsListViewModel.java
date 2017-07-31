package com.layer.ui.conversation;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.SortDescriptor;
import com.layer.ui.adapters.ConversationItemsAdapter;
import com.layer.ui.avatar.IdentityNameFormatter;
import com.layer.ui.conversationitem.ConversationItemFormatter;
import com.layer.ui.recyclerview.OnItemClickListener;
import com.layer.ui.util.imagecache.ImageCacheWrapper;
import com.layer.ui.util.views.SwipeableItem;

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

    protected ConversationItemsAdapter mConversationItemsAdapter;
    protected SwipeableItem.OnItemSwipeListener<Conversation> mItemSwipeListener;

    /**
     * Only show conversations we're still a member of, sorted by the last Message's receivedAt time
     */
    public ConversationItemsListViewModel(Context context, LayerClient layerClient,
                                          ConversationItemFormatter conversationItemFormatter,
                                          ImageCacheWrapper imageCacheWrapper,
                                          IdentityNameFormatter identityNameFormatter) {
        this(context, layerClient, MY_CURRENT_CONVERSATIONS_BY_TIME, null,
                INITIAL_HISTORIC_MESSAGES_TO_SYNC, conversationItemFormatter, imageCacheWrapper, identityNameFormatter);
    }

    /**
     * @param query custom query for Conversation objects
     */
    public ConversationItemsListViewModel(Context context, LayerClient layerClient,
                                          Query<Conversation> query,
                                          Collection<String> updateAttributes,
                                          int initialHistoricMessagesToFetch,
                                          ConversationItemFormatter conversationItemFormatter,
                                          ImageCacheWrapper imageCacheWrapper,
                                          IdentityNameFormatter identityNameFormatter) {
        mConversationItemsAdapter = new ConversationItemsAdapter(context, layerClient, query,
                updateAttributes, conversationItemFormatter, imageCacheWrapper, identityNameFormatter);
        mConversationItemsAdapter.setInitialHistoricMessagesToFetch(initialHistoricMessagesToFetch);
    }

    @Bindable
    public ConversationItemsAdapter getConversationItemsAdapter() {
        return mConversationItemsAdapter;
    }

    public void setItemClickListener(OnItemClickListener<Conversation> itemClickListener) {
        mConversationItemsAdapter.setItemClickListener(itemClickListener);
    }

    public void setItemSwipeListener(SwipeableItem.OnItemSwipeListener<Conversation> itemSwipeListener) {
        mItemSwipeListener = itemSwipeListener;
    }

    public SwipeableItem.OnItemSwipeListener<Conversation> getItemSwipeListener() {
        return mItemSwipeListener;
    }
}
