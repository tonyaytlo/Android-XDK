package com.layer.xdk.ui.message;


import android.view.ViewGroup;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiMessageItemStatusBinding;

public class MessageItemStatusViewHolder extends MessageItemViewHolder<MessageItemStatusViewModel, XdkUiMessageItemStatusBinding> {

    public MessageItemStatusViewHolder(ViewGroup parent, MessageItemStatusViewModel viewModel) {
        super(parent, R.layout.xdk_ui_message_item_status, viewModel);

        getBinding().setViewModel(viewModel);
    }

    public void bind() {
        getViewModel().update();
    }
}
