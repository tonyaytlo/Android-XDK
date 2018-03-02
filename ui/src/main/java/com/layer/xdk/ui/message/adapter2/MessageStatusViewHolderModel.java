package com.layer.xdk.ui.message.adapter2;


import android.content.Context;
import android.databinding.Bindable;

import com.layer.sdk.LayerClient;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.response.ResponseMessageModel;
import com.layer.xdk.ui.message.status.StatusMessageModel;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.viewmodel.MessageViewHolderModel;

@SuppressWarnings("WeakerAccess")
public class MessageStatusViewHolderModel extends MessageViewHolderModel {
    private boolean mEnableReadReceipts;
    private boolean mVisible;
    private String mText;

    public MessageStatusViewHolderModel(Context context, LayerClient layerClient,
            IdentityFormatter identityFormatter,
            DateFormatter dateFormatter) {
        super(context, layerClient, identityFormatter, dateFormatter);

    }

    public void update() {
        if (getItem() instanceof ResponseMessageModel) {
            ResponseMessageModel responseModel = (ResponseMessageModel) getItem();
            mText = responseModel.getText();
        } else if (getItem() instanceof StatusMessageModel) {
            mText = ((StatusMessageModel) getItem()).getText();
        }
        mVisible = ((MessageModel) getItem()).getHasContent();

        if (!getItem().isMessageFromMe() && mEnableReadReceipts) {
            getItem().getMessage().markAsRead();
        }
        notifyChange();
    }

    public void setEnableReadReceipts(boolean enableReadReceipts) {
        mEnableReadReceipts = enableReadReceipts;
    }

    @Bindable
    public CharSequence getText() {
        return mText;
    }

    @Bindable
    public boolean isVisible() {
        return mVisible;
    }
}
