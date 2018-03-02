package com.layer.xdk.ui.message.adapter2;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.avatar.AvatarViewModelImpl;
import com.layer.xdk.ui.databinding.XdkUiMessageItemDefaultBinding;
import com.layer.xdk.ui.message.MessageCluster;
import com.layer.xdk.ui.message.container.MessageContainer;
import com.layer.xdk.ui.message.model.AbstractMessageModel;
import com.layer.xdk.ui.message.model.MessageModelManager;

public class MessageDefaultViewHolder extends RecyclerView.ViewHolder {

    private final XdkUiMessageItemDefaultBinding mBinding;
    private final MessageDefaultViewHolderModel mViewModel;
    // Cache this so we know not to re-set the bias on the constraint layout
    private Boolean mCurrentlyMyMessage;

    public MessageDefaultViewHolder(ViewGroup parent, MessageDefaultViewHolderModel viewModel,
            MessageModelManager modelRegistry) {
        this(DataBindingUtil.<XdkUiMessageItemDefaultBinding>inflate(LayoutInflater.from(parent.getContext()), R.layout.xdk_ui_message_item_default, parent, false), viewModel, modelRegistry);
    }

    public MessageDefaultViewHolder(XdkUiMessageItemDefaultBinding binding, final MessageDefaultViewHolderModel viewModel, MessageModelManager modelRegistry) {
        super(binding.getRoot());
        mBinding = binding;
        mViewModel = viewModel;

        getBinding().avatar.init(new AvatarViewModelImpl(viewModel.getImageCacheWrapper()),
                viewModel.getIdentityFormatter());

        getBinding().currentUserAvatar.init(new AvatarViewModelImpl(viewModel.getImageCacheWrapper()),
                viewModel.getIdentityFormatter());

        getBinding().setViewHolderModel(viewModel);

        getBinding().getRoot().setClickable(true);
        getBinding().getRoot().setOnClickListener(viewModel.getOnClickListener());
        getBinding().getRoot().setOnLongClickListener(viewModel.getOnLongClickListener());
        getBinding().messageViewStub.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, final View inflated) {
                getViewModel().addOnPropertyChangedCallback(new AlphaAndBiasObserver(inflated));
            }
        });
    }

    public void bind(AbstractMessageModel messageModel, MessageCluster messageCluster, int position, int recipientStatusPosition, int parentWidth) {
        getViewModel().setItem(messageModel);

        MessageContainer messageContainer =
                (MessageContainer) getBinding().messageViewStub.getRoot();
        if (messageContainer != null) {
            messageContainer.setMessageModel(messageModel);
        }

        getViewModel().update(messageCluster, position, recipientStatusPosition);
    }

    private XdkUiMessageItemDefaultBinding getBinding() {
        return mBinding;
    }

    private MessageDefaultViewHolderModel getViewModel() {
        return mViewModel;
    }

    public View inflateViewContainer(int containerLayoutId) {
        getBinding().messageViewStub.getViewStub().setLayoutResource(containerLayoutId);
        return getBinding().messageViewStub.getViewStub().inflate();
    }

    private class AlphaAndBiasObserver extends Observable.OnPropertyChangedCallback {
        private final View mInflated;

        public AlphaAndBiasObserver(View inflated) {
            mInflated = inflated;
        }

        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.messageCellAlpha || propertyId == BR._all) {
                mInflated.setAlpha(getViewModel().getMessageCellAlpha());
            }
            if (propertyId == BR._all || (propertyId == BR.myMessage
                    && (mCurrentlyMyMessage == null || getViewModel().isMyMessage() != mCurrentlyMyMessage))) {
                mCurrentlyMyMessage = getViewModel().isMyMessage();
                ConstraintSet set = new ConstraintSet();
                ConstraintLayout parent =
                        (ConstraintLayout) mInflated.getParent();
                set.clone(parent);
                set.setHorizontalBias(mInflated.getId(),
                        getViewModel().isMyMessage() ? 1.0f : 0.0f);
                set.applyTo(parent);
            }
        }
    }
}
