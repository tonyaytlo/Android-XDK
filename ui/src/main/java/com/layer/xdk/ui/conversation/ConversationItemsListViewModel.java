package com.layer.xdk.ui.conversation;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.SortDescriptor;
import com.layer.xdk.ui.adapters.ConversationItemsAdapter;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.recyclerview.OnItemLongClickListener;

import java.util.Collection;

import javax.inject.Inject;

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

    @Inject
    public ConversationItemsListViewModel(ConversationItemsAdapter adapter) {
        mConversationItemsAdapter = adapter;
        setInitialHistoricMessagesToFetch(INITIAL_HISTORIC_MESSAGES_TO_SYNC);
    }

    public void setInitialHistoricMessagesToFetch(int numberOfMessagesToFetch) {
        mConversationItemsAdapter.setInitialHistoricMessagesToFetch(numberOfMessagesToFetch);
    }

    /**
     * Only show conversations we're still a member of, sorted by the last Message's receivedAt time
     */
    public void useDefaultQuery() {
        setQuery(MY_CURRENT_CONVERSATIONS_BY_TIME, null);
    }

    /**
     * @param query custom query for Conversation objects
     */
    public void setQuery(Query<Conversation> query, Collection<String> updateAttributes) {
        mConversationItemsAdapter.setQuery(query, updateAttributes);
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
}
