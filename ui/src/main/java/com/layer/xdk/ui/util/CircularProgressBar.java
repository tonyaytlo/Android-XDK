package com.layer.xdk.ui.util;

import android.content.Context;
import android.support.v4.widget.CircularProgressDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.layer.xdk.ui.R;

/**
 * A circular progress bar used for displaying determinate state. Uses a
 * {@link CircularProgressDrawable}.
 */
public class CircularProgressBar extends ProgressBar {

    private CircularProgressDrawable mProgressDrawable;

    public CircularProgressBar(Context context) {
        this(context, null);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mProgressDrawable = new CircularProgressDrawable(context);
        mProgressDrawable.setStyle(CircularProgressDrawable.DEFAULT);
        mProgressDrawable.setColorSchemeColors(getResources().getColor(R.color.xdk_ui_color_primary_blue));
        setProgressDrawable(mProgressDrawable);
        setIndeterminate(false);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);

        if (mProgressDrawable != null) {
            // The circular progress drawable starts at 3 o'clock so we need to subtract .25 to
            // start it at 12 o'clock
            mProgressDrawable.setStartEndTrim(-0.25f, progress/100f - 0.25f);
        }
    }
}
