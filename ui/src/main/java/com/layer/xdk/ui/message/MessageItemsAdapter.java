package com.layer.xdk.ui.message;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.imagecache.ImageCacheWrapper;

public class MessageItemsAdapter extends MessagesAdapter {

    public MessageItemsAdapter(Context context, LayerClient layerClient,
                               ImageCacheWrapper imageCacheWrapper, DateFormatter dateFormatter,
                               IdentityFormatter identityFormatter) {
        super(context, layerClient, imageCacheWrapper, dateFormatter, identityFormatter);
    }

    @Override
    protected MessageItemHeaderViewHolder createHeaderViewHolder(ViewGroup parent) {
        return new MessageItemHeaderViewHolder(parent,
                new MessageItemHeaderViewModel(getContext(), getLayerClient()));
    }

    @Override
    public void bindHeader(MessageItemViewHolder viewHolder) {
        MessageItemHeaderViewHolder holder = (MessageItemHeaderViewHolder) viewHolder;
        View headerView = getHeaderView();
        if (headerView.getParent() != null) {
            ((ViewGroup) headerView.getParent()).removeView(headerView);
        }

        holder.bind(headerView, mConversation);
    }

    @Override
    protected MessageItemCardViewHolder createCardMessageItemViewHolder(ViewGroup parent) {
        MessageItemCardViewModel viewModel = new MessageItemCardViewModel(parent.getContext(),
                getLayerClient(), getImageCacheWrapper(), getIdentityEventListener());

        viewModel.setEnableReadReceipts(areReadReceiptsEnabled());
        viewModel.setShowAvatars(getShouldShowAvatarInOneOnOneConversations());
        viewModel.setShowPresence(getShouldShowPresence());
        viewModel.setShouldShowAvatarForCurrentUser(getShouldShowAvatarForCurrentUser());
        viewModel.setShouldShowPresenceForCurrentUser(getShouldShowPresenceForCurrentUser());
        viewModel.setItemClickListener(getItemClickListener());

        return new MessageItemCardViewHolder(parent, viewModel, getBinderRegistry().getMessageModelManager());
    }

    @Override
    public void bindCardMessageItem(MessageItemViewHolder viewHolder, MessageCluster messageCluster, int position) {
        MessageItemCardViewHolder holder = (MessageItemCardViewHolder) viewHolder;

        holder.bind(messageCluster, position, getRecipientStatusPosition(), getRecyclerView().getWidth());
    }

    @Override
    protected MessageItemFooterViewHolder createFooterViewHolder(ViewGroup parent) {

        return new MessageItemFooterViewHolder(parent, null, getImageCacheWrapper());
    }

    @Override
    public void bindFooter(MessageItemViewHolder holder) {
        MessageItemFooterViewHolder viewHolder = (MessageItemFooterViewHolder) holder;
        viewHolder.clear();

        View footerView = getFooterView();

        if (footerView.getParent() != null) {
            ((ViewGroup) footerView.getParent()).removeView(footerView);
        }

        boolean shouldAvatarViewBeVisible = !(isOneOnOneConversation() & !getShouldShowAvatarInOneOnOneConversations());
        viewHolder.bind(getUsersTyping(), footerView, shouldAvatarViewBeVisible);
    }

    @Override
    protected MessageItemViewHolder createStatusMessageItemViewHolder(ViewGroup parent) {
//        MessageItemStatusViewModel viewModel = new MessageItemStatusViewModel(parent.getContext(), getLayerClient(), getBinderRegistry().getMessageModelManager());
//        viewModel.setEnableReadReceipts(areReadReceiptsEnabled());
//        return new MessageItemStatusViewHolder(parent, viewModel);
        return null;
    }

    @Override
    public void bindStatusMessageItem(MessageItemViewHolder viewHolder) {
//        MessageItemStatusViewHolder holder = (MessageItemStatusViewHolder) viewHolder;
//        holder.bind();
    }
}
