package com.layer.xdk.ui.message.adapter2;


import android.view.ViewGroup;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiMessageItemStatusBinding;

public class MessageStatusViewHolder extends MessageViewHolder<MessageStatusViewHolderModel, XdkUiMessageItemStatusBinding> {

    public MessageStatusViewHolder(ViewGroup parent, MessageStatusViewHolderModel model) {
        super(parent, R.layout.xdk_ui_message_item_status, model);
        getBinding().setViewHolderModel(model);
        getBinding().executePendingBindings();
    }

    @Override
    void onBind() {
        getViewHolderModel().update();
    }
}
