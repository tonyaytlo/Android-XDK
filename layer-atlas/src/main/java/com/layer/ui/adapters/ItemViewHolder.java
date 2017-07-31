package com.layer.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.layer.ui.style.ItemStyle;
import com.layer.ui.viewmodel.ItemViewModel;

public class ItemViewHolder<ITEM, VIEW_MODEL extends ItemViewModel<ITEM>, BINDING extends ViewDataBinding, STYLE extends ItemStyle>
        extends RecyclerView.ViewHolder {

    protected VIEW_MODEL mViewModel;
    protected STYLE mStyle;
    protected BINDING mBinding;

    public ItemViewHolder(BINDING binding, VIEW_MODEL viewModel) {
        super(binding.getRoot());
        mBinding = binding;
        mViewModel = viewModel;
    }

    public BINDING getBinding() {
        return mBinding;
    }

    @CallSuper
    public void setItem(ITEM item) {
        mViewModel.setItem(item);
    }

    @CallSuper
    public void setEmpty() {

    }

    public ITEM getItem() {
        return mViewModel.getItem();
    }

    public STYLE getStyle() {
        return mStyle;
    }

    @CallSuper
    public void setStyle(STYLE style) {
        mStyle = style;
    }

    public VIEW_MODEL getViewModel() {
        return mViewModel;
    }
}
