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
import com.layer.xdk.ui.message.view.MessageView;

public abstract class MessageContainer extends ConstraintLayout {
    private Path mCornerClippingPath;
    private float mCornerRadius;

    public MessageContainer(@NonNull Context context) {
        this(context, null, 0);
    }

    public MessageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }

    public MessageContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCornerRadius = 0;
    }

    @Deprecated
    public abstract void setMessageView(MessageView view);

    // TODO AND-1242 make abstract
    public View inflateMessageView(@LayoutRes int messageViewLayoutId) {
        return null;
    }

    public abstract <T extends MessageModel> void setMessageModel(T model);

    public float getCornerRadius() {
        return mCornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    protected Path getCornerClippingPath() {
        return mCornerClippingPath;
    }

    protected void setCornerClippingPath(Path cornerClippingPath) {
        mCornerClippingPath = cornerClippingPath;
    }

    protected abstract <T extends MessageModel> void setContentBackground(T model);

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mCornerRadius > 0) {
            int save = canvas.save();
            canvas.clipPath(getCornerClippingPath());
            super.dispatchDraw(canvas);
            canvas.restoreToCount(save);
        } else {
            super.dispatchDraw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        calculateCornerClippingPath(width, height, oldWidth, oldHeight);

    }

    protected void calculateCornerClippingPath(int width, int height, int oldWidth, int oldHeight) {
        if (mCornerRadius > 0) {
            RectF r = new RectF(0, 0, width, height);
            Path path = new Path();
            path.addRoundRect(r, mCornerRadius, mCornerRadius, Path.Direction.CW);
            path.close();
            setCornerClippingPath(path);
        }
    }

}
