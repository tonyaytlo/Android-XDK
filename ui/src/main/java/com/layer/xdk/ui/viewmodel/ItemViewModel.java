package com.layer.xdk.ui.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.layer.sdk.query.Queryable;
import com.layer.xdk.ui.identity.IdentityFormatter;
import com.layer.xdk.ui.recyclerview.OnItemClickListener;
import com.layer.xdk.ui.recyclerview.OnItemLongClickListener;

import javax.inject.Inject;

public class ItemViewModel<ITEM extends Queryable> extends BaseObservable {

    private ITEM mItem;
    private OnItemClickListener<ITEM> mItemClickListener;
    private OnItemLongClickListener<ITEM> mItemLongClickListener;
    private IdentityFormatter mIdentityFormatter;

    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;

    @Inject
    public ItemViewModel(IdentityFormatter identityFormatter) {
        mIdentityFormatter = identityFormatter;

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
                if (mItemLongClickListener != null) {
                    return mItemLongClickListener.onItemLongClick(mItem);
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

    public OnItemClickListener<ITEM> getItemClickListener() {
        return mItemClickListener;
    }

    public void setItemClickListener(OnItemClickListener<ITEM> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public OnItemLongClickListener<ITEM> getItemLongClickListener() {
        return mItemLongClickListener;
    }

    public void setItemLongClickListener(OnItemLongClickListener<ITEM> itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
    }

    public View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public View.OnLongClickListener getOnLongClickListener() {
        return mOnLongClickListener;
    }

    public IdentityFormatter getIdentityFormatter() {
        return mIdentityFormatter;
    }
}