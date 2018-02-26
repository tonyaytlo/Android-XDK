package com.layer.xdk.ui.message.container;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

// TODO AND-1242 Should this be at the container level or the item view holder level?
public class MessageContainerHelper {
    private Path mCornerClippingPath = new Path();
    private float mCornerRadius = 0.0f;

    public void setCornerRadius(float cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    public int beforeDispatchDraw(Canvas canvas) {
        if (mCornerRadius > 0) {
            int save = canvas.save();
            canvas.clipPath(mCornerClippingPath);
            return save;
        }
        return 0;
    }

    public void afterDispatchDraw(Canvas canvas, int saveCount) {
        if (mCornerRadius > 0) {
            canvas.restoreToCount(saveCount);
        }
    }

    protected void calculateCornerClippingPath(int width, int height) {
        if (mCornerRadius > 0) {
            RectF r = new RectF(0, 0, width, height);
            Path path = new Path();
            path.addRoundRect(r, mCornerRadius, mCornerRadius, Path.Direction.CW);
            path.close();
            mCornerClippingPath = path;
        }
    }
}
