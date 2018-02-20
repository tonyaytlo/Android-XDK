package com.layer.xdk.ui.message.adapter2;


import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.MessageCell;
import com.layer.xdk.ui.message.MessageCluster;
import com.layer.xdk.ui.message.MessageItemFooterViewHolder;
import com.layer.xdk.ui.message.MessageItemHeaderViewHolder;
import com.layer.xdk.ui.message.MessageItemHeaderViewModel;
import com.layer.xdk.ui.message.MessageItemLegacyViewHolder;
import com.layer.xdk.ui.message.MessageItemLegacyViewModel;
import com.layer.xdk.ui.message.MessageItemStatusViewHolder;
import com.layer.xdk.ui.message.MessageItemStatusViewModel;
import com.layer.xdk.ui.message.MessageModelCardViewHolder;
import com.layer.xdk.ui.message.MessageModelCardViewModel;
import com.layer.xdk.ui.message.binder.BinderRegistry;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.IdentityRecyclerViewEventListener;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// TODO AND-1242 Abstract a view holder base class
public class MessagesAdapter2 extends PagedListAdapter<MessageModel, RecyclerView.ViewHolder> {

    private final Map<Uri, MessageCluster> mClusterCache = new HashMap<>();
    private final Handler mUiThreadHandler;


    private BinderRegistry mBinderRegistry;
    private final Context mContext;
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


    public MessagesAdapter2(Context context, LayerClient layerClient, BinderRegistry binderRegistry,
            ImageCacheWrapper imageCacheWrapper, DateFormatter dateFormatter,
            IdentityFormatter identityFormatter) {
        super(DIFF_CALLBACK);
        mBinderRegistry = binderRegistry;
        mContext = context;
        mLayerClient = layerClient;
        mImageCacheWrapper = imageCacheWrapper;
        mDateFormatter = dateFormatter;
        mIdentityFormatter = identityFormatter;
        mUiThreadHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == mBinderRegistry.VIEW_TYPE_STATUS) {
            return createStatusMessageItemViewHolder(parent);
        }

        return createCardMessageItemViewHolder(parent);

//        if (viewType == mBinderRegistry.VIEW_TYPE_HEADER) {
//            return createHeaderViewHolder(parent);
//        } else if (viewType == mBinderRegistry.VIEW_TYPE_FOOTER) {
//            return createFooterViewHolder(parent);
//        } else if (viewType >= mBinderRegistry.VIEW_TYPE_LEGACY_START && viewType <= mBinderRegistry.VIEW_TYPE_LEGACY_END) {
//            MessageCell messageCell = mBinderRegistry.getMessageCellForViewType(viewType);
////            messageCell.mCellFactory.setStyle(getStyle());
//            return createLegacyMessageItemViewHolder(parent, messageCell);
//        } else if (viewType == mBinderRegistry.VIEW_TYPE_STATUS) {
//            return createStatusMessageItemViewHolder(parent);
//        } else if (viewType != mBinderRegistry.VIEW_TYPE_UNKNOWN){
//            return createCardMessageItemViewHolder(parent);
//        } else {
//            throw new IllegalStateException("Unknown View Type");
//        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageModel item = getItem(position);


