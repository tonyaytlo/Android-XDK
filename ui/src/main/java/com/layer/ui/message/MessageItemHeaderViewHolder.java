package com.layer.ui.message;

import android.view.View;
import android.view.ViewGroup;

import com.layer.sdk.messaging.Conversation;
import com.layer.ui.R;
import com.layer.ui.databinding.UiMessageItemHeaderBinding;

public class MessageItemHeaderViewHolder extends MessageItemViewHolder<MessageItemHeaderViewModel, UiMessageItemHeaderBinding> {
    public MessageItemHeaderViewHolder(ViewGroup parent, MessageItemHeaderViewModel viewModel) {
        super(parent, R.layout.ui_message_item_header, viewModel);
    }

    public void bind(View headerView, Conversation conversation) {

        if (headerView instanceof EmptyMessageListHeaderView) {
            ((EmptyMessageListHeaderView) headerView).getUiEmptyMessageItemsListBinding().setViewModel(getViewModel());
        }
        getBinding().content.addView(headerView);
        getViewModel().setConversation(conversation);
        getViewModel().notifyChange();
    }
}
