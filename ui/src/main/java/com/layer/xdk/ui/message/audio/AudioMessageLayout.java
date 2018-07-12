package com.layer.xdk.ui.message.audio;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ViewStubProxy;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.layer.xdk.ui.BR;
import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.XdkUiAudioMessageViewBinding;
import com.layer.xdk.ui.media.MediaControllerProvider;
import com.layer.xdk.ui.media.MultiPlaybackCallback;
import com.layer.xdk.ui.media.PlaybackSavedState;
import com.layer.xdk.ui.message.container.StandardMessageContainer;
import com.layer.xdk.ui.message.image.cache.ImageRequestParameters;
import com.layer.xdk.ui.message.view.MediaPlayerMessageView;
import com.layer.xdk.ui.message.view.MessageViewHelper;
import com.layer.xdk.ui.util.Log;

/**
 * Layout to use for audio messages.
 */
public class AudioMessageLayout extends LinearLayout implements MediaPlayerMessageView {

    private MessageViewHelper mMessageViewHelper;
    private AudioProgressControlView mControlView;
    private XdkUiAudioMessageViewBinding mBinding;

    private AudioMessageModel mModel;
    private String mAudioPartId;
    private Observable.OnPropertyChangedCallback mProgressCallback;

    private MediaControllerCompat.Callback mControllerCallback;
    private MediaControllerCompat mMediaController;

    public AudioMessageLayout(Context context) {
        this(context, null, 0);
    }

