package com.layer.xdk.ui.message.adapter2;


import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Identity;
import com.layer.xdk.ui.TypingIndicatorLayout;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.container.MessageContainer;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.response.ResponseMessageModel;
import com.layer.xdk.ui.message.status.StatusMessageModel;
import com.layer.xdk.ui.message.view.ParentMessageView;
import com.layer.xdk.ui.recyclerview.OnItemLongClickListener;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.Log;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

import java.util.Set;

public class MessagesAdapter2 extends PagedListAdapter<MessageModel, MessageViewHolder> {

    private static final int VIEW_TYPE_TYPING_INDICATOR = "TypingIndicator".hashCode();

    private final LayerClient mLayerClient;
    private final ImageCacheWrapper mImageCacheWrapper;
    private final DateFormatter mDateFormatter;
    private final IdentityFormatter mIdentityFormatter;

    private boolean mOneOnOneConversation;
    private boolean mShouldShowAvatarInOneOnOneConversations;
    private boolean mShouldShowAvatarPresence = true;
    private boolean mShouldShowAvatarForCurrentUser;
    private boolean mShouldShowPresenceForCurrentUser;
    private boolean mReadReceiptsEnabled = true;
    private boolean mShowTypingIndicator = true;
    private View mTypingIndicatorLayout;
    private Set<Identity> mUsersTyping;

    private MessageModel mLastModelForViewTypeLookup;
    private OnItemLongClickListener<MessageModel> mItemLongClickListener;


    public MessagesAdapter2(LayerClient layerClient,
            ImageCacheWrapper imageCacheWrapper, DateFormatter dateFormatter,
            IdentityFormatter identityFormatter) {
        super(DIFF_CALLBACK);
        mLayerClient = layerClient;
        mImageCacheWrapper = imageCacheWrapper;
        mDateFormatter = dateFormatter;
        mIdentityFormatter = identityFormatter;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VIEW_TYPE_TYPING_INDICATOR == viewType) {
            return createTypingIndicatorViewHolder(parent);
        }

        MessageModel model = getModelForViewType(viewType);

        if (model instanceof StatusMessageModel || model instanceof ResponseMessageModel) {
            return createStatusViewHolder(parent);
        } else {
            return createDefaultViewHolder(parent, model);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        if (position == getTypingIndicatorPosition()) {
            bindTypingIndicator((MessageTypingIndicatorViewHolder) holder);
            return;
        }
        MessageModel item = getItem(position);
        holder.bindItem(item);
    }

    private void bindTypingIndicator(MessageTypingIndicatorViewHolder holder) {
        holder.clear();

        if (mTypingIndicatorLayout.getParent() != null) {
            ((ViewGroup) mTypingIndicatorLayout.getParent()).removeView(mTypingIndicatorLayout);
        }

        boolean shouldAvatarViewBeVisible = !(isOneOnOneConversation() & !getShouldShowAvatarInOneOnOneConversations());
        holder.bind(mUsersTyping, mTypingIndicatorLayout, shouldAvatarViewBeVisible);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getTypingIndicatorPosition()) {
            return VIEW_TYPE_TYPING_INDICATOR;
        }

