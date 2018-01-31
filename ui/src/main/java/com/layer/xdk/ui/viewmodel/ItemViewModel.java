package com.layer.xdk.ui.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.layer.sdk.LayerClient;
import com.layer.sdk.query.Queryable;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.identity.IdentityFormatterImpl;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.util.DateFormatter;
import com.layer.xdk.ui.util.DateFormatterImpl;

public class ItemViewModel<ITEM extends Queryable> extends BaseObservable {

    private Context mContext;
    private LayerClient mLayerClient;

    private ITEM mItem;
    private OnItemClickListener<ITEM> mItemClickListener;
    private IdentityFormatter mIdentityFormatter;
    private DateFormatter mDateFormatter;

    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;

    public ItemViewModel(Context context, LayerClient layerClient) {
        mContext = context;
        mLayerClient = layerClient;

        mIdentityFormatter = new IdentityFormatterImpl(context);
        mDateFormatter = new DateFormatterImpl(context);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(mItem);
                }
            }
        };

        mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemClickListener!=null) {
                    return mItemClickListener.onItemLongClick(mItem);
                } else {
                    return false;
                }
            }
        };
    }

    @Bindable
    public ITEM getItem() {
        return mItem;
    }

    public void setItem(ITEM item) {
        mItem = item;
    }

    public void setEmpty() {
        mItem = null;
    }

    public void setItemClickListener(OnItemClickListener<ITEM> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public OnItemClickListener<ITEM> getItemClickListener() {
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