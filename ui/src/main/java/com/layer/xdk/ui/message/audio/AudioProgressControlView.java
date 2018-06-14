package com.layer.xdk.ui.message.audio;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.util.CircularProgressBar;

/**
 * A view that contains a button along with a circular determinate progress bar. One or
 * the other will be shown at a time based on the calls made. The button can be either a play, pause
 * or broken play button.
 */
public class AudioProgressControlView extends FrameLayout {

    private final AppCompatImageButton mButton;
    private final ProgressBar mProgressBar;

    public AudioProgressControlView(
            @NonNull Context context) {
        this(context, null);
    }

    public AudioProgressControlView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioProgressControlView(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int controlSize = getResources().getDimensionPixelSize(
                R.dimen.xdk_ui_audio_message_control_size);

        mButton = new AppCompatImageButton(context, attrs, defStyleAttr);
        LayoutParams buttonParams = new LayoutParams(controlSize, controlSize);
        TypedValue foregroundValue = new TypedValue();
        context.getTheme()
                .resolveAttribute(R.attr.selectableItemBackgroundBorderless, foregroundValue, true);
        Drawable foregroundDrawable = ContextCompat.getDrawable(context,
                foregroundValue.resourceId);
        ViewCompat.setBackground(mButton, foregroundDrawable);
        addView(mButton, buttonParams);

        mProgressBar = new CircularProgressBar(
                new ContextThemeWrapper(context, R.style.Message_ProgressBar_CircularDeterminate));
        LayoutParams progressBarParams = new LayoutParams(controlSize, controlSize);
        addView(mProgressBar, progressBarParams);
    }

    /**
     * Show a play button and hide the progress bar.
     */
    void showPlayButton() {
        mButton.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mButton.setImageResource(R.drawable.xdk_ui_audio_play);
    }

    /**
     * Show a pause button and hide the progress bar.
     */
    void showPauseButton() {
        mButton.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mButton.setImageResource(R.drawable.xdk_ui_audio_pause);
    }

    /**
     * Show a broken play button (to signal an error) and hide the progress bar.
     */
    void showBrokenPlayButton() {
        mButton.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mButton.setImageResource(R.drawable.xdk_ui_audio_broken);
    }

    /**
     * Show the progress bar with the give progress [0-100] and hide the button.
     */
    void showProgressBar(int progress) {
        mButton.setVisibility(GONE);
        mProgressBar.setVisibility(VISIBLE);
        mProgressBar.setProgress(progress);
    }

    /**
     * Set a click listener for the button.
     *
     * @param listener listener to use for the button clicks
     */
    void setButtonOnClickListener(OnClickListener listener) {
        mButton.setOnClickListener(listener);
    }
}
