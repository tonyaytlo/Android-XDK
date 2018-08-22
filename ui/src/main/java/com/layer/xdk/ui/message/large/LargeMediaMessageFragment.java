package com.layer.xdk.ui.message.large;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.Observable;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.FragmentLargeMediaMessageBinding;
import com.layer.xdk.ui.media.LargeMediaViewModel;
import com.layer.xdk.ui.media.MediaPlaybackService;
import com.layer.xdk.ui.media.PlaybackSavedState;
import com.layer.xdk.ui.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Shows a full screen media player used to play a media file from a message part. Use the provided
 * ARG_ constants to create a bundle for the arguments.
 */
public class LargeMediaMessageFragment extends Fragment {
    public static final String ARG_MESSAGE_PART_ID = "message_part_id";
    public static final String ARG_SOURCE_URI = "source_uri";
    public static final String ARG_PREVIEW_URL = "preview_url";
    public static final String ARG_MEDIA_WIDTH = "media_width";
    public static final String ARG_MEDIA_HEIGHT = "media_height";
    public static final String ARG_MEDIA_ASPECT_RATIO = "media_aspect_ratio";
    public static final String ARG_ORDERED_METADATA = "ordered_metadata";
    public static final String ARG_IS_VIDEO = "is_video";

    private static final String EXTRA_RESUME_PLAYBACK_RESTORATION = "resumePlaybackOnRestoration";

    private FragmentLargeMediaMessageBinding mBinding;

    private LargeMediaViewModel mViewModel;
    private final Observable.OnPropertyChangedCallback mSeekBarProgressUpdate =
            new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    mBinding.seekBar.setProgress(((ObservableInt) sender).get());
                }
            };

    private boolean mResumePlaybackOnRestoration;

    private MediaPlaybackService.MediaPlaybackBinder mMediaPlaybackServiceBinder;
    private SurfaceHolder mSurfaceHolder;
    private boolean mServiceBound;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBound = true;
            mMediaPlaybackServiceBinder = (MediaPlaybackService.MediaPlaybackBinder) service;
            if (mSurfaceHolder != null) {
                mMediaPlaybackServiceBinder.setSurfaceHolder(mSurfaceHolder);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
            mMediaPlaybackServiceBinder.setSurfaceHolder(null);
        }
    };

    public LargeMediaMessageFragment() {
        // Default constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(LargeMediaViewModel.class);
        if (getArguments() != null && savedInstanceState == null) {
            mViewModel.setExtras(getArguments());
        }
        if (savedInstanceState != null
                && savedInstanceState.getBoolean(EXTRA_RESUME_PLAYBACK_RESTORATION)) {
            mViewModel.resumeIfNeeded();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mBinding = FragmentLargeMediaMessageBinding.inflate(inflater, container, false);

        mBinding.elapsedTime.setText(DateUtils.formatElapsedTime(mViewModel.mElapsedSeconds.get()));
        setUpControlButton();
        startSeekBarProgressUpdate();
        setSeekBarListener();
        observePlaybackState();
        observeDownloadProgress();
        if (mViewModel.isUsingVideo()) {
            setUpVideo();
        }

        mBinding.setViewModel(mViewModel);

        return mBinding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.pauseIfNeeded();
        if (getActivity() != null && getActivity().isChangingConfigurations()) {
            mResumePlaybackOnRestoration = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceBound && getActivity() != null) {
            getActivity().unbindService(mServiceConnection);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_RESUME_PLAYBACK_RESTORATION, mResumePlaybackOnRestoration);
    }

    private void setUpControlButton() {
        mBinding.controlButton.setPlayButton(R.drawable.xdk_ui_fullscreen_play);
        mBinding.controlButton.setPauseButton(R.drawable.xdk_ui_fullscreen_pause);
        mBinding.controlButton.setButtonTint(R.color.xdk_ui_large_media_message_control_color);
        mBinding.controlButton.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.onControlButtonPressed();
            }
        });
    }

    private void stopSeekBarProgressUpdate() {
        mViewModel.mElapsedSeconds.removeOnPropertyChangedCallback(mSeekBarProgressUpdate);
    }

    private void startSeekBarProgressUpdate() {
        mViewModel.mElapsedSeconds.addOnPropertyChangedCallback(mSeekBarProgressUpdate);
    }

    private void observePlaybackState() {
        mViewModel.getPlaybackState().observe(this,
                new Observer<PlaybackSavedState>() {
                    @Override
                    public void onChanged(@Nullable PlaybackSavedState state) {
                        if (state == null) {
                            mBinding.controlButton.update(PlaybackStateCompat.STATE_STOPPED);
                        } else {
                            mBinding.controlButton.update(state.mPlaybackState);
                            mBinding.seekBar.setSecondaryProgress(
                                    (int) TimeUnit.MILLISECONDS.toSeconds(state.mBufferedPosition));
                        }
                    }
                });
    }

    private void observeDownloadProgress() {
        mViewModel.getDownloadProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer progress) {
                if (progress != null && progress == 100) {
                    // Show final state
                    mBinding.seekBar.setEnabled(true);
                    mBinding.controlButton.showPlayButton();
                } else {
                    // Show progress state
                    mBinding.seekBar.setEnabled(false);
                    if (progress == null) {
                        progress = 0;
                    }
                    mBinding.controlButton.showProgressBar(progress);
                }
            }
        });
    }

    private void setSeekBarListener() {
        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private boolean mTrackingTouch;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mTrackingTouch && fromUser) {
                    mBinding.elapsedTime.setText(DateUtils.formatElapsedTime(progress));
                } else if (!mTrackingTouch && !fromUser) {
                    mBinding.elapsedTime.setText(DateUtils.formatElapsedTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mTrackingTouch = true;
                mViewModel.mElapsedSeconds.removeOnPropertyChangedCallback(mSeekBarProgressUpdate);
                stopSeekBarProgressUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mViewModel.seekTo(seekBar.getProgress());
                mTrackingTouch = false;
                startSeekBarProgressUpdate();
            }
        });
    }

    private void setUpVideo() {
        if (getActivity() == null) {
            if (Log.isLoggable(Log.ERROR)) {
                Log.e("Unable to initialize video as activity doesn't exist");
            }
            throw new IllegalStateException("Unable to initialize video as activity doesn't exist");
        }

        Intent serviceIntent = new Intent(getActivity(), MediaPlaybackService.class);
        getActivity().bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        mBinding.video.setSize(mViewModel.mVideoAspectRatio, mViewModel.mVideoWidth);
        mBinding.video.getHolder().addCallback(new VideoSurfaceCallback());
    }

    private class VideoSurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (mMediaPlaybackServiceBinder != null) {
                mMediaPlaybackServiceBinder.setSurfaceHolder(holder);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            if (mMediaPlaybackServiceBinder != null) {
                mMediaPlaybackServiceBinder.setSurfaceHolder(null);
            }
        }
    }
}
