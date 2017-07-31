package com.layer.ui.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

public abstract class ItemViewModel<ITEM> extends BaseObservable {

    protected ITEM mItem;

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
}
