package com.layer.xdk.ui.message.container;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Constraints;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;

import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiTitledMessageContainerBinding;
import com.layer.xdk.ui.message.model.MessageModel;

public class TitledMessageContainer extends ConstraintLayout implements MessageContainer {
    private XdkUiTitledMessageContainerBinding mBinding;
    private MessageContainerHelper mMessageContainerHelper;

    public TitledMessageContainer(@NonNull Context context) {
        this(context, null, 0);
    }

    public TitledMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitledMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mMessageContainerHelper = new MessageContainerHelper();
        mMessageContainerHelper.setCornerRadius(context.getResources()
                .getDimension(R.dimen.xdk_ui_titled_message_container_corner_radius));
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        int saveCount = mMessageContainerHelper.beforeDispatchDraw(canvas);
        super.dispatchDraw(canvas);
        mMessageContainerHelper.afterDispatchDraw(canvas, saveCount);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mMessageContainerHelper.calculateCornerClippingPath(width, height);
    }

    @Override
    public View inflateMessageView(@LayoutRes int messageViewLayoutId) {
        ViewStub viewStub = getBinding().xdkUiTitledMessageContainerContentView.getViewStub();
        viewStub.setLayoutResource(messageViewLayoutId);
        return viewStub.inflate();
    }

    @Override
    public <T extends MessageModel> void setMessageModel(T model) {
        View messageView = getBinding().xdkUiTitledMessageContainerContentView.getRoot();
        ViewDataBinding messageBinding = DataBindingUtil.getBinding(messageView);
        messageBinding.setVariable(BR.messageModel, model);

        getBinding().setMessageModel(model);
        if (model != null) {
            HasContentOrMetadataCallback hasContentOrMetadataCallback =
                    new HasContentOrMetadataCallback();
            model.addOnPropertyChangedCallback(hasContentOrMetadataCallback);
            // Initiate the view properties as this will only be called if the model changes
            hasContentOrMetadataCallback.onPropertyChanged(model, BR._all);
            setContentBackground(model);
        }

        messageBinding.executePendingBindings();
        getBinding().executePendingBindings();
    }

    @Override
    public <T extends MessageModel> void setContentBackground(T model) {
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.xdk_ui_titled_message_container_background);
        if (background != null) {
            background.setColor(ContextCompat.getColor(getContext(), model.getBackgroundColor()));
        }
        getBinding().xdkUiTitledMessageContainerContentView.getRoot().setBackgroundDrawable(background);
    }

    private XdkUiTitledMessageContainerBinding getBinding() {
        if (mBinding == null) {
            mBinding = DataBindingUtil.getBinding(this);
        }
        return mBinding;
    }

    private class HasContentOrMetadataCallback extends Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            MessageModel messageModel = (MessageModel) sender;
            View messageRoot = getBinding().xdkUiTitledMessageContainerContentView.getRoot();
            if (propertyId == BR.hasContent || propertyId == BR._all) {
                messageRoot.setVisibility(messageModel.getHasContent() ? VISIBLE : GONE);
            }
            if (propertyId == BR.hasMetadata || propertyId == BR._all) {
                ConstraintSet set = new ConstraintSet();
                set.clone(TitledMessageContainer.this);

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
                set.constrainMinWidth(messageRoot.getId(), minWidth);
                set.applyTo(TitledMessageContainer.this);
                Constraints.LayoutParams layoutParams =
                        (Constraints.LayoutParams) messageRoot.getLayoutParams();
                layoutParams.topMargin = topMargin;
                messageRoot.setLayoutParams(layoutParams);
            }
        }
    }
}