        if (item != null) {
            if (holder instanceof MessageItemStatusViewHolder) {
                ((MessageItemStatusViewHolder) holder).bind(item);
            } else if (holder instanceof MessageModelCardViewHolder) {
                MessageCluster messageCluster = getClustering(item.getMessage(), position);
                ((MessageModelCardViewHolder) holder).bind(item, messageCluster, position, getRecipientStatusPosition(),
                        mRecyclerView.getWidth());
            }
        }
        // TODO And-1242 CLear if it is a placeholder


    }

    @Override
    public int getItemViewType(int position) {
        // TODO AND-1242 Header/footer

        MessageModel item = getItem(position);
        if (item == null) {
            // TODO AND-1242 Return invalid (placeholder)
            return -1;
        }
        // TODO AND-1242 Move this out of the binder registry?
        return mBinderRegistry.getViewType(item.getMessage());
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



    /*
     * ViewHolders
     */



    protected MessageItemHeaderViewHolder createHeaderViewHolder(ViewGroup parent) {
        return new MessageItemHeaderViewHolder(parent,
                new MessageItemHeaderViewModel(mContext, mLayerClient));
    }

    protected MessageItemFooterViewHolder createFooterViewHolder(ViewGroup parent) {
        MessageItemLegacyViewModel viewModel = new MessageItemLegacyViewModel(parent.getContext(),
                mLayerClient, getImageCacheWrapper(), getIdentityEventListener());

        viewModel.setEnableReadReceipts(false);
        viewModel.setShowAvatars(getShouldShowAvatarInOneOnOneConversations());
        viewModel.setShowPresence(false);

        return new MessageItemFooterViewHolder(parent, viewModel, getImageCacheWrapper());
    }

    protected MessageItemLegacyViewHolder createLegacyMessageItemViewHolder(ViewGroup parent, MessageCell messageCell) {
        MessageItemLegacyViewModel viewModel = new MessageItemLegacyViewModel(parent.getContext(),
                mLayerClient, getImageCacheWrapper(), getIdentityEventListener());

        viewModel.setEnableReadReceipts(areReadReceiptsEnabled());
        viewModel.setShowAvatars(getShouldShowAvatarInOneOnOneConversations());
        viewModel.setShowPresence(getShouldShowPresence());
        viewModel.setShouldShowAvatarForCurrentUser(getShouldShowAvatarForCurrentUser());
        viewModel.setShouldShowPresenceForCurrentUser(getShouldShowPresenceForCurrentUser());
        viewModel.setItemClickListener(getItemClickListener());

        return new MessageItemLegacyViewHolder(parent, viewModel, messageCell);
    }

    protected MessageModelCardViewHolder createCardMessageItemViewHolder(ViewGroup parent) {
        MessageModelCardViewModel viewModel = new MessageModelCardViewModel(parent.getContext(),
                mLayerClient, getImageCacheWrapper(), getIdentityEventListener());

        viewModel.setEnableReadReceipts(areReadReceiptsEnabled());
        viewModel.setShowAvatars(getShouldShowAvatarInOneOnOneConversations());
        viewModel.setShowPresence(getShouldShowPresence());
        viewModel.setShouldShowAvatarForCurrentUser(getShouldShowAvatarForCurrentUser());
        viewModel.setShouldShowPresenceForCurrentUser(getShouldShowPresenceForCurrentUser());
        viewModel.setItemClickListener(getItemClickListener());

        return new MessageModelCardViewHolder(parent, viewModel, mBinderRegistry.getMessageModelManager());
    }


    protected MessageItemStatusViewHolder createStatusMessageItemViewHolder(ViewGroup parent) {
        MessageItemStatusViewModel viewModel = new MessageItemStatusViewModel(parent.getContext(), mLayerClient, mBinderRegistry.getMessageModelManager());
        viewModel.setEnableReadReceipts(areReadReceiptsEnabled());
        return new MessageItemStatusViewHolder(parent, viewModel);
    }

    // TODO AND-1242 - Rework this
    private IdentityRecyclerViewEventListener getIdentityEventListener() {
        return new IdentityRecyclerViewEventListener(this);
    }


    protected Integer getRecipientStatusPosition() {
        return getItemCount() - 1;
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

    //==============================================================================================
    // Listeners
    //==============================================================================================

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



    //==============================================================================================
    // Clustering
    //==============================================================================================

    private static boolean isDateBoundary(Date d1, Date d2) {
        if (d1 == null || d2 == null) return false;
        return (d1.getYear() != d2.getYear()) || (d1.getMonth() != d2.getMonth()) || (d1.getDay()
                != d2.getDay());
    }

    // TODO: optimize by limiting search to positions in- and around- visible range
    protected MessageCluster getClustering(Message message, int position) {
        MessageCluster result = mClusterCache.get(message.getId());
        if (result == null) {
            result = new MessageCluster();
            mClusterCache.put(message.getId(), result);
        }

        int previousPosition = position - 1;
        Message previousMessage = (previousPosition >= 0) ? getItem(previousPosition).getMessage() : null;
        if (previousMessage != null) {
            result.mDateBoundaryWithPrevious = isDateBoundary(previousMessage.getReceivedAt(),
                    message.getReceivedAt());
            result.mClusterWithPrevious = MessageCluster.Type.fromMessages(previousMessage, message);

            MessageCluster previousMessageCluster = mClusterCache.get(previousMessage.getId());
            if (previousMessageCluster == null) {
                previousMessageCluster = new MessageCluster();
                mClusterCache.put(previousMessage.getId(), previousMessageCluster);
            } else {
                // does the previous need to change its clustering?
                if ((previousMessageCluster.mClusterWithNext != result.mClusterWithPrevious) ||
                        (previousMessageCluster.mDateBoundaryWithNext
                                != result.mDateBoundaryWithPrevious)) {
                    requestUpdate(previousMessage, previousPosition);
                }
            }
            previousMessageCluster.mClusterWithNext = result.mClusterWithPrevious;
            previousMessageCluster.mDateBoundaryWithNext = result.mDateBoundaryWithPrevious;
        }

        int nextPosition = position + 1;
        if (nextPosition >= getItemCount() || getItem(nextPosition) == null) {
            // TODO AND-1242 This is because placeholders are enabled
            return result;
        }
        Message nextMessage = (nextPosition < getItemCount()) ? getItem(nextPosition).getMessage() : null;
        if (nextMessage != null) {
            result.mDateBoundaryWithNext = isDateBoundary(message.getReceivedAt(),
                    nextMessage.getReceivedAt());
            result.mClusterWithNext = MessageCluster.Type.fromMessages(message, nextMessage);

            MessageCluster nextMessageCluster = mClusterCache.get(nextMessage.getId());
            if (nextMessageCluster == null) {
                nextMessageCluster = new MessageCluster();
                mClusterCache.put(nextMessage.getId(), nextMessageCluster);
            } else {
                // does the next need to change its clustering?
                if ((nextMessageCluster.mClusterWithPrevious != result.mClusterWithNext) ||
                        (nextMessageCluster.mDateBoundaryWithPrevious != result.mDateBoundaryWithNext)) {
                    requestUpdate(nextMessage, nextPosition);
                }
            }
            nextMessageCluster.mClusterWithPrevious = result.mClusterWithNext;
            nextMessageCluster.mDateBoundaryWithPrevious = result.mDateBoundaryWithNext;
        }

        if (mShouldShowAvatarForCurrentUser && nextMessage != null) {
            result.mNextMessageIsFromSameUser = message.getSender() == nextMessage.getSender()
                    && !result.mDateBoundaryWithNext;
        }

        return result;
    }

    private void requestUpdate(final Message message, final int lastPosition) {
        mUiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO AND-1242 Restore
//                notifyItemChanged(getPosition(message, lastPosition));
            }
        });
    }



    private static final DiffCallback<MessageModel> DIFF_CALLBACK = new DiffCallback<MessageModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull MessageModel oldItem, @NonNull MessageModel newItem) {
            return oldItem.getMessage().getId().equals(newItem.getMessage().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessageModel oldItem, @NonNull MessageModel newItem) {
            // TODO we should do a deep equals here
            if (newItem.getMessage().getUpdatedAt() == null) {
                return oldItem.getMessage().getUpdatedAt() == null;
            }
            return newItem.getMessage().getUpdatedAt().equals(oldItem.getMessage().getUpdatedAt());
        }
    };

}
