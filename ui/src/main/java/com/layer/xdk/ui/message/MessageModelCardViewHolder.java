package com.layer.xdk.ui.message;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.avatar.AvatarViewModelImpl;
import com.layer.xdk.ui.databinding.XdkUiMessageItemCardNewBinding;
import com.layer.xdk.ui.message.container.MessageContainer;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.model.MessageModelManager;

public class MessageModelCardViewHolder extends RecyclerView.ViewHolder {

    private final XdkUiMessageItemCardNewBinding mBinding;
    private final MessageModelCardViewModel mViewModel;
    private final LayoutInflater mLayoutInflater;

    public MessageModelCardViewHolder(ViewGroup parent, MessageModelCardViewModel viewModel,
            MessageModelManager modelRegistry) {
        this(DataBindingUtil.<XdkUiMessageItemCardNewBinding>inflate(LayoutInflater.from(parent.getContext()), R.layout.xdk_ui_message_item_card_new, parent, false), viewModel, modelRegistry);
    }

    public MessageModelCardViewHolder(XdkUiMessageItemCardNewBinding binding, MessageModelCardViewModel viewModel, MessageModelManager modelRegistry) {
        super(binding.getRoot());
        mBinding = binding;
        mViewModel = viewModel;
        mLayoutInflater = LayoutInflater.from(binding.getRoot().getContext());

        getBinding().avatar.init(new AvatarViewModelImpl(viewModel.getImageCacheWrapper()),
                viewModel.getIdentityFormatter());


        getBinding().currentUserAvatar.init(new AvatarViewModelImpl(viewModel.getImageCacheWrapper()),
                viewModel.getIdentityFormatter());

//        getBinding().set(viewModel);
        getBinding().setMessageModel(viewModel);
//        getBinding().messageViewer.setMessageModelManager(modelRegistry);
//        getBinding().messageViewer.setOnClickListener(viewModel.getOnClickListener());
//        getBinding().messageViewer.setOnLongClickListener(viewModel.getOnLongClickListener());

        getBinding().getRoot().setClickable(true);
        getBinding().getRoot().setOnClickListener(viewModel.getOnClickListener());
        getBinding().getRoot().setOnLongClickListener(viewModel.getOnLongClickListener());
    }

    public void bind(MessageModel messageModel, MessageCluster messageCluster, int position, int recipientStatusPosition, int parentWidth) {
        getViewModel().setItem(messageModel);

        // TODO AND-1242 This is super ugly. Rework
        ViewGroup containerRoot = getBinding().getRoot().findViewById(R.id.message_view_container);
        ((MessageContainer) containerRoot.getChildAt(0)).setMessageModel(messageModel);

        getViewModel().update(messageCluster, position, recipientStatusPosition);
    }

    private XdkUiMessageItemCardNewBinding getBinding() {
        return mBinding;
    }

    private MessageModelCardViewModel getViewModel() {
        return mViewModel;
    }

}
