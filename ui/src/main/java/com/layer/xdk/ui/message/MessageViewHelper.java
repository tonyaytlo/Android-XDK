package com.layer.xdk.ui.message;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.JsonObject;
import com.layer.xdk.ui.message.action.ActionHandlerRegistry;
import com.layer.xdk.ui.message.model.MessageModel;

public class MessageViewHelper {
    private Context mContext;
    private String mActionEvent;
    private JsonObject mActionData;

    private View.OnClickListener mCurrentClickListener;
    private View.OnClickListener mContainingClickListener;
    private View.OnLongClickListener mContainingLongClickListener;

    public MessageViewHelper(Context context) {
        mContext = context;
    }

    public void performAction() {
        if (!TextUtils.isEmpty(mActionEvent)) {
            ActionHandlerRegistry.dispatchEvent(mContext, mActionEvent, mActionData);
        }
    }

    public void setMessageModel(@Nullable MessageModel model) {
        if (model == null) {
            mActionEvent = null;
            mActionData = null;
        } else {
            mActionEvent = model.getActionEvent();
            mActionData = model.getActionData();
        }
    }

    // TODO - AND-1242 Hook up containing click listeners
    public void setContainingClickListener(View view, View.OnClickListener containingClickListener) {
        mContainingClickListener = containingClickListener;
        if (mCurrentClickListener == null) {
            view.setOnClickListener(null);
        }
    }

    public void setContainingLongClickListener(View view, View.OnLongClickListener containingLongClickListener) {
        mContainingLongClickListener = containingLongClickListener;
        // This overwrites long clicks set in constructors. Long clicks should be reserved to use
        // by the containing click listener
        view.setOnLongClickListener(mContainingLongClickListener);
    }

    public void setOnClickListener(View view, @Nullable final View.OnClickListener listener) {
        mCurrentClickListener = listener;
        view.setOnClickListener(new View.OnClickListener() {
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
}
