package com.layer.xdk.ui.message.view;


import com.layer.xdk.ui.message.model.MessageModel;

// TODO - AND-1242 Document
public interface ParentMessageView {

    <T extends MessageModel> void inflateChildLayouts(T model);
}
