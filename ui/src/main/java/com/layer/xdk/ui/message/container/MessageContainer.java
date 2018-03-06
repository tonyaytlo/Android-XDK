package com.layer.xdk.ui.message.container;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.AttrRes;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.view.View;

import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.message.model.MessageModel;

public abstract class MessageContainer extends ConstraintLayout {
    private Path mCornerClippingPath = new Path();
    private float mCornerRadius = 0.0f;

    public MessageContainer(@NonNull Context context) {
        this(context, null, 0);
    }

    public MessageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(mCornerClippingPath);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        RectF r = new RectF(0, 0, width, height);
        Path path = new Path();
        path.addRoundRect(r, mCornerRadius, mCornerRadius, Path.Direction.CW);
        path.close();
        mCornerClippingPath = path;
    }

    protected void setCornerRadius(float radius) {
        mCornerRadius = radius;
    }

    public abstract View inflateMessageView(@LayoutRes int messageViewLayoutId);

    /**
     * @return view object that contains the message view
     */
    protected abstract View getMessageView();

    /**
     * Return the minimum width, in pixels, that this container should be.
     *
     * @param hasMetadata if the associated model has metadata or not
     * @return minimum width in pixels
     */
    protected abstract int getContainerMinimumWidth(boolean hasMetadata);

    public abstract <T extends MessageModel> void setContentBackground(T model);

    @CallSuper
    public <T extends MessageModel> void setMessageModel(T model) {
        ViewDataBinding messageBinding = DataBindingUtil.getBinding(getMessageView());
        messageBinding.setVariable(BR.messageModel, model);
        if (model != null) {
            HasContentOrMetadataCallback hasContentOrMetadataCallback =
                    new HasContentOrMetadataCallback();
            model.addOnPropertyChangedCallback(hasContentOrMetadataCallback);
            // Initiate the view properties as this will only be called if the model changes
            hasContentOrMetadataCallback.onPropertyChanged(model, BR._all);
            setContentBackground(model);
        }

        messageBinding.executePendingBindings();
    }

    private class HasContentOrMetadataCallback extends Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            MessageModel messageModel = (MessageModel) sender;
            View messageView = getMessageView();
            if (propertyId == BR.hasContent || propertyId == BR._all) {
                messageView.setVisibility(messageModel.getHasContent() ? VISIBLE : GONE);
            }
            if (propertyId == BR.hasMetadata || propertyId == BR._all) {
                ConstraintSet set = new ConstraintSet();
                set.clone(MessageContainer.this);
                int minWidth = getContainerMinimumWidth(messageModel.getHasMetadata());
                set.constrainMinWidth(messageView.getId(), minWidth);
                set.applyTo(MessageContainer.this);
            }
        }
    }
}
