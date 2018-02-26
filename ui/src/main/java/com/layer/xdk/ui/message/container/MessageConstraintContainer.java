package com.layer.xdk.ui.message.container;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

public abstract class MessageConstraintContainer extends ConstraintLayout implements MessageContainer {
    protected MessageContainerHelper mMessageContainerHelper;

    public MessageConstraintContainer(@NonNull Context context) {
        this(context, null, 0);
    }

    public MessageConstraintContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageConstraintContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMessageContainerHelper = new MessageContainerHelper();
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
}
