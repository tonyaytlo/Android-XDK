package com.layer.xdk.ui.message;

import android.view.View;
import android.view.ViewGroup;

import com.layer.sdk.messaging.Conversation;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiMessageItemHeaderBinding;

public class MessageItemHeaderViewHolder extends MessageItemViewHolder<MessageItemHeaderViewModel, XdkUiMessageItemHeaderBinding> {
    public MessageItemHeaderViewHolder(ViewGroup parent, MessageItemHeaderViewModel viewModel) {
        super(parent, R.layout.xdk_ui_message_item_header, viewModel);
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
