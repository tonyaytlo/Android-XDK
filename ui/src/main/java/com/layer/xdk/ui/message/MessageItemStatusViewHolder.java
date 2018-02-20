package com.layer.xdk.ui.message;


import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiMessageItemStatusBinding;
import com.layer.xdk.ui.message.model.MessageModel;

public class MessageItemStatusViewHolder extends RecyclerView.ViewHolder {

    private final MessageItemStatusViewModel mViewModel;
    private XdkUiMessageItemStatusBinding mBinding;

    public MessageItemStatusViewHolder(ViewGroup parent, MessageItemStatusViewModel viewModel) {
        this(DataBindingUtil.<XdkUiMessageItemStatusBinding>inflate(LayoutInflater.from(parent.getContext()), R.layout.xdk_ui_message_item_status, parent, false), viewModel);
    }

    public MessageItemStatusViewHolder(XdkUiMessageItemStatusBinding binding, MessageItemStatusViewModel viewModel) {
        super(binding.getRoot());
        mBinding = binding;
        mViewModel = viewModel;

        mBinding.setViewModel(viewModel);
    }

    public void bind(MessageModel messageModel) {
        mViewModel.setItem(messageModel);
        mViewModel.update();
    }

}
