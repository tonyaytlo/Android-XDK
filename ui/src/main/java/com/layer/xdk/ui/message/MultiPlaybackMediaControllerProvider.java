package com.layer.xdk.ui.message;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.layer.xdk.ui.util.Log;

import javax.inject.Inject;

/**
 * Provides a {@link MediaControllerCompat} for use with handling media playback from several
 * different message parts. Uses a {@link MultiPlaybackCallback} instance.
 */
public class MultiPlaybackMediaControllerProvider implements MediaControllerProvider, LifecycleObserver {

    private Context mAppContext;

    private MediaPlayer mMediaPlayer;
    private MediaControllerCompat mMediaController;
    private MediaSessionCompat mMediaSession;
    private boolean mLifecycleObserverAdded;

    @Inject
    public MultiPlaybackMediaControllerProvider(Context appContext) {
        mAppContext = appContext;
    }

    /**
     * Called when the lifecycle owner is paused. Pauses playback if it is currently happening
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (mMediaController != null && mMediaController.getPlaybackState() != null) {
            switch (mMediaController.getPlaybackState().getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                case PlaybackStateCompat.STATE_BUFFERING:
                    mMediaController.getTransportControls().pause();
                    break;
            }
        }
    }

    /**
     * Called when the lifecycle owner is destroyed. Releases objects associated with media playback
     * if they have been created.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (mMediaPlayer != null) {
            if (Log.isLoggable(Log.VERBOSE)) {
                Log.v("Releasing media player");
            }
            mMediaPlayer.release();
        }
        if (mMediaSession != null) {
            if (Log.isLoggable(Log.VERBOSE)) {
                Log.v("Releasing media session");
            }
            mMediaSession.release();
        }
        mMediaPlayer = null;
        mMediaSession = null;
        mMediaController = null;
    }

    /**
     * Creates a {@link MediaPlayer}, {@link MediaSessionCompat} and {@link MediaControllerCompat}
     * if none have been created. This uses the {@link MultiPlaybackCallback} to handle playback
     * between multiple messages with the same player/session/controller.
     *
     * @return the controller instance managed by this provider
     */
    @NonNull
    public MediaControllerCompat getMediaController() {
        if (mMediaController == null) {

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaSession = new MediaSessionCompat(mAppContext,
                    "MessageItemsListSession");
            mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mMediaSession.setMediaButtonReceiver(null);

            mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY
                            | PlaybackStateCompat.ACTION_PLAY_PAUSE)
                    .build());

            mMediaSession.setCallback(new MultiPlaybackCallback(mAppContext, mMediaPlayer, mMediaSession));

            mMediaController = new MediaControllerCompat(mAppContext, mMediaSession);
        }

        if (!mLifecycleObserverAdded && Log.isLoggable(Log.WARN)) {
            Log.w("MediaControllerProvider is not registered as a lifecycle observer. Please"
                    + " register the containing view model.");
        }

        return mMediaController;
    }

    @Override
    public void addLifecycleObserver(LifecycleOwner lifecycleOwner) {
        mLifecycleObserverAdded = true;
        lifecycleOwner.getLifecycle().addObserver(this);
    }
}
