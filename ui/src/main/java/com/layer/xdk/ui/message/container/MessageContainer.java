package com.layer.xdk.ui.message.container;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;

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

    public abstract <T extends MessageModel> void setMessageModel(T model);

    public abstract <T extends MessageModel> void setContentBackground(T model);
}
