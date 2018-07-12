package com.layer.xdk.ui.message.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.layer.xdk.ui.message.action.ActionHandlerRegistry;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.util.Log;

public class MessageViewHelper {
    private Context mContext;
    private String mActionEvent;
    private MessageModel mMessageModel;

    public MessageViewHelper(Context context) {
        mContext = context;
    }

    public void performAction() {
        if (!TextUtils.isEmpty(mActionEvent) && mMessageModel != null) {
            ActionHandlerRegistry.dispatchEvent(mContext, mActionEvent, mMessageModel);
        } else if (Log.isLoggable(Log.INFO)) {
            Log.i("Unable to perform action event (" + mActionEvent + ") with model: "
                    + mMessageModel);
        }
    }

    public void setMessageModel(@Nullable MessageModel model) {
        if (model == null) {
            mActionEvent = null;
            mMessageModel = null;
        } else {
            mActionEvent = model.getActionEvent();
            mMessageModel = model;
        }
    }
}
