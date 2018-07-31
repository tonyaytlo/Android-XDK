package com.layer.xdk.ui.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.util.CircularProgressBar;

/**
 * A view that contains a button along with a circular determinate progress bar. One or
 * the other will be shown at a time based on the calls made. The button can be either a play, pause
 * or broken play button.
 */
public class MediaProgressControlView extends FrameLayout {

    private final AppCompatImageButton mButton;
    private final ProgressBar mProgressBar;

    private @DrawableRes int mPlayButtonResId = R.drawable.xdk_ui_ic_media_play;
    private @DrawableRes int mBrokenButtonResId = R.drawable.xdk_ui_ic_media_broken;
    private @DrawableRes int mPauseButtonResId = R.drawable.xdk_ui_ic_media_pause;
    private @ColorRes int mButtonTintResId;

    public MediaProgressControlView(
            @NonNull Context context) {
        this(context, null);
    }

    public MediaProgressControlView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaProgressControlView(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        mButton = new AppCompatImageButton(context, attrs, defStyleAttr);
        LayoutParams buttonParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TypedValue foregroundValue = new TypedValue();
        context.getTheme()
                .resolveAttribute(R.attr.selectableItemBackgroundBorderless, foregroundValue, true);
        Drawable foregroundDrawable = ContextCompat.getDrawable(context,
                foregroundValue.resourceId);
        ViewCompat.setBackground(mButton, foregroundDrawable);
        addView(mButton, buttonParams);

        mProgressBar = new CircularProgressBar(
                new ContextThemeWrapper(context, R.style.Message_ProgressBar_CircularDeterminate));
        LayoutParams progressBarParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mProgressBar, progressBarParams);
    }

    public void setPlayButton(@DrawableRes int resId) {
        mPlayButtonResId = resId;
    }

    public void setPauseButton(@DrawableRes int resId) {
        mPauseButtonResId = resId;
    }

    @SuppressWarnings("unused")
    public void setBrokenButton(@DrawableRes int resId) {
        mBrokenButtonResId = resId;
    }

    public void setButtonTint(@ColorRes int resId) {
        mButtonTintResId = resId;
    }

    /**
     * Show a play button and hide the progress bar.
     */
    public void showPlayButton() {
        mButton.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mButton.setImageResource(mPlayButtonResId);
        tintButton();
    }

    /**
     * Show a pause button and hide the progress bar.
     */
    public void showPauseButton() {
        mButton.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mButton.setImageResource(mPauseButtonResId);
        tintButton();
    }

    /**
     * Show a broken play button (to signal an error) and hide the progress bar.
     */
    public void showBrokenPlayButton() {
        mButton.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mButton.setImageResource(mBrokenButtonResId);
        tintButton();
    }

    /**
     * Show the progress bar with the give progress [0-100] and hide the button.
     */
    public void showProgressBar(int progress) {
        mButton.setVisibility(GONE);
        mProgressBar.setVisibility(VISIBLE);
        mProgressBar.setIndeterminate(false);
        mProgressBar.setProgress(progress);
    }

    /**
     * Show the progress bar with an indeterminate state and hide the button.
     */
    public void showIndeterminateProgressBar() {
        mButton.setVisibility(GONE);
        mProgressBar.setVisibility(VISIBLE);
        mProgressBar.setIndeterminate(true);
    }

    /**
     * Set a click listener for the button.
     *
     * @param listener listener to use for the button clicks
     */
    public void setButtonOnClickListener(OnClickListener listener) {
        mButton.setOnClickListener(listener);
    }

    /**
     * Update the view based on the playback state.
     *
     * @param state playback state of the media session
     */
    public void update(@PlaybackStateCompat.State int state) {
        switch (state) {
            case PlaybackStateCompat.STATE_PAUSED:
            case PlaybackStateCompat.STATE_STOPPED:
                showPlayButton();
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                showPauseButton();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                showIndeterminateProgressBar();
                break;
            case PlaybackStateCompat.STATE_ERROR:
                showBrokenPlayButton();
                break;
        }
    }

    private void tintButton() {
        if (mButtonTintResId != 0) {
            DrawableCompat.setTint(mButton.getDrawable(), ContextCompat.getColor(getContext(), mButtonTintResId));
        }
    }
}
