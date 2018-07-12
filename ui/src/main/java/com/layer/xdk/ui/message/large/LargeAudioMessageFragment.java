package com.layer.xdk.ui.message.large;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.Observable;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.databinding.FragmentLargeAudioMessageBinding;
import com.layer.xdk.ui.media.LargeAudioViewModel;
import com.layer.xdk.ui.media.PlaybackSavedState;

/**
 * Shows a full screen media player used to play an audio file from a message part. Use the provided
 * ARG_ constants to create a bundle for the arguments.
 */
public class LargeAudioMessageFragment extends Fragment {
    public static final String ARG_MESSAGE_PART_ID = "message_part_id";
    public static final String ARG_SOURCE_URI = "source_uri";
    public static final String ARG_PREVIEW_URL = "preview_url";
    public static final String ARG_IMAGE_WIDTH = "image_width";
    public static final String ARG_IMAGE_HEIGHT = "image_height";
    public static final String ARG_ORDERED_METADATA = "ordered_metadata";

    private static final String EXTRA_RESUME_PLAYBACK_RESTORATION = "resumePlaybackOnRestoration";

    private FragmentLargeAudioMessageBinding mBinding;

    private LargeAudioViewModel mViewModel;
    private final Observable.OnPropertyChangedCallback mSeekBarProgressUpdate =
            new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    mBinding.seekBar.setProgress(((ObservableInt) sender).get());
                }
            };

    private boolean mResumePlaybackOnRestoration;

    public LargeAudioMessageFragment() {
        // Default constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(LargeAudioViewModel.class);
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
        mBinding = FragmentLargeAudioMessageBinding.inflate(inflater, container, false);

        mBinding.elapsedTime.setText(DateUtils.formatElapsedTime(mViewModel.mElapsedSeconds.get()));
        setUpControlButton();
        startSeekBarProgressUpdate();
        setSeekBarListener();
        observePlaybackState();
        observeDownloadProgress();

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_RESUME_PLAYBACK_RESTORATION, mResumePlaybackOnRestoration);
    }

    private void setUpControlButton() {
        mBinding.controlButton.setPlayButton(R.drawable.xdk_ui_fullscreen_play);
        mBinding.controlButton.setPauseButton(R.drawable.xdk_ui_fullscreen_pause);
        mBinding.controlButton.setButtonTint(R.color.xdk_ui_large_audio_message_control_color);
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
}
