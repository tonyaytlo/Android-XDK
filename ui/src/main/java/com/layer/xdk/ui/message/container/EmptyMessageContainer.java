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
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.model.AbstractMessageModel;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.util.Log;

public class EmptyMessageContainer extends FrameLayout implements MessageContainer {
    protected MessageContainerHelper mMessageContainerHelper;
    private LayoutInflater mInflater;

    public EmptyMessageContainer(@NonNull Context context) {
        this(context, null, 0);
    }

    public EmptyMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyMessageContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMessageContainerHelper = new MessageContainerHelper();
        mMessageContainerHelper.setCornerRadius(context.getResources().getDimension(R.dimen.xdk_ui_standard_message_container_corner_radius));
        mInflater = LayoutInflater.from(context);
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
        ViewDataBinding binding = DataBindingUtil.inflate(mInflater, messageViewLayoutId, this,
                true);
        return binding.getRoot();
    }

    @Override
    public <T extends AbstractMessageModel> void setMessageModel(T model) {
        if (getChildCount() == 0) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.w("No message view set on this container");
            }
            throw new IllegalStateException("No message view set on this container");
        }
        ViewDataBinding messageBinding = DataBindingUtil.getBinding(getChildAt(0));
        messageBinding.setVariable(BR.messageModel, model);

        if (model != null) {
            model.addOnPropertyChangedCallback(new HasContentCallback());
            setContentBackground(model);
        }
    }

    @Override
    public <T extends AbstractMessageModel> void setContentBackground(@NonNull T model) {
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.xdk_ui_standard_message_container_content_background);
        if (background != null) {
            background.setColor(ContextCompat.getColor(getContext(), model.getBackgroundColor()));
        }
        setBackgroundDrawable(background);
    }

    private class HasContentCallback extends Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.hasContent) {
                MessageModel messageModel = (MessageModel) sender;
                View messageRoot = getChildAt(0);
                messageRoot.setVisibility(messageModel.getHasContent() ? VISIBLE : GONE);
            }
        }
    }
}
