package com.layer.xdk.ui.message.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.google.gson.JsonObject;
import com.layer.xdk.ui.message.action.ActionHandlerRegistry;
import com.layer.xdk.ui.message.container.MessageContainer;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.model.MessageModelManager;

public abstract class MessageView<VIEW_MODEL extends MessageModel> extends FrameLayout {

    private MessageModelManager mMessageModelManager;
    private OnClickListener mCurrentClickListener;
    private OnClickListener mContainingClickListener;
    private OnLongClickListener mContainingLongClickListener;

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

    public void setContainingClickListener(OnClickListener containingClickListener) {
        mContainingClickListener = containingClickListener;
        if (mCurrentClickListener == null) {
            setOnClickListener(null);
        }
    }

    public void setContainingLongClickListener(OnLongClickListener containingLongClickListener) {
        mContainingLongClickListener = containingLongClickListener;
        // This overwrites long clicks set in constructors. Long clicks should be reserved to use
        // by the containing click listener
        setOnLongClickListener(mContainingLongClickListener);
    }

    @Override
    public void setOnClickListener(@Nullable final OnClickListener listener) {
        mCurrentClickListener = listener;
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContainingClickListener != null) {
                    mContainingClickListener.onClick(v);
                }
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });
    }

    /**
     * Use of adding long clicks in message views is discouraged. Additional long click actions
     * should be handled in the containing {@link com.layer.xdk.ui.recyclerview.OnItemClickListener}
     *
     * @param listener This parameter is ignored
     */
    @Override
    public void setOnLongClickListener(@Nullable final OnLongClickListener listener) {
        super.setOnLongClickListener(mContainingLongClickListener);
    }
}
