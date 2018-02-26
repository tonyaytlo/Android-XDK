package com.layer.xdk.ui.message.container;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Constraints;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;

import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiStandardMessageContainerBinding;
import com.layer.xdk.ui.message.model.MessageModel;

public class StandardMessageContainer extends MessageConstraintContainer {
    private XdkUiStandardMessageContainerBinding mBinding;

    public StandardMessageContainer(@NonNull Context context) {
        this(context, null, 0);
    }

    public StandardMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StandardMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMessageContainerHelper.setCornerRadius(context.getResources().getDimension(R.dimen.xdk_ui_standard_message_container_corner_radius));
    }

    @Override
    public View inflateMessageView(@LayoutRes int messageViewLayoutId) {
        ViewStub viewStub = getBinding().xdkUiStandardMessageContainerContentView.getViewStub();
        viewStub.setLayoutResource(messageViewLayoutId);
        return viewStub.inflate();
    }

    @Override
    public <T extends MessageModel> void setMessageModel(T model) {
        View messageView = getBinding().xdkUiStandardMessageContainerContentView.getRoot();
        ViewDataBinding messageBinding = DataBindingUtil.getBinding(messageView);
        messageBinding.setVariable(BR.messageModel, model);

        getBinding().setMessageModel(model);
        if (model != null) {
            model.addOnPropertyChangedCallback(new HasContentOrMetadataCallback());
            setContentBackground(model);
        }

        getBinding().executePendingBindings();
    }

    @Override
    public <T extends MessageModel> void setContentBackground(T model) {
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.xdk_ui_standard_message_container_content_background);
        if (background != null) {
            background.setColor(ContextCompat.getColor(getContext(), model.getBackgroundColor()));
        }
        getBinding().xdkUiStandardMessageContainerContentView.getRoot().setBackgroundDrawable(background);
    }

    private XdkUiStandardMessageContainerBinding getBinding() {
        if (mBinding == null) {
            mBinding = DataBindingUtil.getBinding(this);
        }
        return mBinding;
    }

    private class HasContentOrMetadataCallback extends Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId != BR.hasContent && propertyId != BR.hasMetadata) {
                return;
            }
            MessageModel messageModel = (MessageModel) sender;
            View messageRoot = getBinding().xdkUiStandardMessageContainerContentView.getRoot();
            if (propertyId == BR.hasContent) {
                messageRoot.setVisibility(messageModel.getHasContent() ? VISIBLE : GONE);
            } else {
                int minWidth;
                int bottomMargin = 0;
                if (messageModel.getHasMetadata()) {
                    minWidth = getResources().getDimensionPixelSize(
                            R.dimen.xdk_ui_standard_message_container_min_width);
                    bottomMargin = getResources().getDimensionPixelOffset(
                            R.dimen.xdk_ui_margin_tiny);
                } else {
                    minWidth = getResources().getDimensionPixelSize(
                            R.dimen.xdk_ui_standard_message_container_min_width_zero);

                }
                messageRoot.setMinimumWidth(minWidth);
                Constraints.LayoutParams layoutParams =
                        (Constraints.LayoutParams) messageRoot.getLayoutParams();
                layoutParams.bottomMargin = bottomMargin;
                messageRoot.setLayoutParams(layoutParams);
            }
        }
    }
}
