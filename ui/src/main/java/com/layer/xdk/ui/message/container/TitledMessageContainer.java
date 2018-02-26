package com.layer.xdk.ui.message.container;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Constraints;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;

import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiTitledMessageContainerMergeBinding;
import com.layer.xdk.ui.message.model.MessageModel;

public class TitledMessageContainer extends MessageConstraintContainer {
    private XdkUiTitledMessageContainerMergeBinding mBinding;

    public TitledMessageContainer(@NonNull Context context) {
        this(context, null, 0);
    }

    public TitledMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitledMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mBinding = XdkUiTitledMessageContainerMergeBinding.inflate(inflater, this, true);
        mBinding.xdkUiTitledMessageContainerTitle.setVisibility(GONE);
        mMessageContainerHelper.setCornerRadius(context.getResources().getDimension(R.dimen.xdk_ui_titled_message_container_corner_radius));
    }

    @Override
    public View inflateMessageView(@LayoutRes int messageViewLayoutId) {
        ViewStub viewStub = mBinding.xdkUiTitledMessageContainerContentView.getViewStub();
        viewStub.setLayoutResource(messageViewLayoutId);
        return viewStub.inflate();
    }

    @Override
    public <T extends MessageModel> void setMessageModel(T model) {
        View messageView = mBinding.xdkUiTitledMessageContainerContentView.getRoot();
        ViewDataBinding messageBinding = DataBindingUtil.getBinding(messageView);
        messageBinding.setVariable(BR.viewModel, model);

        mBinding.setViewModel(model);
        mBinding.getViewModel().addOnPropertyChangedCallback(new HasContentOrMetadataCallback());

        mBinding.executePendingBindings();
        setContentBackground(model);
    }

    @Override
    public <T extends MessageModel> void setContentBackground(T model) {
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.xdk_ui_titled_message_container_background);
        background.setColor(ContextCompat.getColor(getContext(), model.getBackgroundColor()));
        mBinding.xdkUiTitledMessageContainerContentView.getRoot().setBackgroundDrawable(background);
    }

    private class HasContentOrMetadataCallback extends Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId != BR.hasContent && propertyId != BR.hasMetadata) {
                return;
            }
            MessageModel messageModel = (MessageModel) sender;
            View messageRoot = mBinding.xdkUiTitledMessageContainerContentView.getRoot();
            if (propertyId == BR.hasContent) {
                messageRoot.setVisibility(messageModel.getHasContent() ? VISIBLE : GONE);
            } else {
                int minWidth;
                int topMargin = 0;
                if (messageModel.getHasMetadata()) {
                    minWidth = getResources().getDimensionPixelSize(
                            R.dimen.xdk_ui_titled_message_container_min_width);
                    topMargin = getResources().getDimensionPixelOffset(R.dimen.xdk_ui_margin_tiny);
                } else {
                    minWidth = getResources().getDimensionPixelSize(
                            R.dimen.xdk_ui_titled_message_container_min_width_zero);

                }
                messageRoot.setMinimumWidth(minWidth);
                Constraints.LayoutParams layoutParams =
                        (Constraints.LayoutParams) messageRoot.getLayoutParams();
                layoutParams.topMargin = topMargin;
                messageRoot.setLayoutParams(layoutParams);
            }
        }
    }
}
