package com.layer.xdk.ui.message;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.TypingIndicatorLayout;
import com.layer.xdk.ui.message.adapter2.MessagesAdapter2;
import com.layer.xdk.ui.message.adapter2.decoration.GroupStartItemDecoration;
import com.layer.xdk.ui.message.adapter2.decoration.SubGroupInnerItemDecoration;
import com.layer.xdk.ui.message.adapter2.decoration.SubGroupStartItemDecoration;

import java.util.Set;

public class MessageItemsListView extends SwipeRefreshLayout implements LayerChangeEventListener.BackgroundThread.Weak {

    protected RecyclerView mMessagesRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected MessagesAdapter2 mAdapter;

    protected LayerClient mLayerClient;
    protected Conversation mConversation;
    private int mNumberOfItemsPerSync = 20;
    private View mHeaderView;

    public MessageItemsListView(Context context) {
        this(context, null);
    }

    public MessageItemsListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(getContext(), R.layout.xdk_ui_message_items_list, this);
        mMessagesRecyclerView = findViewById(R.id.xdk_ui_message_recycler);
        mHeaderView = new EmptyMessageListHeaderView(getContext());

        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mLinearLayoutManager.setReverseLayout(true);
        mMessagesRecyclerView.setHasFixedSize(true);
        mMessagesRecyclerView.setLayoutManager(mLinearLayoutManager);

        mMessagesRecyclerView.addItemDecoration(new GroupStartItemDecoration(context));
        mMessagesRecyclerView.addItemDecoration(new SubGroupStartItemDecoration(context));
        mMessagesRecyclerView.addItemDecoration(new SubGroupInnerItemDecoration(context));

        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLayerClient.registerEventListener(MessageItemsListView.this);
                if (mConversation.getHistoricSyncStatus() == Conversation.HistoricSyncStatus.MORE_AVAILABLE) {
                    mConversation.syncMoreHistoricMessages(mNumberOfItemsPerSync);
                }
            }
        });
    }

    private void removeEmptyHeaderView() {
        //Check the default data source state of the Adapter, if HeaderView, FooterView are set on the Adapter
//        int count = (mAdapter.getHeaderView() == null ? 0 : 1) + (mAdapter.getFooterView() == null ? 0 : 1);
//        if (mAdapter.getHeaderView() == mHeaderView && count != mAdapter.getItemCount()) {
//            mAdapter.setHeaderView(null);
//            mAdapter.setShouldShowHeader(false);
//        }
    }

    public void removeHeaderView() {
//        mAdapter.setHeaderView(null);
    }

    public void setHeaderView(View headerView) {
//        mAdapter.setHeaderView(headerView);
    }

    public void setAdapter(final MessagesAdapter2 adapter) {
        mAdapter = adapter;
        mMessagesRecyclerView.setAdapter(adapter);

        mAdapter.registerAdapterDataObserver(new MessagesAdapter2.NewMessageReceivedObserver() {
            @Override
            public void onNewMessageReceived() {
                autoScroll();
            }
        });
    }

    //============================================================================================
    // LayerChangeEventListener.BackgroundThread.Weak Methods
    //============================================================================================

    @Override
    public void onChangeEvent(LayerChangeEvent layerChangeEvent) {
        for (LayerChange change : layerChangeEvent.getChanges()) {
            if (change.getObject() != mConversation) continue;
            if (change.getChangeType() != LayerChange.Type.UPDATE) continue;
            if (!change.getAttributeName().equals("historicSyncStatus")) continue;
            mLayerClient.unregisterEventListener(this);
            refresh();
        }
    }

    //============================================================================================
    // SwipeRefreshLayout Methods
    //============================================================================================

    /**
     * Automatically refresh on resume.
     */
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) return;
        refresh();
    }

    /**
     * Refreshes the state of the underlying recycler view
     */
    private void refresh() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mConversation == null) {
                    setEnabled(false);
                    setRefreshing(false);
                    return;
                }
                Conversation.HistoricSyncStatus status = mConversation.getHistoricSyncStatus();
                setEnabled(status == Conversation.HistoricSyncStatus.MORE_AVAILABLE);
                setRefreshing(status == Conversation.HistoricSyncStatus.SYNC_PENDING);
            }
        });
    }

    //============================================================================================
    // Public Methods
    //============================================================================================


    public int getNumberOfItemsPerSync() {
        return mNumberOfItemsPerSync;
    }

    public void setNumberOfItemsPerSync(int numberOfItemsPerSync) {
        mNumberOfItemsPerSync = numberOfItemsPerSync;
    }

    /**
     * Convenience pass-through to this list's MessagesAdapter.
     *
     * @see MessagesAdapter2#getShouldShowAvatarInOneOnOneConversations()
     */
    public boolean getShouldShowAvatarInOneOnOneConversations() {
        return mAdapter.getShouldShowAvatarInOneOnOneConversations();
    }

    /**
     * Convenience pass-through to this list's MessagesAdapter.
     *
     * @see MessagesAdapter2#setShouldShowAvatarInOneOnOneConversations(boolean)
     */
    public void setShouldShowAvatarInOneOnOneConversations(boolean shouldShowAvatarInOneOnOneConversations) {
        mAdapter.setShouldShowAvatarInOneOnOneConversations(shouldShowAvatarInOneOnOneConversations);
    }

    /**
     * Scrolls if the user is at the end
     */
    private void autoScroll() {
        // Find first since this layout is reversed
        int firstVisiblePosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        if (firstVisiblePosition < 2) {
            mLinearLayoutManager.smoothScrollToPosition(mMessagesRecyclerView, null, 0);
        }
    }

    /**
     * Convenience pass-through to this list's MessagesAdapter.
     *
//     * @see MessagesAdapter2#setFooterView(View, Set)
     */
    public void setFooterView(TypingIndicatorLayout footerView, Set<Identity> users) {
//        mAdapter.setFooterView(footerView, users);
        autoScroll();
    }
}
