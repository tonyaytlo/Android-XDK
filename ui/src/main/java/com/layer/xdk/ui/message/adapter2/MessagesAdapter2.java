package com.layer.xdk.ui.message.adapter2;


import android.arch.paging.PagedListAdapter;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.container.MessageContainer;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.response.ResponseMessageModel;
import com.layer.xdk.ui.message.status.StatusMessageModel;
import com.layer.xdk.ui.message.view.ParentMessageView;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.Log;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

public class MessagesAdapter2 extends PagedListAdapter<MessageModel, MessageViewHolder> {


    private final LayerClient mLayerClient;
    private final ImageCacheWrapper mImageCacheWrapper;
    private final DateFormatter mDateFormatter;
    private final IdentityFormatter mIdentityFormatter;
    private RecyclerView mRecyclerView;


    private boolean mIsOneOnOneConversation;
    private boolean mShouldShowAvatarInOneOnOneConversations;
    private boolean mShouldShowAvatarPresence = true;
    private boolean mShouldShowAvatarForCurrentUser;
    private boolean mShouldShowPresenceForCurrentUser;
    private boolean mReadReceiptsEnabled = true;

    // TODO AND-1242 Change to MessageModel?
    private OnItemClickListener<Message> mItemClickListener;

    private MessageModel mLastModelForViewTypeLookup;


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
        MessageModel model = getModelForViewType(viewType);

        // TODO AND-1242 Header/footer creations

        if (model instanceof StatusMessageModel || model instanceof ResponseMessageModel) {
            return createStatusViewHolder(parent);
        } else {
            return createDefaultViewHolder(parent, model);
        }


//        if (viewType == mBinderRegistry.VIEW_TYPE_STATUS) {
//            return createStatusViewHolder(parent);
//        }
//
//        return createDefaultViewHolder(parent);

//        if (viewType == mBinderRegistry.VIEW_TYPE_HEADER) {
//            return createHeaderViewHolder(parent);
//        } else if (viewType == mBinderRegistry.VIEW_TYPE_FOOTER) {
//            return createFooterViewHolder(parent);
//        } else if (viewType >= mBinderRegistry.VIEW_TYPE_LEGACY_START && viewType <= mBinderRegistry.VIEW_TYPE_LEGACY_END) {
//            MessageCell messageCell = mBinderRegistry.getMessageCellForViewType(viewType);
////            messageCell.mCellFactory.setStyle(getStyle());
//            return createLegacyMessageItemViewHolder(parent, messageCell);
//        } else if (viewType == mBinderRegistry.VIEW_TYPE_STATUS) {
//            return createStatusViewHolder(parent);
//        } else if (viewType != mBinderRegistry.VIEW_TYPE_UNKNOWN){
//            return createDefaultViewHolder(parent);
//        } else {
//            throw new IllegalStateException("Unknown View Type");
//        }
    }


    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel item = getItem(position);
        holder.bindItem(item);
    }

    @Override
    public int getItemViewType(int position) {
        // TODO AND-1242 Header/footer

        mLastModelForViewTypeLookup = getItem(position);
        return mLastModelForViewTypeLookup.getMimeTypeTree().hashCode();
    }


    @CallSuper
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = null;
    }



    public void setItemClickListener(OnItemClickListener<Message> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public OnItemClickListener<Message> getItemClickListener() {
        return mItemClickListener;
    }

    @NonNull
    @Override
    protected MessageModel getItem(int position) {
        MessageModel item = super.getItem(position);
        if (item == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Items should be non null");
            }
            throw new IllegalStateException("Items should be non null");
        }
        return item;
    }


    /*
     * ViewHolders
     */



//    protected MessageItemHeaderViewHolder createHeaderViewHolder(ViewGroup parent) {
//        return new MessageItemHeaderViewHolder(parent,
//                new MessageItemHeaderViewModel(mContext, mLayerClient));
//    }
//
//    protected MessageItemFooterViewHolder createFooterViewHolder(ViewGroup parent) {
//        MessageItemLegacyViewModel viewModel = new MessageItemLegacyViewModel(parent.getAppContext(),
//                mLayerClient, getImageCacheWrapper(), getIdentityEventListener());
//
//        viewModel.setEnableReadReceipts(false);
//        viewModel.setShowAvatars(getShouldShowAvatarInOneOnOneConversations());
//        viewModel.setShowPresence(false);
//
//        return new MessageItemFooterViewHolder(parent, viewModel, getImageCacheWrapper());
//    }


    protected MessageDefaultViewHolder createDefaultViewHolder(ViewGroup parent, MessageModel model) {
        MessageDefaultViewHolderModel viewModel = new MessageDefaultViewHolderModel(
                parent.getContext(),
                mLayerClient, getImageCacheWrapper(), mIdentityFormatter, mDateFormatter);

        viewModel.setEnableReadReceipts(areReadReceiptsEnabled());
        viewModel.setShowAvatars(getShouldShowAvatarInOneOnOneConversations());
        viewModel.setShowPresence(getShouldShowPresence());
        viewModel.setShouldShowAvatarForCurrentUser(getShouldShowAvatarForCurrentUser());
        viewModel.setShouldShowPresenceForCurrentUser(getShouldShowPresenceForCurrentUser());
        viewModel.setItemClickListener(getItemClickListener());

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
        if (messageView instanceof ParentMessageView) {
            ((ParentMessageView) messageView).inflateChildLayouts(model);
        }
    }

    /*
     * Settings
     */


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
            return oldItem.deepEquals(newItem) && oldItem.messageDeepEquals(newItem.getMessage());
        }
    };

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
