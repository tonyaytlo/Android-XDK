package com.layer.xdk.ui.message.adapter2;


import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiMessageItemStatusBinding;
import com.layer.xdk.ui.message.model.AbstractMessageModel;

public class MessageStatusViewHolder extends RecyclerView.ViewHolder {

    private final MessageStatusViewHolderModel mViewModel;
    private XdkUiMessageItemStatusBinding mBinding;

    public MessageStatusViewHolder(ViewGroup parent, MessageStatusViewHolderModel viewModel) {
        this(DataBindingUtil.<XdkUiMessageItemStatusBinding>inflate(LayoutInflater.from(parent.getContext()), R.layout.xdk_ui_message_item_status, parent, false), viewModel);
    }

    public MessageStatusViewHolder(XdkUiMessageItemStatusBinding binding, MessageStatusViewHolderModel viewModel) {
        super(binding.getRoot());
        mBinding = binding;
        mViewModel = viewModel;

        mBinding.setViewModel(viewModel);
    }

    public void bind(AbstractMessageModel messageModel) {
        mViewModel.setItem(messageModel);
        mViewModel.update();
    }

}
