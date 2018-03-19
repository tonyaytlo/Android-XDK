package com.layer.xdk.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.layer.sdk.query.RecyclerViewController;
import com.layer.xdk.ui.conversationitem.ConversationItemViewModel;
import com.layer.xdk.ui.databinding.XdkUiFourPartItemBinding;
import com.layer.xdk.ui.fourpartitem.FourPartItemViewHolder;
import com.layer.xdk.ui.style.FourPartItemStyle;
import com.layer.xdk.ui.util.IdentityRecyclerViewEventListener;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

import javax.inject.Inject;

import dagger.internal.Factory;

public class ConversationItemsAdapter extends ItemRecyclerViewAdapter<Conversation,
        ConversationItemViewModel, XdkUiFourPartItemBinding, FourPartItemStyle,
        FourPartItemViewHolder<Conversation, ConversationItemViewModel>> {
    private static final String TAG = "ConversationItemsAdapter";

    private long mInitialHistory = 0;
    private final IdentityRecyclerViewEventListener mIdentityEventListener;

    private ImageCacheWrapper mImageCacheWrapper;

    private Factory<ConversationItemViewModel> mItemViewModelFactory;

    @Inject
    public ConversationItemsAdapter(Context context, LayerClient layerClient,
                                    ImageCacheWrapper imageCacheWrapper,
                                    Factory<ConversationItemViewModel> itemViewModelFactory) {
        super(context, layerClient, TAG, false);
        mImageCacheWrapper = imageCacheWrapper;
        mItemViewModelFactory = itemViewModelFactory;
        mIdentityEventListener = new IdentityRecyclerViewEventListener(this);

        layerClient.registerEventListener(mIdentityEventListener);
    }

    //==============================================================================================
    // Superclass methods
    //==============================================================================================

    @NonNull
    @Override
    public FourPartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        XdkUiFourPartItemBinding binding = XdkUiFourPartItemBinding.inflate(getLayoutInflater(), parent, false);
        ConversationItemViewModel viewModel = mItemViewModelFactory.get();

        viewModel.setItemClickListener(getItemClickListener());
        viewModel.setItemLongClickListener(getItemLongClickListener());
        viewModel.setAuthenticatedUser(getLayerClient().getAuthenticatedUser());

        FourPartItemViewHolder itemViewHolder = new FourPartItemViewHolder<>(binding, viewModel, getStyle(), mImageCacheWrapper);

        binding.addOnRebindCallback(getOnRebindCallback());

        return itemViewHolder;
    }

    @Override
    public void onQueryDataSetChanged(RecyclerViewController recyclerViewController) {
        syncInitialMessages(0, getItemCount());
        super.onQueryDataSetChanged(recyclerViewController);
    }

    @Override
    public void onQueryItemInserted(RecyclerViewController recyclerViewController, int position) {
        syncInitialMessages(position, 1);
        super.onQueryItemInserted(recyclerViewController, position);
    }

    @Override
    public void onQueryItemRangeInserted(RecyclerViewController recyclerViewController, int positionStart, int itemCount) {
        syncInitialMessages(positionStart, itemCount);
        super.onQueryItemRangeInserted(recyclerViewController, positionStart, itemCount);
    }

    @Override
    public void onDestroy() {
        getLayerClient().unregisterEventListener(mIdentityEventListener);
    }

    //==============================================================================================
    // Initial message history
    //==============================================================================================

    public void setInitialHistoricMessagesToFetch(long initialHistory) {
        mInitialHistory = initialHistory;
    }

    private void syncInitialMessages(final int start, final int length) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long desiredHistory = mInitialHistory;
                if (desiredHistory <= 0) return;
                for (int i = start; i < start + length; i++) {
                    try {
                        final Conversation conversation = getItem(i);
                        if (conversation == null || conversation.getHistoricSyncStatus() != Conversation.HistoricSyncStatus.MORE_AVAILABLE) {
                            continue;
                        }
                        Query<Message> localCountQuery = Query.builder(Message.class)
                                .predicate(new Predicate(Message.Property.CONVERSATION, Predicate.Operator.EQUAL_TO, conversation))
                                .build();
                        long delta = desiredHistory - getLayerClient().executeQueryForCount(localCountQuery);
                        if (delta > 0) conversation.syncMoreHistoricMessages((int) delta);
                    } catch (IndexOutOfBoundsException e) {
                        // Concurrent modification
                    }
                }
            }
        }).start();
    }
}
