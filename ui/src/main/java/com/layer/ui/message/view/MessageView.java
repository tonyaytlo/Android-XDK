package com.layer.ui.message.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.gson.JsonObject;
import com.layer.ui.message.action.ActionHandlerRegistry;
import com.layer.ui.message.container.MessageContainer;
import com.layer.ui.message.model.MessageModel;
import com.layer.ui.message.model.MessageModelManager;

public abstract class MessageView<VIEW_MODEL extends MessageModel> extends FrameLayout {
    private MessageModelManager mMessageModelManager;
    public MessageView(Context context) {
        super(context);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setMessageModel(VIEW_MODEL model);

    public abstract Class<? extends MessageContainer> getContainerClass();

    public void performAction(String event, JsonObject customData) {
        if (event != null) {
            ActionHandlerRegistry.dispatchEvent(getContext(), event, customData);
        }
    }

    public MessageModelManager getMessageModelManager() {
        return mMessageModelManager;
    }

    public void setMessageModelManager(MessageModelManager messageModelManager) {
        mMessageModelManager = messageModelManager;
    }
}
