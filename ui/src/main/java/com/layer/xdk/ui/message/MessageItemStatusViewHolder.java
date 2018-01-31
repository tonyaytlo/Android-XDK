package com.layer.xdk.ui.message;


import android.view.ViewGroup;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.UiMessageItemStatusBinding;

public class MessageItemStatusViewHolder extends MessageItemViewHolder<MessageItemStatusViewModel, UiMessageItemStatusBinding> {

    public MessageItemStatusViewHolder(ViewGroup parent, MessageItemStatusViewModel viewModel) {
        super(parent, R.layout.ui_message_item_status, viewModel);

        getBinding().setViewModel(viewModel);
    }

    public void bind() {
        getViewModel().update();
    }
}
