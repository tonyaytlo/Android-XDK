package com.layer.xdk.ui.message.container;

import android.support.annotation.LayoutRes;
import android.view.View;

import com.layer.xdk.ui.message.model.AbstractMessageModel;

public interface MessageContainer {
    View inflateMessageView(@LayoutRes int messageViewLayoutId);

    <T extends AbstractMessageModel> void setMessageModel(T model);

    <T extends AbstractMessageModel> void setContentBackground(T model);
}
