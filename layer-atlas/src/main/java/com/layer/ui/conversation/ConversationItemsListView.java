package com.layer.ui.conversation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.ui.R;
import com.layer.ui.adapters.ConversationItemsAdapter;
import com.layer.ui.recyclerview.ItemsRecyclerView;
import com.layer.ui.style.ConversationItemStyle;
import com.layer.ui.util.views.SwipeableItem;

import java.util.List;

public class ConversationItemsListView extends ConstraintLayout {

    protected ConversationItemStyle mConversationItemStyle;
    protected ItemsRecyclerView<Conversation> mConversationItemsRecycler;
    protected LayerClient mLayerClient;

    public ConversationItemsListView(Context context) {
        super(context);
        init();
    }

    public ConversationItemsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mConversationItemStyle = new ConversationItemStyle(context, attrs, 0);
    }

    @SuppressWarnings("unchecked")
    protected void init() {
        inflate(getContext(), R.layout.ui_conversation_items_list, this);
        mConversationItemsRecycler = (ItemsRecyclerView<Conversation>) findViewById(R.id.ui_conversation_items_list_recycler);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        manager.setStackFromEnd(false);
        mConversationItemsRecycler.setLayoutManager(manager);

        DefaultItemAnimator noChangeAnimator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull List<Object> payloads) {
                return true;
            }
        };
        noChangeAnimator.setSupportsChangeAnimations(false);
        mConversationItemsRecycler.setItemAnimator(noChangeAnimator);
    }

    public void setConversationItemsAdapter(ConversationItemsAdapter adapter) {
        adapter.setStyle(mConversationItemStyle);
        mConversationItemsRecycler.setAdapter(adapter);
        adapter.refresh();
    }

    public void setItemSwipeListener(SwipeableItem.OnItemSwipeListener<Conversation> itemSwipeListener) {
        mConversationItemsRecycler.setItemSwipeListener(itemSwipeListener);
    }

    public void onDestroy() {
        mConversationItemsRecycler.onDestroy();
    }
}
