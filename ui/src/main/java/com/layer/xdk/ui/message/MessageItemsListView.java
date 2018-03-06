package com.layer.xdk.ui.message;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.SortDescriptor;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.TypingIndicatorLayout;
import com.layer.xdk.ui.message.action.ActionHandlerRegistry;
import com.layer.xdk.ui.message.action.GoogleMapsOpenMapActionHandler;
import com.layer.xdk.ui.message.action.OpenFileActionHandler;
import com.layer.xdk.ui.message.action.OpenUrlActionHandler;
import com.layer.xdk.ui.message.adapter2.MessagesAdapter2;
import com.layer.xdk.ui.message.adapter2.decoration.GroupStartItemDecoration;
import com.layer.xdk.ui.message.adapter2.decoration.SubGroupInnerItemDecoration;
import com.layer.xdk.ui.message.adapter2.decoration.SubGroupStartItemDecoration;
import com.layer.xdk.ui.message.messagetypes.MessageStyle;
import com.layer.xdk.ui.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageItemsListView extends SwipeRefreshLayout implements LayerChangeEventListener.BackgroundThread.Weak {

    protected boolean mShouldShowAvatarsInOneOnOneConversations;
    protected MessageStyle mMessageStyle;
    protected RecyclerView mMessagesRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected MessagesAdapter2 mAdapter;

    protected LayerClient mLayerClient;
    protected Conversation mConversation;
    private int mNumberOfItemsPerSync = 20;
    private TextView mEmptyListTextView;

    public MessageItemsListView(Context context) {
        this(context, null);
    }

    public MessageItemsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseStyle(context, attrs, 0);

        inflate(getContext(), R.layout.xdk_ui_message_items_list, this);
        mMessagesRecyclerView = findViewById(R.id.xdk_ui_message_recycler);
        mEmptyListTextView = findViewById(R.id.xdk_ui_messages_recycler_empty_text);

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
                if (mConversation.getHistoricSyncStatus() == Conversation.HistoricSyncStatus.MORE_AVAILABLE) {
                    mConversation.syncMoreHistoricMessages(mNumberOfItemsPerSync);
                }
            }
        });
    }

    public void onDestroy() {
        mLayerClient.unregisterEventListener(this);
    }

    public void setAdapter(final MessagesAdapter2 adapter) {
        mAdapter = adapter;
//        mAdapter.setStyle(mMessageStyle);
        mMessagesRecyclerView.setAdapter(adapter);
        setShouldShowAvatarInOneOnOneConversations(mShouldShowAvatarsInOneOnOneConversations);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("ZZZZZ onChanged");
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                Log.d("ZZZZZ onItemRangeChanged. positionStart: " + positionStart + " itemCount: " + itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                Log.d("ZZZZZ onItemRangeChangedWithPayload. positionStart: " + positionStart + " itemCount: " + itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
//                mMessagesRecyclerView.scrollToPosition(0);
                Log.d("ZZZZZ onItemRangeInserted. positionStart: " + positionStart + " itemCount: " + itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                Log.d("ZZZZZ onItemRangeRemoved. positionStart: " + positionStart + " itemCount: " + itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                Log.d("ZZZZZ onItemRangeMoved. fromPosition: " + fromPosition + " toPosition:" + toPosition + " itemCount: " + itemCount);
            }
        });

        mAdapter.registerAdapterDataObserver(new MessagesAdapter2.NewMessageReceivedObserver() {
            @Override
            public void onNewMessageReceived() {
                if (mEmptyListTextView.getVisibility() == VISIBLE && mAdapter.getItemCount() > 0) {
                    mEmptyListTextView.setVisibility(GONE);
                }
                autoScroll();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (mAdapter.getItemCount() == 0) {
                    mEmptyListTextView.setVisibility(VISIBLE);
                }
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
     * @see MessagesAdapter#getShouldShowAvatarInOneOnOneConversations()
     */
    public boolean getShouldShowAvatarInOneOnOneConversations() {
//        return mAdapter.getShouldShowAvatarInOneOnOneConversations();
        return true;
    }

    /**
     * Convenience pass-through to this list's MessagesAdapter.
     *
     * @see MessagesAdapter#setShouldShowAvatarInOneOnOneConversations(boolean)
     */
    public void setShouldShowAvatarInOneOnOneConversations(boolean shouldShowAvatarInOneOnOneConversations) {
//        mAdapter.setShouldShowAvatarInOneOnOneConversations(shouldShowAvatarInOneOnOneConversations);
    }

    public void setTextTypeface(Typeface myTypeface, Typeface otherTypeface) {
        mMessageStyle.setMyTextTypeface(myTypeface);
        mMessageStyle.setOtherTextTypeface(otherTypeface);
    }

    /**
     * Scrolls if the user is at the end
     */
    private void autoScroll() {
        // Find first since this layout is reversed
        int lastVisiblePosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        if (lastVisiblePosition < 3) {
            mMessagesRecyclerView.scrollToPosition(0);
        }
    }

    /**
     * Convenience pass-through to this list's MessagesAdapter.
     *
     * @see MessagesAdapter#setFooterView(View, Set)
     */
    public void setFooterView(TypingIndicatorLayout footerView, Set<Identity> users) {
//        mAdapter.setFooterView(footerView, users);
        autoScroll();
    }

    /**
     * Updates the underlying MessagesAdapter with a Query for Messages in the given
     * Conversation.
     *
     * @param layerClient  LayerClient currently in use
     * @param conversation Conversation to display Messages for.
     */
    public void setConversation(LayerClient layerClient, Conversation conversation) {
        setConversation(layerClient, conversation, Query.builder(Message.class)
                .predicate(new Predicate(Message.Property.CONVERSATION, Predicate.Operator.EQUAL_TO, conversation))
                .sortDescriptor(new SortDescriptor(Message.Property.POSITION, SortDescriptor.Order.ASCENDING))
                .build());

        ActionHandlerRegistry.registerHandler(new OpenUrlActionHandler(layerClient));
        ActionHandlerRegistry.registerHandler(new GoogleMapsOpenMapActionHandler(layerClient));
        ActionHandlerRegistry.registerHandler(new OpenFileActionHandler(layerClient));
    }


    /**
     * Updates the underlying MessagesAdapter with the supplied Query for Messages in the given
     * Conversation.
     *
     * @param layerClient  LayerClient currently in use
     * @param conversation Conversation to display Messages for.
     * @param query        Query to be used with the specified conversation
     */
    public void setConversation(LayerClient layerClient, Conversation conversation, Query<Message> query) {
        mConversation = conversation;
        mLayerClient = layerClient;

        mEmptyListTextView.setText(getEmptyConversationHeaderText(getContext(), conversation.getParticipants(), layerClient.getAuthenticatedUser()));
        if (mAdapter.getItemCount() == 0) {
            mEmptyListTextView.setVisibility(VISIBLE);
        }

//        mAdapter.setQuery(query, null);
//        if (conversation != null) {
//            mAdapter.setReadReceiptsEnabled(conversation.isReadReceiptsEnabled());
//            mAdapter.setIsOneOnOneConversation(conversation.getParticipants().size() == 2);
//            mAdapter.setConversation(conversation);
//        }
//
//        mLayerClient.registerEventListener(this);
//        mAdapter.refresh();
    }

    protected void parseStyle(Context context, AttributeSet attrs, int defStyle) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MessageItemsListView, R.attr.MessageItemListView, defStyle);
        MessageStyle.Builder messageStyleBuilder = new MessageStyle.Builder();
        messageStyleBuilder.myTextColor(ta.getColor(R.styleable.MessageItemsListView_myTextColor, context.getResources().getColor(R.color.xdk_ui_text_black)));
        int myTextStyle = ta.getInt(R.styleable.MessageItemsListView_myTextStyle, Typeface.NORMAL);
        messageStyleBuilder.myTextStyle(myTextStyle);
        String myTextTypefaceName = ta.getString(R.styleable.MessageItemsListView_myTextTypeface);
        messageStyleBuilder.myTextTypeface(myTextTypefaceName != null ? Typeface.create(myTextTypefaceName, myTextStyle) : null);
        messageStyleBuilder.myTextSize(ta.getDimensionPixelSize(R.styleable.MessageItemsListView_myTextSize, context.getResources().getDimensionPixelSize(R.dimen.xdk_ui_text_size_message_item)));

        messageStyleBuilder.otherTextColor(ta.getColor(R.styleable.MessageItemsListView_theirTextColor, context.getResources().getColor(R.color.xdk_ui_color_primary_blue)));
        int otherTextStyle = ta.getInt(R.styleable.MessageItemsListView_theirTextStyle, Typeface.NORMAL);
        messageStyleBuilder.otherTextStyle(otherTextStyle);
        String otherTextTypefaceName = ta.getString(R.styleable.MessageItemsListView_theirTextTypeface);
        messageStyleBuilder.otherTextTypeface(otherTextTypefaceName != null ? Typeface.create(otherTextTypefaceName, otherTextStyle) : null);
        messageStyleBuilder.otherTextSize(ta.getDimensionPixelSize(R.styleable.MessageItemsListView_theirTextSize, context.getResources().getDimensionPixelSize(R.dimen.xdk_ui_text_size_message_item)));

        messageStyleBuilder.myBubbleColor(ta.getColor(R.styleable.MessageItemsListView_myBubbleColor, context.getResources().getColor(R.color.xdk_ui_color_primary_blue)));
        messageStyleBuilder.otherBubbleColor(ta.getColor(R.styleable.MessageItemsListView_theirBubbleColor, context.getResources().getColor(R.color.xdk_ui_color_primary_gray)));

        mShouldShowAvatarsInOneOnOneConversations = ta.getBoolean(R.styleable.MessageItemsListView_shouldShowAvatarsInOneOnOneConversations, false);
        ta.recycle();
        mMessageStyle = messageStyleBuilder.build();
    }

    private String getEmptyConversationHeaderText(Context context, Set<Identity> participants, Identity authenticatedUser) {

        if (participants.size() == 0 || authenticatedUser == null) return "";
        participants.remove(authenticatedUser);
        List<Identity> participantList = new ArrayList<>(participants);

        String headerText;

        switch (participants.size()) {
            case 0:
                headerText = context.getString(
                        R.string.xdk_ui_empty_conversation_with_zero_participant);
                break;
            case 1:
                headerText = context.getString(
                        R.string.xdk_ui_empty_conversation_with_one_participant,
                        participantList.get(0).getDisplayName());
                break;
            case 2:
                headerText = context.getString(
                        R.string.xdk_ui_empty_conversation_with_two_participants,
                        participantList.get(0).getDisplayName(),
                        participantList.get(1).getDisplayName());
                break;
            case 3:
                headerText = context.getString(
                        R.string.xdk_ui_empty_conversation_with_three_participants,
                        participantList.get(0).getDisplayName(),
                        participantList.get(1).getDisplayName());
                break;
            default:
                int remainder = participantList.size() - 2;
                headerText = context.getString(
                        R.string.xdk_ui_empty_conversation_with_many_participants,
                        participantList.get(0).getDisplayName(),
                        participantList.get(1).getDisplayName(), remainder);
        }

        return headerText;
    }


}
