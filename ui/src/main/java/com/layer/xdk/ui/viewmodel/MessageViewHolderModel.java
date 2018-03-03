package com.layer.xdk.ui.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Message;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.util.DateFormatter;

public class MessageViewHolderModel extends BaseObservable {
    private Context mContext;
    private LayerClient mLayerClient;

    private MessageModel mItem;
    // TODO AND-1242 - Change to MessageModel?
    private OnItemClickListener<Message> mItemClickListener;
    private IdentityFormatter mIdentityFormatter;
    private DateFormatter mDateFormatter;

    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;

    public MessageViewHolderModel(Context context, LayerClient layerClient, IdentityFormatter identityFormatter, DateFormatter dateFormatter) {
        mContext = context;
        mLayerClient = layerClient;

        mIdentityFormatter = identityFormatter;
        mDateFormatter = dateFormatter;

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(mItem.getMessage());
                }
            }
        };

        mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemClickListener!=null) {
                    return mItemClickListener.onItemLongClick(mItem.getMessage());
                } else {
                    return false;
                }
            }
        };
    }

    @Bindable
    public MessageModel getItem() {
        return mItem;
    }

    public void setItem(MessageModel item) {
        mItem = item;
    }

    public void setEmpty() {
        mItem = null;
    }

    public void setItemClickListener(OnItemClickListener<Message> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public OnItemClickListener<Message> getItemClickListener() {
        return mItemClickListener;
    }

    public View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public View.OnLongClickListener getOnLongClickListener() {
        return mOnLongClickListener;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public LayerClient getLayerClient() {
        return mLayerClient;
    }

    public void setLayerClient(LayerClient layerClient) {
        mLayerClient = layerClient;
    }

    public IdentityFormatter getIdentityFormatter() {
        return mIdentityFormatter;
    }

    public void setIdentityFormatter(IdentityFormatter identityFormatter) {
        mIdentityFormatter = identityFormatter;
    }

    public DateFormatter getDateFormatter() {
        return mDateFormatter;
    }

    public void setDateFormatter(DateFormatter dateFormatter) {
        mDateFormatter = dateFormatter;
    }
}
