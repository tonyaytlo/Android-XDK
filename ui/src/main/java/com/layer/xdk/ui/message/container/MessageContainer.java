package com.layer.xdk.ui.message.container;

import android.support.annotation.LayoutRes;
import android.view.View;

import com.layer.xdk.ui.message.model.MessageModel;

public interface MessageContainer {
    View inflateMessageView(@LayoutRes int messageViewLayoutId);

    <T extends MessageModel> void setMessageModel(T model);

    <T extends MessageModel> void setContentBackground(T model);
}
