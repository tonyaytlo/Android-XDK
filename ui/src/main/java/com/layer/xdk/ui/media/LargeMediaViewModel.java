package com.layer.xdk.ui.media;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.layer.sdk.LayerClient;
import com.layer.sdk.listeners.LayerProgressListener;
import com.layer.sdk.messaging.MessagePart;
import com.layer.xdk.ui.XdkUiDependencyManager;
import com.layer.xdk.ui.message.image.cache.ImageCacheWrapper;
import com.layer.xdk.ui.message.image.cache.ImageRequestParameters;
import com.layer.xdk.ui.message.large.LargeMediaMessageFragment;
import com.layer.xdk.ui.repository.FetchMessagePartTask;
import com.layer.xdk.ui.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * ViewModel that handles a large audio/video message and playback.
 */
public class LargeMediaViewModel extends AndroidViewModel {

    @Inject
    ImageCacheWrapper mImageCacheWrapper;

    @Inject
    MultiPlaybackMediaControllerProvider mMediaControllerProvider;

    @Inject
    LayerClient mLayerClient;

    private String mMessagePartId;
    private final MutableLiveData<MessagePart> mMessagePart = new MutableLiveData<>();
    private Uri mSourceUri;
    public final ObservableField<ImageRequestParameters> mImageRequestParameters =
            new ObservableField<>();
    public final ObservableInt mTotalSeconds = new ObservableInt();
    public final ObservableInt mElapsedSeconds = new ObservableInt();
    public final ObservableField<String> mPrimaryMetadata = new ObservableField<>();
    public final ObservableField<String> mSecondaryMetadata = new ObservableField<>();
    public int mVideoWidth;
    public int mVideoHeight;
    public Double mVideoAspectRatio;

    private MediaControllerCompat mMediaController;
    private final MutableLiveData<PlaybackSavedState> mPlaybackState = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDownloadProgress = new MutableLiveData<>();
    private LayerProgressListener mProgressListener;

    private boolean mShouldResume;
    private boolean mUsingVideo;

    private final Observer<MediaControllerCompat> mControllerObserver =
            new Observer<MediaControllerCompat>() {

                @Override
                public void onChanged(@Nullable MediaControllerCompat controller) {
                    if (controller != null) {
                        mMediaControllerProvider.getMediaController().removeObserver(this);
                        mMediaController = controller;
                        mMediaController.registerCallback(mPlaybackStateCallback);
                        if (canPlay()) {
                            prepareControllerFromUri();
                            mMediaController.getTransportControls().play();
                        }
                    }
                }
            };

    private final MediaControllerCompat.Callback mPlaybackStateCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
                    PlaybackSavedState state = null;
                    // Pull the state for this message in the bundle
                    if (playbackState != null && playbackState.getExtras() != null) {
                        state = playbackState.getExtras().getParcelable(mMessagePartId);
                    }

