package com.layer.xdk.ui.message.text;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

import com.google.gson.JsonObject;
import com.layer.xdk.ui.message.MessageViewHelper;

public class TextMessageView extends AppCompatTextView {

    private MessageViewHelper mMessageViewHelper;
    private String mActionEvent;
    private JsonObject mActionData;

    public TextMessageView(Context context) {
        this(context, null, 0);
    }

    public TextMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMessageViewHelper = new MessageViewHelper(context);
        mMessageViewHelper.setOnClickListener(this, new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessageViewHelper.performAction(mActionEvent, mActionData);
            }
        });
    }

    public void setMessageModel(TextMessageModel model) {
        if (model == null) {
            mActionEvent = null;
            mActionData = null;
        } else {
            mActionEvent = model.getActionEvent();
            mActionData = model.getActionData();
        }
    }
}
