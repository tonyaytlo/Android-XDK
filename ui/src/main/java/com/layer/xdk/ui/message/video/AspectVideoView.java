package com.layer.xdk.ui.message.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.layer.xdk.ui.util.Log;

/**
 * A {@link VideoView} that maintains a set aspect ratio.
 */
public class AspectVideoView extends VideoView {

    private double mAspectRatio;
    private int mWidth;

    public AspectVideoView(Context context) {
        super(context);
    }

    public AspectVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAspectRatio > 0) {
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
            if (widthSpecMode != MeasureSpec.AT_MOST) {
                if (Log.isLoggable(Log.ERROR)) {
                    Log.e("Only wrap_content should be used for the width of this video view");
                }
                throw new IllegalArgumentException(
                        "Only wrap_content should be used for the width of this video view");
            }

            int width = 0;
            if (mWidth != 0) {
                width = getDefaultSize(mWidth, widthMeasureSpec);
            }
            if (width == 0 || width > widthSpecSize) {
                // Too wide or no width supplied. Set to fill space
                width = widthSpecSize;
            }

            int height = (int) Math.round(width / mAspectRatio);

            if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                // Too tall. Decrease both width and height
                height = heightSpecSize;
                width = (int) Math.round(height * mAspectRatio);
            }

            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    /**
     * Set the aspect ratio and the width of this view. The height will be calculated from these
     * values.
     */
    public void setSize(double aspectRatio, int width) {
        mAspectRatio = aspectRatio;
        mWidth = width;
    }
}