                    mPlaybackState.setValue(state);
                    if (state != null) {
                        mTotalSeconds.set((int) TimeUnit.MILLISECONDS.toSeconds(state.mDuration));
                        mElapsedSeconds.set(
                                (int) TimeUnit.MILLISECONDS.toSeconds(state.mPlaybackPosition));
                    }
                }
            };

    private final Observer<MessagePart> mMessagePartObserver = new Observer<MessagePart>() {
        @Override
        public void onChanged(@Nullable MessagePart messagePart) {
            if (messagePart != null && messagePart.getTransferStatus() != MessagePart.TransferStatus.COMPLETE) {
                // Report download progress while this part is downloading
                mProgressListener = new LayerProgressListener() {
                    @Override
                    public void onProgressStart(MessagePart messagePart,
                            Operation operation) {
                    }

                    @Override
                    public void onProgressUpdate(MessagePart messagePart, Operation operation,
                            long transferredBytes) {
                        mDownloadProgress.postValue((int) Math.round((double) transferredBytes / messagePart.getSize() * 100));
                    }

                    @Override
                    public void onProgressComplete(MessagePart messagePart,
                            Operation operation) {
                        mDownloadProgress.postValue(100);
                        // Set up and start playback
                        mSourceUri = messagePart.getFileUri(getApplication());
                        prepareControllerFromUri();
                        mMediaController.getTransportControls().play();
                    }

                    @Override
                    public void onProgressError(MessagePart messagePart, Operation operation,
                            Throwable throwable) {
                    }
                };
                mLayerClient.registerProgressListener(messagePart, mProgressListener);
            }
        }
    };

    public LargeMediaViewModel(@NonNull Application application) {
        super(application);
        XdkUiDependencyManager.INSTANCE.getXdkUiComponent().inject(this);
        // Normally this view model should not observe LiveData but this is a one-time observation
        // to retrieve a controller from the provider and is not dependant on a lifecycle
        mMediaControllerProvider.getMediaController().observeForever(mControllerObserver);

        mMessagePart.observeForever(mMessagePartObserver);
    }

    /**
     * Clean up the media controller when this is no longer used
     */
    @Override
    protected void onCleared() {
        mMediaControllerProvider.getMediaController().removeObserver(mControllerObserver);
        mMessagePart.removeObserver(mMessagePartObserver);
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mPlaybackStateCallback);
        }
        mMediaControllerProvider.onDestroy();
        if (mProgressListener != null && mMessagePart.getValue() != null) {
            mLayerClient.unregisterProgressListener(mMessagePart.getValue(), mProgressListener);
        }
    }

    /**
     * @return current playback state of the media session
     */
    public LiveData<PlaybackSavedState> getPlaybackState() {
        return mPlaybackState;
    }

    /**
     * @return the injected image cache wrapper
     */
    public ImageCacheWrapper getImageCacheWrapper() {
        return mImageCacheWrapper;
    }

    /**
     * @return current download progress of the part from 0 to 100
     */
    public LiveData<Integer> getDownloadProgress() {
        return mDownloadProgress;
    }

    /**
     * Play or pause depending on the current state. Should be called when the play/pause button
     * is pressed.
     */
    public void onControlButtonPressed() {
        if (mMediaController == null) {
            if (Log.isLoggable(Log.WARN)) {
                Log.w("Control button pressed before controller was assigned.");
            }
        }
        if (canPlay()) {
            PlaybackStateCompat state = mMediaController.getPlaybackState();
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                case PlaybackStateCompat.STATE_BUFFERING:
                    mMediaController.getTransportControls().pause();
                    break;
                default:
                    mMediaController.getTransportControls().play();
            }
        }
    }

    /**
     * Pause the playback if it is playing. Also determines if the {@link #resumeIfNeeded()} should
     * resume if media is playing.
     */
    public void pauseIfNeeded() {
        if (mMediaController != null && canPlay()) {
            mShouldResume = mMediaController.getPlaybackState().getState()
                    == PlaybackStateCompat.STATE_PLAYING;
            mMediaController.getTransportControls().pause();
        }
    }

    /**
     * Resume playback if media was playing when {@link #pauseIfNeeded()} was called.
     */
    public void resumeIfNeeded() {
        if (mMediaController != null && mShouldResume) {
            mMediaController.getTransportControls().play();
        }
    }

    /**
     * @return true if the media represented by this view model is a video, false otherwise
     */
    public boolean isUsingVideo() {
        return mUsingVideo;
    }

    /**
     * Set the extras passed in the activity/fragment creation to initialize member variables.
     *
     * @param extras bundle that was passed in the intent/arguments for starting the activity or
     *               fragment
     */
    public void setExtras(@NonNull Bundle extras) {
        mMessagePartId = extras.getString(LargeMediaMessageFragment.ARG_MESSAGE_PART_ID);
        String rawSourceUri = extras.getString(LargeMediaMessageFragment.ARG_SOURCE_URI);
        if (rawSourceUri != null) {
            mSourceUri = Uri.parse(rawSourceUri);
            mDownloadProgress.setValue(100);
        } else {
            mDownloadProgress.setValue(0);
            new FetchMessagePartTask(mLayerClient, Uri.parse(mMessagePartId), mMessagePart).execute();
        }

        setImageFromExtras(extras);
        setMetadataFromExtras(extras);
        setVideoDataFromExtras(extras);
    }

    /**
     * Seek to a position in the playback.
     *
     * @param positionSeconds position in seconds to move to
     */
    public void seekTo(int positionSeconds) {
        if (mMediaController != null && canPlay()) {
            mMediaController.getTransportControls().seekTo(
                    TimeUnit.SECONDS.toMillis(positionSeconds));
        }
    }

    /**
     * Start an activity to handle video playback. If no activity can be resolved then log a message
     * saying as such.
     */
    public void launchExternalVideoActivity() {
        if (mSourceUri == null) {
            if (Log.isLoggable(Log.INFO)) {
                Log.i("No URI available for video click event. Ignoring.");
            }
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(mSourceUri);
        if (mSourceUri.getScheme().equals("content")) {
            // Set to a generic video type since the internal file name does not have the
            // correct type extension.
            intent.setType("video/*");
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplication().getPackageManager()) != null) {
            getApplication().startActivity(intent);
        } else if (Log.isLoggable(Log.INFO)) {
            Log.i("Cannot resolve intent for external video playback. Intent: " + intent);
        }
    }

    /**
     * @return true if playback can be started, false otherwise
     */
    private boolean canPlay() {
        return mSourceUri != null;
    }

    /**
     * Prep the control with the uri for playback. Required before playback can be started.
     */
    private void prepareControllerFromUri() {
        Bundle extras = new Bundle(1);
        extras.putString(MultiPlaybackCallback.EXTRA_KEY_ACTIVE_MESSAGE_PART_ID, mMessagePartId);
        mMediaController.getTransportControls().prepareFromUri(mSourceUri, extras);
    }

    private void setImageFromExtras(@NonNull Bundle extras) {
        String previewUrl = extras.getString(LargeMediaMessageFragment.ARG_PREVIEW_URL);
        int width = extras.getInt(LargeMediaMessageFragment.ARG_MEDIA_WIDTH);
        int height = extras.getInt(LargeMediaMessageFragment.ARG_MEDIA_HEIGHT);
        ImageRequestParameters.Builder builder = new ImageRequestParameters.Builder();
        if (previewUrl != null) {
            builder.uri(Uri.parse(previewUrl));
            if (width > 0 && height > 0) {
                builder.resize(width, height);
            }
        }
        mImageRequestParameters.set(builder.build());
    }

    private void setMetadataFromExtras(@NonNull Bundle extras) {
        ArrayList<String> metadata = extras.getStringArrayList(
                LargeMediaMessageFragment.ARG_ORDERED_METADATA);
        StringBuilder secondaryBuilder = new StringBuilder();
        if (metadata != null) {
            for (String s : metadata) {
                if (mPrimaryMetadata.get() == null) {
                    mPrimaryMetadata.set(s);
                    continue;
                }
                if (secondaryBuilder.length() > 0) {
                    secondaryBuilder.append('\n');
                }
                secondaryBuilder.append(s);
            }
            mSecondaryMetadata.set(secondaryBuilder.toString());
        }
    }

    private void setVideoDataFromExtras(Bundle extras) {
        mUsingVideo = extras.getBoolean(LargeMediaMessageFragment.ARG_IS_VIDEO);
        mVideoHeight = extras.getInt(LargeMediaMessageFragment.ARG_MEDIA_HEIGHT);
        mVideoWidth = extras.getInt(LargeMediaMessageFragment.ARG_MEDIA_WIDTH);
        mVideoAspectRatio = extras.getDouble(LargeMediaMessageFragment.ARG_MEDIA_ASPECT_RATIO);
    }
}
