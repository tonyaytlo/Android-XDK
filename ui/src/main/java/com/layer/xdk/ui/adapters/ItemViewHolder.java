package com.layer.xdk.ui.adapters;

import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;

import com.layer.xdk.ui.style.ItemStyle;
import com.layer.xdk.ui.viewmodel.ItemViewModel;

public class ItemViewHolder<ITEM, VIEW_MODEL extends ItemViewModel<ITEM>, BINDING extends ViewDataBinding, STYLE extends ItemStyle>
        extends RecyclerView.ViewHolder {

    private VIEW_MODEL mViewModel;
    private STYLE mStyle;
    protected BINDING mBinding;

    public ItemViewHolder(BINDING binding, VIEW_MODEL viewModel) {
        super(binding.getRoot());
        mBinding = binding;
        mViewModel = viewModel;
    }

    public BINDING getBinding() {
        return mBinding;
    }

    protected void setBinding(BINDING binding) {
        mBinding = binding;
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
