package com.layer.xdk.ui.adapters;

import android.databinding.ViewDataBinding;

import com.layer.sdk.query.Queryable;
import com.layer.xdk.ui.style.ItemStyle;
import com.layer.xdk.ui.viewmodel.ItemViewModel;

public interface ItemAdapter<T extends Queryable, VT extends ItemViewModel<T>, BINDING extends ViewDataBinding, STYLE extends ItemStyle> {
    Integer getPosition(T item);

    Integer getPosition(T item, int lastPosition);

    T getItem(int position);

    T getItem(ItemViewHolder<T, VT, BINDING, STYLE> viewHolder);
}