        mLastModelForViewTypeLookup = getItem(position);
        return mLastModelForViewTypeLookup.getMimeTypeTree().hashCode();
    }

    @NonNull
    @Override
    protected MessageModel getItem(int position) {
        if (mTypingIndicatorLayout != null && mShowTypingIndicator) {
            if (position == 0) {
                throw new IllegalArgumentException("Cannot fetch the typing indicator view");
            }
            position--;
        }

        MessageModel item = super.getItem(position);
        if (item == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Items should be non null");
            }
            throw new IllegalStateException("Items should be non null");
        }
        return item;
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        if (mTypingIndicatorLayout != null && mShowTypingIndicator) {
            return count + 1;
        }
        return count;
    }

    /*
     * ViewHolders
     */

    protected MessageTypingIndicatorViewHolder createTypingIndicatorViewHolder(ViewGroup parent) {
        MessageTypingIndicatorViewHolderModel model = new MessageTypingIndicatorViewHolderModel(
                parent.getContext(),
                mLayerClient,
                getImageCacheWrapper(),
                mIdentityFormatter,
                mDateFormatter);
        return new MessageTypingIndicatorViewHolder(parent, model);
    }


    protected MessageDefaultViewHolder createDefaultViewHolder(ViewGroup parent, MessageModel model) {
        MessageDefaultViewHolderModel viewModel = new MessageDefaultViewHolderModel(
                parent.getContext(),
                mLayerClient, getImageCacheWrapper(), mIdentityFormatter, mDateFormatter);

        viewModel.setEnableReadReceipts(areReadReceiptsEnabled());
        viewModel.setShowAvatars(getShouldShowAvatarInOneOnOneConversations());
        viewModel.setShowPresence(getShouldShowPresence());
        viewModel.setShouldShowAvatarForCurrentUser(getShouldShowAvatarForCurrentUser());
        viewModel.setShouldShowPresenceForCurrentUser(getShouldShowPresenceForCurrentUser());
        viewModel.setItemLongClickListener(getItemLongClickListener());

        MessageDefaultViewHolder viewHolder = new MessageDefaultViewHolder(parent, viewModel);
        inflateDefaultViewHolder(viewHolder, model);
        return viewHolder;
    }

    protected MessageStatusViewHolder createStatusViewHolder(ViewGroup parent) {
        MessageStatusViewHolderModel viewModel = new MessageStatusViewHolderModel(parent.getContext(),
                mLayerClient, mIdentityFormatter, mDateFormatter);
        viewModel.setEnableReadReceipts(areReadReceiptsEnabled());
        return new MessageStatusViewHolder(parent, viewModel);
    }

    private void inflateDefaultViewHolder(MessageDefaultViewHolder viewHolder, MessageModel model) {
        MessageContainer rootMessageContainer =
                (MessageContainer) viewHolder.inflateViewContainer(model.getContainerViewLayoutId());

        View messageView = rootMessageContainer.inflateMessageView(model.getViewLayoutId());
        messageView.setOnLongClickListener(viewHolder.getLongClickListener());
        if (messageView instanceof ParentMessageView) {
            ((ParentMessageView) messageView).inflateChildLayouts(model, viewHolder.getLongClickListener());
        }
    }

    /*
     * Settings
     */

    public boolean isOneOnOneConversation() {
        return mOneOnOneConversation;
    }

    public void setOneOnOneConversation(boolean oneOnOneConversation) {
        mOneOnOneConversation = oneOnOneConversation;
    }

    /**
     * @return If the AvatarViewModel for the other participant in a one on one conversation  will
     * be shown
     * or not
     */
    public boolean getShouldShowAvatarInOneOnOneConversations() {
        return mShouldShowAvatarInOneOnOneConversations;
    }

    /**
     * @param shouldShowAvatarInOneOnOneConversations Whether the AvatarViewModel for the other
     *                                                participant
     *                                                in a one on one conversation should be shown
     *                                                or not
     */
    public void setShouldShowAvatarInOneOnOneConversations(
            boolean shouldShowAvatarInOneOnOneConversations) {
        mShouldShowAvatarInOneOnOneConversations = shouldShowAvatarInOneOnOneConversations;
    }

    /**
     * @return If the AvatarViewModel for the other participant in a one on one conversation will be
     * shown
     * or not. Defaults to `true`.
     */
    public boolean getShouldShowPresence() {
        return mShouldShowAvatarPresence;
    }

    public boolean getShouldShowAvatarForCurrentUser() {
        return mShouldShowAvatarForCurrentUser;
    }

    public boolean getShouldShowPresenceForCurrentUser() {
        return mShouldShowPresenceForCurrentUser;
    }

    /**
     * @param shouldShowPresence Whether the AvatarView for the other participant in a one on one
     *                           conversation should be shown or not. Default is `true`.
     */
    public void setShouldShowAvatarPresence(boolean shouldShowPresence) {
        mShouldShowAvatarPresence = shouldShowPresence;
    }

    public void setShouldShowAvatarForCurrentUser(boolean shouldShowAvatarForCurrentUser) {
        mShouldShowAvatarForCurrentUser = shouldShowAvatarForCurrentUser;
    }

    public void setShouldShowPresenceForCurrentUser(boolean shouldShowPresenceForCurrentUser) {
        mShouldShowPresenceForCurrentUser = shouldShowPresenceForCurrentUser;
    }

    protected ImageCacheWrapper getImageCacheWrapper() {
        return mImageCacheWrapper;
    }

    public boolean areReadReceiptsEnabled() {
        return mReadReceiptsEnabled;
    }

    /**
     * Set whether or not the conversation supports read receipts. This determines if the read
     * receipts should be shown in the view holders.
     *
     * @param readReceiptsEnabled true if the conversation is adapter is used for supports read
     *                            receipts
     */
    public void setReadReceiptsEnabled(boolean readReceiptsEnabled) {
        mReadReceiptsEnabled = readReceiptsEnabled;
    }

    /**
     * Set the long click listener that will be added to the view holder and message views.
     *
     * @param itemLongClickListener listener to set on view holder and message views
     */
    public void setItemLongClickListener(OnItemLongClickListener<MessageModel> itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
    }

    public OnItemLongClickListener<MessageModel> getItemLongClickListener() {
        return mItemLongClickListener;
    }

    /**
     * Copying this from Groupie which copied it from Epoxy.
     */
    private MessageModel getModelForViewType(int viewType) {
        if (mLastModelForViewTypeLookup != null
                && mLastModelForViewTypeLookup.getMimeTypeTree().hashCode() == viewType) {
            // We expect this to be a hit 100% of the time
            return mLastModelForViewTypeLookup;
        }

        // To be extra safe in case RecyclerView implementation details change...
        for (int i = 0; i < getItemCount(); i++) {
            MessageModel item = getItem(i);
            if (item.getMimeTypeTree().hashCode() == viewType) {
                return item;
            }
        }

        throw new IllegalStateException("Could not find model for view type: " + viewType);
    }

    private static final DiffUtil.ItemCallback<MessageModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<MessageModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull MessageModel oldItem, @NonNull MessageModel newItem) {
            return oldItem.getMessage().getId().equals(newItem.getMessage().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessageModel oldItem, @NonNull MessageModel newItem) {
            return oldItem.deepEquals(newItem);
        }
    };

    public boolean shouldShowTypingIndicator() {
        return mShowTypingIndicator;
    }

    public void setShowTypingIndicator(boolean showTypingIndicator) {
        mShowTypingIndicator = showTypingIndicator;
    }

    public void setTypingIndicatorLayout(TypingIndicatorLayout layout, Set<Identity> users) {
        if (mShowTypingIndicator) {
            boolean isNull = layout == null;
            boolean wasNull = mTypingIndicatorLayout == null;
            mTypingIndicatorLayout = layout;
            mUsersTyping = users;

            if (wasNull && !isNull) {
                // Insert
                notifyItemInserted(0);
            } else if (!wasNull && isNull) {
                // Delete
                notifyItemRemoved(0);
            } else if (!wasNull && !isNull) {
                // Change
                notifyItemChanged(0);
            }
        }
    }

    private int getTypingIndicatorPosition() {
        if (mShowTypingIndicator && mTypingIndicatorLayout != null) return 0;
        return -1;
    }

    /**
     * Listens for inserts to the beginning of an MessagesAdapter2. This will be called when items
     * are prepended to the beginning of this adapter (i.e. new messages are received).  This is
     * useful for implementing a scroll-to-bottom feature.
     */
    public abstract static class NewMessageReceivedObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (positionStart == 0) {
                onNewMessageReceived();
            }
        }

        /**
         * Alerts the observer when a newer message was prepended
         */
        public abstract void onNewMessageReceived();
    }

}