    public AudioMessageLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioMessageLayout(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        mMessageViewHelper = new MessageViewHelper(context);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessageViewHelper.performAction();
            }
        });

        initializeProgressCallback();
        initializeControllerCallback();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mMediaController != null) {
            mMediaController.registerCallback(mControllerCallback);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // Need to un-register here to avoid memory leaks
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mControllerCallback);
        }
    }

    /**
     * Create the property change callback to be used when downloading a message part. This will
     * refresh the progress bar that indicates download progress.
     */
    private void initializeProgressCallback() {
        mProgressCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.downloadProgress) {
                    refreshDownloadingState();
                }
            }
        };
    }

    /**
     * Create the controller callback that refreshes the view when the state changes.
     */
    private void initializeControllerCallback() {
        mControllerCallback = new MediaControllerCompat.Callback() {
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                refreshState(state);
            }
        };
    }

    public void setMessageModel(final AudioMessageModel model) {
        if (mModel != null && mProgressCallback != null) {
            // Unregister the progress callback since a new model is being bound
            mModel.removeOnPropertyChangedCallback(mProgressCallback);
        }
        mModel = model;
        initControlButtonIfNeeded();

        mMessageViewHelper.setMessageModel(model);
        if (model != null) {
            mAudioPartId = model.getAudioPartId().toString();

            refreshDownloadingState();

            refreshState();

            initializePreviewImage(model.getPreviewRequestParameters());
            model.addOnPropertyChangedCallback(mProgressCallback);
        }
    }

    /**
     * Show the downloading progress with the current progress value.
     */
    private void refreshDownloadingState() {
        mControlView.showProgressBar(mModel.getDownloadProgress());
    }

    /**
     * Refresh the state of the controls (play/pause/progress).
     */
    private void refreshState() {
        if (mMediaController != null) {
            PlaybackStateCompat playbackState = mMediaController.getPlaybackState();
            refreshState(playbackState);
        }
    }

    /**
     * Refresh the state of the controls (play/pause/progress).
     *
     * @param playbackState state to read the values/extras from
     */
    private void refreshState(@Nullable PlaybackStateCompat playbackState) {
        if (mModel == null) {
            if (Log.isLoggable(Log.VERBOSE)) {
                Log.v("Audio model hasn't been set when state refresh was requested. Ignoring "
                        + "for now.");
            }
            return;
        }
        // No state when the part is downloading so ensure the progress bar is hidden and return
        if (mModel.isDownloadingSourcePart()) {
            hideProgressBar();
            return;
        }

        PlaybackSavedState state = null;
        // Pull the state for this message in the bundle
        if (playbackState != null && playbackState.getExtras() != null) {
            state = playbackState.getExtras().getParcelable(mAudioPartId);
        }

        if (state == null) {
            // No state so it should be set to stopped and no progress
            mControlView.update(PlaybackStateCompat.STATE_STOPPED);
            hideProgressBar();
        } else {
            mControlView.update(state.mPlaybackState);
            switch (state.mPlaybackState) {
                case PlaybackStateCompat.STATE_PAUSED:
                case PlaybackStateCompat.STATE_STOPPED:
                    int position = state.mPlaybackPosition;
                    int duration = state.mDuration;
                    showPlayingProgressBar(duration, position);
                    break;
                case PlaybackStateCompat.STATE_PLAYING:
                    position = state.mPlaybackPosition;
                    duration = state.mDuration;
                    showPlayingProgressBar(duration, position);
                    break;
                case PlaybackStateCompat.STATE_BUFFERING:
                    showBufferingProgressBar();
                    break;
                case PlaybackStateCompat.STATE_ERROR:
                    hideProgressBar();
                    break;
            }
        }
    }

    private void showBufferingProgressBar() {
        getBinding().progressBar.setVisibility(VISIBLE);
        getBinding().progressBar.setIndeterminate(true);
    }

    private void hideProgressBar() {
        getBinding().progressBar.setVisibility(GONE);
    }

    private void showPlayingProgressBar(int duration, long position) {
        getBinding().progressBar.setVisibility(VISIBLE);
        getBinding().progressBar.setIndeterminate(false);
        getBinding().progressBar.setMax(duration);
        getBinding().progressBar.setProgress((int) position);
    }

    private XdkUiAudioMessageViewBinding getBinding() {
        if (mBinding == null) {
            mBinding = DataBindingUtil.getBinding(this);
        }
        return mBinding;
    }

    @NonNull
    private ViewStubProxy getControlButtonHolder() {
        if (getParent() instanceof StandardMessageContainer) {
            return ((StandardMessageContainer) getParent()).getRightMetadataView();
        } else {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Audio Message view expects to be wrapped in a "
                        + "standard message container");
            }
            throw new UnsupportedOperationException("Audio Message view expects to be wrapped "
                    + "in a standard message container");
        }
    }

    /**
     * Inflates the layout in the standard container, finds the button used for play/pause and sets
     * a click listener on it.
     */
    private void initControlButtonIfNeeded() {
        if (mControlView == null) {
            ViewStubProxy controlButtonHolder = getControlButtonHolder();
            if (!controlButtonHolder.isInflated()) {
                controlButtonHolder.getViewStub().setLayoutResource(
                        R.layout.xdk_ui_audio_message_control_view);
                controlButtonHolder.getViewStub().inflate();
            }
            mControlView = ((AudioProgressControlView) controlButtonHolder.getRoot());

            mControlView.setButtonOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMediaController == null) {
                        if (Log.isLoggable(Log.WARN)) {
                            Log.w("Still awaiting a media controller. Ignoring user action");
                        }
                        return;
                    }
                    PlaybackStateCompat state = mMediaController.getPlaybackState();
                    switch (state.getState()) {
                        case PlaybackStateCompat.STATE_PLAYING:
                        case PlaybackStateCompat.STATE_BUFFERING:
                            mMediaController.getTransportControls().pause();

                            if (state.getExtras() != null && mAudioPartId.equals(state.getExtras().getString(MultiPlaybackCallback.EXTRA_KEY_ACTIVE_MESSAGE_PART_ID))) {
                                // This was a pause for the current media so don't call play()
                                return;
                            }
                            break;
                    }

                    Bundle extras = new Bundle(1);
                    extras.putString(MultiPlaybackCallback.EXTRA_KEY_ACTIVE_MESSAGE_PART_ID,
                            mAudioPartId);
                    mMediaController.getTransportControls().prepareFromUri(mModel.getSourceUri(),
                            extras);
                    mMediaController.getTransportControls().play();
                }
            });
        }
    }

    @Override
    public void setMediaControllerProvider(final MediaControllerProvider provider) {
        provider.getMediaController().observeForever(
                new Observer<MediaControllerCompat>() {
                    @Override
                    public void onChanged(@Nullable MediaControllerCompat controller) {
                        if (controller != null) {
                            mMediaController = controller;
                            // Register the callback in case this view was attached to the window
                            // before we got this instance (attach check requires API 19).
                            mMediaController.registerCallback(mControllerCallback);
                            refreshState();
                            provider.getMediaController().removeObserver(this);
                        }
                    }
                });
    }

    /**
     * Set up the preview image width/height.
     *
     * @param requestParams parameters to use for the preview image request
     */
    private void initializePreviewImage(ImageRequestParameters requestParams) {
        if (requestParams == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = getBinding().preview.getLayoutParams();
        int width, height;
        if (requestParams.getUri() != null || requestParams.getUrl() != null) {
            width = (requestParams.getTargetWidth() > 0 ? requestParams.getTargetWidth()
                    : ViewGroup.LayoutParams.WRAP_CONTENT);
            height = (requestParams.getTargetHeight() > 0 ? requestParams.getTargetHeight()
                    : ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            // Use the vector drawable here since we can't load it through the image cache
            getBinding().preview.setImageResource(R.drawable.xdk_ui_file_audio);
            width = getResources().getDimensionPixelSize(R.dimen.xdk_ui_audio_message_default_image_width);
            height = getResources().getDimensionPixelSize(R.dimen.xdk_ui_audio_message_default_image_height);
        }
        layoutParams.width = width;
        layoutParams.height = height;
        getBinding().preview.setLayoutParams(layoutParams);
    }
}
